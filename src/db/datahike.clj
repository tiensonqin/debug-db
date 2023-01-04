(ns db.datahike
  (:require [datahike.api :as d]))

(def cfg {:store {:backend :file :path "/tmp/example-1"}
          :schema-flexibility :read})

(d/create-database cfg)
(def conn (d/connect cfg))

(def schema [{:db/ident :block/uuid
              :db/valueType :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/unique :db.unique/identity }
             {:db/ident :block/parent
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/one
              :db/index true}
             {:db/ident :block/left
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/one
              :db/index true}
             {:db/ident :block/collapsed?
              :db/valueType :db.type/boolean
              :db/cardinality :db.cardinality/one
              :db/index true}
             {:db/ident :block/format
              :db/valueType :db.type/keyword
              :db/cardinality :db.cardinality/one}
             {:db/ident :block/page
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/one
              :db/index true}
             {:db/ident :block/refs
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/many}
             {:db/ident :block/path-refs
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/many}
             {:db/ident :block/tags
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/many}
             {:db/ident :block/alias
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/many}
             {:db/ident :block/name
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :block/original-name
              :db/valueType :db.type/string
              :db/unique :db.unique/identity
              :db/cardinality :db.cardinality/one}
             {:db/ident :block/namespace
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/one}
             {:db/ident :block/macros
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/many}
             {:db/ident :block/file
              :db/valueType :db.type/ref
              :db/cardinality :db.cardinality/one}
             {:db/ident :file/path
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/unique :db.unique/identity}])
(d/transact conn schema)

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

(comment
  (d/transact conn [[:db.fn/retractEntity 103]]))
