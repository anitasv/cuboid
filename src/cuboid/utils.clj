(ns cuboid.utils)

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
