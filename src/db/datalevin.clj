(ns db.datalevin
  (:require [datalevin.core :as d]))

;; Define an optional schema.
;; Note that pre-defined schema is optional, as Datalevin does schema-on-write.
;; However, attributes requiring special handling need to be defined in schema,
;; e.g. many cardinality, uniqueness constraint, reference type, and so on.
(def schema
  {:block/uuid {:db/valueType :db.type/uuid
                :db/cardinality :db.cardinality/one
                :db/unique :db.unique/identity }
   :block/parent {:db/valueType :db.type/ref
                  :db/cardinality :db.cardinality/one
                  :db/index true}
   :block/left {:db/valueType :db.type/ref
                :db/cardinality :db.cardinality/one
                :db/index true}
   :block/collapsed? {:db/valueType :db.type/boolean
                      :db/cardinality :db.cardinality/one
                      :db/index true}
   :block/format {:db/valueType :db.type/keyword
                  :db/cardinality :db.cardinality/one}
   :block/page {:db/valueType :db.type/ref
                :db/cardinality :db.cardinality/one
                :db/index true}
   :block/refs {:db/valueType :db.type/ref
                :db/cardinality :db.cardinality/many}
   :block/path-refs {:db/valueType :db.type/ref
                     :db/cardinality :db.cardinality/many}
   :block/tags {:db/valueType :db.type/ref
                :db/cardinality :db.cardinality/many}
   :block/alias {:db/valueType :db.type/ref
                 :db/cardinality :db.cardinality/many}
   :block/name {:db/valueType :db.type/string
                :db/unique :db.unique/identity
                :db/cardinality :db.cardinality/one}
   :block/original-name {:db/valueType :db.type/string
                         :db/unique :db.unique/identity
                         :db/cardinality :db.cardinality/one}
   :block/namespace {:db/valueType :db.type/ref
                     :db/cardinality :db.cardinality/one}
   :block/macros {:db/valueType :db.type/ref
                  :db/cardinality :db.cardinality/many}
   :block/file {:db/valueType :db.type/ref
                :db/cardinality :db.cardinality/one}
   :block/content {:db/valueType :db.type/string
                   :db/cardinality :db.cardinality/one
                   :db/fulltext  true}
   :file/path {:db/valueType :db.type/string
               :db/cardinality :db.cardinality/one
               :db/unique :db.unique/identity}})

;; Create DB on disk and connect to it, assume write permission to create given dir
(def conn (d/get-conn "/tmp/datalevin/mydb-3" schema))
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

(comment
  (d/transact! conn
    [{:db/id 100
      :block/name "foo"}
     {:db/id 101
      :block/name "bar"}
     {:db/id 102
      :block/page 100
      :block/parent 100
      :block/left 100
      :block/content "first block foo bar 很好的测试"}
     {:db/id 103
      :block/page 101
      :block/parent 101
      :block/left 101
      :block/refs #{100}
      :block/content "first block [[foo]]"}]))
