(ns betfair-lib.markets
  (:use [betfair-lib.util]
        [betfair-lib.exchange]
        [clojure.string :only (split)])
  (:require [clj-time.core :as tm]
            [clj-time.format :as tmf]
            [clojure.math.numeric-tower :as math]))


(def ticks
  (vec (map #(/ (float %) 100)
       (concat
        (range 101 200 1)
        (range 200, 300, 2)
        (range 300, 400, 5)
        (range 400, 600, 10)
        (range  600, 1000, 20)
        (range 1000, 2000, 50)
        (range 2000, 3000, 100)
        (range 3000, 5000, 200)
        (range 5000, 10000, 500)
        [10000]))))

(defn dist-time-estimates []
  { "5f" 60
    "6f" 74
    "7f" 88
    "1m" 104
    "1m1f" 122
    "1m2f" 140
    "1m3f" 158
    "1m4f" 176
    "1m5f" 194
    "1m6f" 212
    "1m7f" 230
    "2m" 248
    "2m1f" 266
    "2m2f" 284
    "2m3f" 302
    "2m4f" 320
    "2m5f" 338
    "2m6f" 356
    "2m7f" 374
    "3m" 392
    "3m1f" 410
    "3m2f" 428
    "3m3f" 446
    "3m4f" 464
    "3m5f" 482
    "3m6f" 500
    "3m7f" 518
    "4m" 536})

(defn dist-to-time [dst]
  {:pre [(string? dst)]
   :post [number? %]}
  ((dist-time-estimates) (first (split dst #"\s+")))
  )

(defn tick-index
  "Return the index for a tick-price in the tick array"
  [t]
  (binary-search ticks (float t) <)
  )
(defn tick-delta [t1 t2]
  "Return the number of ticks between two points on the ladder"
  (try
    (math/abs
     (-
      (tick-index t1)
      (tick-index t2)))
    (catch Exception ex 0)
    )
  )

(defn tick-move
  "Return the price n ticks from t"
  [t n]
  (nth ticks (+ (tick-index t) n))
  )
(defn tick-up [t n]
  (tick-move t (math/abs n))
  )

(defn tick-down [t n]
  (tick-move t (- (math/abs n)))
  )

(defn time-till-start [market]
  (try (tm/interval (tm/now) (:market-time market)) (catch java.lang.IllegalArgumentException ex 0))
  )

(defn format-time-till-start [market]
  (try
    (let [seconds (.getStandardSeconds (.toDuration (time-till-start market)))]
      (format "%02d:%02d" (quot seconds 60) (rem seconds 60))
      )
    (catch java.lang.IllegalArgumentException ex "00:00")
    )
  )

(defn race-meet-name [market]
  (last  (split (.replace (:menu-path market) "\\" ">>") #">>")))

(defn print-market [market]
  (println
   (format "%-20s %10s %10s %20s"  (:name market) (dt-str (:market-time market) (tmf/formatters :hour-minute)) (:market-id market) (race-meet-name market) )))

(defn print-markets [markets]
  (println "------------------------------------")
  (println (count markets) "Markets")
  (println "------------------------------------")
  (doseq [market markets]
    (print-market market)))

(defn win-only-market? [market]
  "Return true if market looks like a win-only racing market"
  (and (= 1 (:number-of-winners market))
       (re-find #"\d[m|f]+" (:name market))
       )
    )

(defn win-only-horse-racing-markets
  "Convenience function that will return all of todays win-only horse racing markets in GBR/IRE"
  []
  (let [markets  (get-all-markets exchange-uk [7] ["GBR" "IRL"] (tm/now) (tm/plus (tm/now) (tm/hours 8)) )]
    (filter win-only-market?
            (sort-by #(:market-time %) markets))
    )

  )

(defn in-play? [market-price]
  "takes a market price and determines if the market is in-play"
  {:pre [(number? (:delay market-price))]}
   (= (:delay market-price) 1)
   )

(defn market->complete? [prices]
  "check that the prices data for the race is complete"
  (let
      [[prices-ip prices-pre] (partition-by in-play? prices
                           )]
    (and
     (> (count prices-ip) 0)
     (> (count prices-pre) 0)
     (contains? #{"CLOSED" "SUSPENDED"} (:market-status (last prices-ip)))
     )))
