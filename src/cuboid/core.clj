(ns cuboid.core
  (:require [cuboid.solver :refer [solve]])
  (:gen-class))

(defn -main
  "Prints solution given number of cubes as first argument"
  [& args]
  (println (solve (Integer/parseInt (first args)))))
