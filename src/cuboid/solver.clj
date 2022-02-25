(ns cuboid.solver
  (:require [cuboid.utils :refer [nil-combine]]
            [cuboid.random-cuboids :refer [cuboid-seq]]))

(defn seg-vol 
  "Length of a line segment"
  [[start, end]]
  (- end start))

(defn cuboid-vol
  "Volume of an axis parallel cuboid, defined as vector of segments for each axes"
   [cuboid]
  (->> cuboid 
       (map seg-vol) 
       (apply *))) ; product of length of all sides

(defn phased-vol
  "Volume of a cuboid that either lives in current world or nether-world!"
  [{:keys [phase cuboid]}]
  (cond-> (cuboid-vol cuboid)
    (not phase) (-)))
  
(defn seg-intersect 
  "Intersection of two line segments. If no intersection returns nil"
  [[a-start a-end] [b-start b-end]]
  (let [c-start (max a-start b-start)
        c-end (min a-end b-end)]
    (when (< c-start c-end)
      [c-start c-end])))
  
(defn cuboid-intersect 
  "Returns the cuboid that is result of intersection of two cuboids.
   If no intersection returns nil"
  [c1 c2]
  (-> (map seg-intersect c1 c2)
      (nil-combine))) ; drop if any intersection is nil

(defn ph-intersect
  "Intersecting a phased-cuboid with a real cuboid returns another phased-cuboid
   of opposite sign. If no intersection returns nil"
  [{phase :phase cuboid-1 :cuboid} cuboid-2]
  (some-> (cuboid-intersect cuboid-1 cuboid-2)
          (#(do {:phase (not phase) :cuboid %}))))

(defn add-cuboid 
  "Adds a new cuboid to a list of phased-cuboids (space). 
   Adds itself as a positive phased cuboid, and adds it's own
   intersection to all existing phased-cuboids"
  [space cuboid]
  (->> space
       (map #(ph-intersect % cuboid)) ; intersect with all 
       (filter some?) ; take only valid intersections
       (into space) ; add these intersections to space
       (#(conj % {:phase true :cuboid cuboid})))) ; add new cuboid

(defn solve 
  "Given number of cuboids return the total volume occupied by them"
  [num-cuboids]
  (->> (cuboid-seq)
       (take num-cuboids)
       (reduce add-cuboid [])
       (map phased-vol)
       (reduce +)))