(ns cuboid.solver
  (:require [cuboid.utils :refer [nil-combine]]
            [cuboid.random-cuboids :refer [cuboid-seq]]))

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

(defn cuboid-intersect 
  "Returns the cuboid that is result of intersection of two cuboids.
   If no intersection returns nil"
  [c1 c2]
  (nil-combine (map seg-intersect c1 c2)))

(defn ph-intersect 
  "Intersecting a phased-cuboid with a real cuboid returns another phased-cuboid
   of opposite sign. If no intersection returns nil"
  [{phase :phase cuboid-1 :cuboid} cuboid-2]
  (let [icuboid (cuboid-intersect cuboid-1 cuboid-2)]
    (if (nil? icuboid)
      nil
      {:phase (not phase) :cuboid icuboid})))

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

(defn solve 
  "Given number of cuboids return the total volume occupied by them"
  [num-cuboids]
  (->> (cuboid-seq)
       (take num-cuboids)
       (reduce add-cuboid [])
       (map phased-vol)
       (reduce +)))