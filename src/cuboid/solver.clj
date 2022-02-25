(ns cuboid.solver
  (:require [cuboid.lagged-fib :refer [fib-seq]]))

; What dimension cuboids are we working with!
(def ^:const dims 3)

(defn seg-vol 
  "Length of a line segment, defined using [start, end]"
  [s]
  (- (s 1) (s 0)))

(defn cuboid-vol
  "Volume of an axis parallel cuboid, defined as vector of segments for each axes"
   [cuboid]
  (->> cuboid 
       (map seg-vol) 
       (apply *))) ; product of length of all sides

(defn phased-vol 
  "Volume of a cuboid that either lives in current world or nether-world!"
  [{phase :phase cuboid :cuboid}]
  (let [cv (cuboid-vol cuboid)]
      (if phase cv (- cv))))

(defn seg-intersect 
  "Intersection of two line segments. If no intersection returns nil"
  [s1 s2]
  (if (> (s1 0) (s2 0))
    (seg-intersect s2 s1)
    (if (>= (s2 0) (s1 1))
      nil
      (if (<= (s2 1) (s1 1))
        s2
        [(s2 0) (s1 1)]))))

(defn- nil-conj 
  "private: Reducer used to implement nil-combine below!"
  [v a]
  (if (nil? a)
    (reduced nil)
    (conj v a)))

(defn nil-combine 
  "Takes a lazy collection, returns nil if any element is nil
   otherwise return vector of the elements in the collection.
   It short circuits when the first nil element is encountered"
  [coll]
  (reduce nil-conj [] coll))

(defn cuboid-intersect 
  "Returns the cuboid that is result of intersection of two cuboids.
   If no intersection returns nil"
  [c1 c2]
  (nil-combine (map seg-intersect c1 c2)))

(defn ph-intersect 
  "Intersecting a phased-cuboid with a real cuboid returns another phased-cuboid
   of opposite sign. If no intersection returns nil"
  [phased cuboid]
  (let [icuboid (cuboid-intersect (phased :cuboid) cuboid)]
    (if (nil? icuboid)
      nil
      {:phase (not (phased :phase)) :cuboid icuboid})))

(defn add-cuboid 
  "Adds a new cuboid to a list of phased-cuboids (space). 
   Adds itself as a positive phased cuboid, and adds it's own
   intersection to all existing phased-cuboids"
  [space cuboid]
  (->> space
       (map #(ph-intersect % cuboid)) ; intersect with all 
       (filter some?) ; take only valid intersections
       (reduce conj space) ; add these intersections to space
       (#(conj % {:phase true :cuboid cuboid})))) ; add new cuboid

(defn make-cuboid [coll]
  (let [[pos delta] (partition dims coll)
        pos-mod (map #(mod % 10000) pos)
        del-mod (map #(+ 1 (mod % 399)) delta)]
    (->> (map vector pos-mod del-mod)
         (map (fn [[p d]] [p (+ p d)])))))

(defn cuboid-seq
  "Infinite lazy sequence of cuboids generated using lagged fibonacci
   numbers"
  []
  (->> (fib-seq)
       (partition (* 2 dims))
       (map make-cuboid)))

(defn solve 
  "Given number of cuboids return the total volume occupied by them"
  [num-cuboids]
  (->> (cuboid-seq)
       (take num-cuboids)
       (reduce add-cuboid [])
       (map phased-vol)
       (reduce +)))