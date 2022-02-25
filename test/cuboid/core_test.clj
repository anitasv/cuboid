(ns cuboid.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [cuboid.solver :refer [cuboid-seq solve]]
            [cuboid.lagged-fib :refer [fib-seq]]))

(deftest c1-test
  (testing "Checking if first cube has correct params"
    (is (=
         [[7, (+ 7 94)] [53, (+ 53 369)],[183, (+ 183 56)]]
         (first (cuboid-seq))))))

(deftest c2-test
  (testing "Checking if second cube has correct params"
    (is (=
         [[2383, (+ 2383 42)] [3563, (+ 3563 212)],[5079, (+ 5079 344)]]
         (second (cuboid-seq))))))

(deftest solve-100
  (testing "Checking if sum of first 100 cubes have correct volume"
    (is (=
         723581599
         (solve 100)))))


(deftest lagged-fib-100
  (testing "Checking if lagged fib implementation is right"
    (is (=
         197673
         (nth (fib-seq) 100)))))

