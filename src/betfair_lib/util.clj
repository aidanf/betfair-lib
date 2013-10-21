(ns betfair-lib.util
  (:use
   [clojure.string :only (split)]
   [clj-time.format])
  (:import (java.io File)))


(defn not-implemented [] (throw (Exception. "Not Implemented")))
(defn parse-int [x] (Integer/parseInt x))

(def default-format (formatter "dd/MM/yy HH:mm"))

(defn dt-str
  ([str] (dt-str str default-format ))
  ([str fmt] (unparse fmt str)))

(defn binary-search
  ([v target f]
     (binary-search v target f 0 (dec (count v))))
  ([v target f low high]
     (if (> low high)
       false
       (let [mid (quot (+ low high) 2)
             mid-val (v mid)]
         (cond (f mid-val target) (binary-search v target f (inc mid) high)
               (f target mid-val) (binary-search v target f low (dec mid))
                              :else mid)))))

(defn frm-save
  "Save a clojure form to file."
  [#^java.io.File file form]
  (with-open [w (java.io.FileWriter. file)]
    (binding [*out* w *print-dup* true] (prn form))))

(defn frm-load
  "Load a clojure form from file."
  ;; FIXME
  [#^java.io.File file]
  (with-open [r (java.io.PushbackReader.
                 (java.io.FileReader. file))]
    (let [rec (read r)] rec)))


(defmethod print-dup org.joda.time.DateTime [o w]
           (print-ctor o (fn [o w] (print-dup (.getMillis  o) w)) w))


(defn two-dp [n]
  (Double/parseDouble (format "%.2f" (double n)))
  )

(defn windows
  "break a collection into windows of size n"
  [n coll]
  (apply interleave
         (map
          #(partition n (drop % coll))
          (range n))))

(defn past-and-future-windows
  [n coll]
  (map
   #(split-at n %)
   (windows (* n 2) coll)))
;; (take 5 (windows 10 (range 1000)))
