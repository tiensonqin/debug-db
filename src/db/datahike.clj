(ns db.datahike
  (:require [datahike.api :as d]))

(def cfg {:store {:backend :file :path "/tmp/example"}})

(d/create-database cfg)
(def conn (d/connect cfg))
(d/transact conn [{:db/ident :name
                   :db/valueType :db.type/string
                   :db/cardinality :db.cardinality/one }
                  {:db/ident :age
                   :db/valueType :db.type/long
                   :db/cardinality :db.cardinality/one }])

;; 500ms
(time
  (do
    (d/transact conn
               (for [i (range 1000)]
                 {:name (str i)
                  :age (long (rand-int 100))}))
    nil))

;; "Elapsed time: 353406.290542 msecs"
(time
  (do
    (d/transact conn
                (for [i (range 1000000)]
                  {:name (str i)
                   :age (long (rand-int 100))}))
    nil))

"Elapsed time: 6974.389083 msecs"
(time
  (def result
    (d/q '[:find ?e ?n ?a
           :where
           [?e :name ?n]
           [?e :age ?a]]
      @conn)))
