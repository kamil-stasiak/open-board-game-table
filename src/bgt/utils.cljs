(ns bgt.utils)

(defn drop-nth [n coll]
  (keep-indexed #(if (not= %1 n) %2) coll))

(defn add-nth [n element coll]
  (concat (subvec coll 0 n) [element] (subvec coll n)))

(comment
  (add-nth 0 "x" [{:a "aaa"}]))
