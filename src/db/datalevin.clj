(ns db.datalevin
  (:require [datalevin.core :as d]))

;; Define an optional schema.
;; Note that pre-defined schema is optional, as Datalevin does schema-on-write.
;; However, attributes requiring special handling need to be defined in schema,
;; e.g. many cardinality, uniqueness constraint, reference type, and so on.
(def schema {:name {:db/valueType :db.type/string
                    :db/cardinality :db.cardinality/one}
             ;; :db/valueType is optional, if unspecified, the attribute will be
             ;; treated as EDN blobs, and may not be optimal for range queries
             :age {:db/valueType :db.type/long
                   :db/cardinality :db.cardinality/one}})

;; Create DB on disk and connect to it, assume write permission to create given dir
(def conn (d/get-conn "/tmp/datalevin/mydb" schema))
;; or if you have a Datalevin server running on myhost with default port 8898
;; (def conn (d/get-conn "dtlv://myname:mypasswd@myhost/mydb" schema))

;; Transact some data
;; Notice that :nation is not defined in schema, so it will be treated as an EDN blob

(time
  (d/transact conn
              (for [i (range 1000)]
                {:name (str i)
                 :age (long (rand-int 100))})))

;; "Elapsed time: 18317.73825 msecs"
(time
  (do
    (d/transact conn
                (for [i (range 1000000)]
                  {:name (str i)
                   :age (long (rand-int 100))}))
    nil))

;; "Elapsed time: 2482.922916 msecs"
(time
  (def result
    (d/q '[:find ?e ?n ?a
           :where
           [?e :name ?n]
           [?e :age ?a]]
      @conn)))
