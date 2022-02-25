(ns cuboid.random-cuboids
  (:require [cuboid.lagged-fib :refer [fib-seq]]))

; What dimension cuboids are we working with!
(def ^:const dims 3)

(defn- make-cuboid 
  "Given 6 random numbers generate a cuboid "
  [coll]
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