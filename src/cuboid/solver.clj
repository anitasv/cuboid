(ns cuboid.solver
  (:require [cuboid.lagged-fib :refer [fib-seq]]))

; What dimension cubes are we working with!
(def ^:const dims 3)

(defn seg-vol 
  "Length of a line segment, defined using [start, end]"
  [s]
  (- (s 1) (s 0)))

(defn cube-vol
  "Volume of an axis parallel cuboid, defined as vector of segments for each axes"
   [cube]
  (->> cube 
       (map seg-vol) 
       (apply *))) ; product of length of all sides

(defn phased-vol 
  "Volume of a cuboid that either lives in current world or nether-world!"
  [{phase :phase cube :cube}]
  (let [cv (cube-vol cube)]
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

(defn cube-intersect 
  "Returns the cuboid that is result of intersection of two cuboids.
   If no intersection returns nil"
  [c1 c2]
  (nil-combine (map seg-intersect c1 c2)))

(defn ph-intersect 
  "Intersecting a phased-cube with a real cube returns another phased-cube
   of opposite sign. If no intersection returns nil"
  [phased cube]
  (let [icube (cube-intersect (phased :cube) cube)]
    (if (nil? icube)
      nil
      {:phase (not (phased :phase)) :cube icube})))

(defn add-cube 
  "Adds a new cube to a list of phased-cubes (space). 
   When adding a new cube itself is added as a positive
  phased cube, and all intersections are added with previous
  phased cubes"
  [space cube]
  (->> space
       (map #(ph-intersect % cube)) ; intersect with all 
       (filter some?) ; take only valid intersections
       (reduce conj space) ; add these intersections to space
       (#(conj % {:phase true :cube cube})))) ; add new cube

(defn make-cube [coll]
  (let [[pos delta] (partition dims coll)
        pos-mod (map #(mod % 10000) pos)
        del-mod (map #(+ 1 (mod % 399)) delta)]
    (->> (map vector pos-mod del-mod)
         (map (fn [[p d]] [p (+ p d)])))))

(defn cube-seq 
  "Infinite lazy sequence of all cubes generated using lagged fibonacci
   numbers"
  
  ([]
   (cube-seq (fib-seq)))
  
  ([lfib]
   (->> lfib
       (partition (* 2 dims))
       (map make-cube))))

(defn solve 
  "Given number of cubes return the total volume occupied by them"
  [num-cubes]
  (->> (cube-seq)
       (take num-cubes)
       (reduce add-cube [])
       (map phased-vol)
       (reduce +)))