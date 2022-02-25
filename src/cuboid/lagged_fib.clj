(ns cuboid.lagged-fib
  (:require [cuboid.ring-buffer :as cycle]))

(defn- lagged-fib-head
  "private: Equation that works for 1 <= k <= 55"
  [k]
  (->
   (let [t1 100003
         t2 (* 200003 k)
         k3 (* (* k k) k)
         t3 (* 300007 k3)]
     (+ (- t1 t2) t3))
   (mod 1000000)))

(defn fib-seq
  "Lazy sequence that generates lagged fibonacci numbers."
  ([]
   (fib-seq (->> (range 1 56)
                    (map lagged-fib-head)
                    (cycle/make))))

  ([cycle]
   (lazy-seq (cons (cycle/get cycle 0)
                   (fib-seq
                    (->  (+ (cycle/get cycle 0) (cycle/get cycle 31))
                         (mod 1000000)
                         (cycle/add cycle)))))))