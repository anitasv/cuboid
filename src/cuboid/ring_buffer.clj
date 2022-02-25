(ns cuboid.ring-buffer
  (:refer-clojure :exclude [get]))

(defn make
  "Makes a ring buffer, with constant random access (almost)"
  [coll]
  {:index 0 :array (vec coll)})

(defn add
  "Adds an element to the ring buffer"
  [x cycle]
  (let [index (:index cycle)
        arr (:array cycle)]
    {:index (mod (inc index) (count arr))
     :array (assoc arr index x)}))

(defn get
  "Get n-th element in the ring buffer"
  [cycle pos]
  (let [index (:index cycle)
        arr (:array cycle)
        actual-pos (mod (+ index pos) (count arr))]
    (arr actual-pos)))
