(ns betfair-lib.persistence
  (:use [clj-time.format]
        [clojure.data.json :only (read-json json-str write-json Write-JSON)]
        )
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def datetime-formatter (formatters :basic-date-time))

(defn unparse-datetime [dt]
  (unparse datetime-formatter dt))

(defn parse-datestring [ds]
  (parse datetime-formatter ds))

(defn- write-json-joda-datetime [x out escape-unicode?]
  (write-json (unparse-datetime x) out escape-unicode?))

(extend org.joda.time.DateTime Write-JSON
        {:write-json write-json-joda-datetime})

(defn looks-like-time-str? [str]
  (and
   (string? str)
   (= 20 (count str))
   (= (last str) \Z)))

(defn replace-json-strings-with-dates [json-data]
  "Replaces our date-strings(from when we wrote to json) with DateTime objects"
  (let [date-keys (filter #(looks-like-time-str? (% json-data)) (keys json-data))]
    (merge
     json-data
     (zipmap
      date-keys
      (map #(parse-datestring (% json-data)) date-keys)))))

(defn dump-to-file [obj filename]
  (spit filename  (json-str obj)))

(defn load-from-file [filename]
  (let [json-data (read-json (slurp filename))]
    (if (map? json-data)
      (replace-json-strings-with-dates json-data)
      (vec (map #(replace-json-strings-with-dates %) json-data)))))

(defn complete-market-prices? [prices]
  ;; TODO
  )

(defn incomplete-markets-ids []
  (let [market-files (filter #(.endsWith (.getName %) ".market") (file-seq (io/file "data")))
        market-ids (map #(first (str/split (.getName %) #"\.")) market-files)
        ]
    (first market-ids))
  ;; TODO filter market ids based on complete?
  )
