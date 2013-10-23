(ns betfair-lib.prices
  (:use [betfair-lib.models]
        [betfair-lib.markets]
        [clojure.core.typed])
  (:import (betfair_lib.models MarketPrice RunnerPrice)))

(defn get-runner-price [rid market-price]
  "Given a set of runner prices, return the one that matches the rid"
  (first (filter #(= rid (:selection-id %)) (:runner-prices market-price))))

(defn average-price-matched [rid coll]
  "Calculate the average price matched for a runner over a seq of prices"
  (/
   (reduce + (map #(:last-price-matched (get-runner-price rid %)) coll))
   (count coll)))

(defn print-runner-prices [runner-prices prices]
  (doseq [r runner-prices]
    (try
      (println (format "\t %10s %5.2f (%.2f..%.2f) %5.2f %5.2f |> %5d"
                       (:selection-id r)
                       (:last-price-matched r)
                       (:price (first (:best-prices-to-lay r)))
                       (:price (first (:best-prices-to-back r)))
                       (average-price-matched (:selection-id r) (take-last 5 prices))
                       (average-price-matched (:selection-id r) (take-last 10 prices))
                       (tick-delta (:price (first (:best-prices-to-lay r)))
                                   (:price (first (:best-prices-to-back r))))
                       ))
      (catch Exception ex (println ex)))))

(defn take-fav-runner-prices
  ([n price]
     {:post [(= n (count %))]}
     (take n
           (sort-by #(:last-price-matched %) (:runner-prices price)))))
