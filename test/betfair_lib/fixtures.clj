(ns betfair-lib.fixtures
  (:use [betfair-lib.persistence])
  (:require [clojure.test :refer :all]
            ))

(def market-id  "108562374")
(def market-fixture (load-from-file (java.io.File. (str "./test/betfair_lib/data/" market-id ".market"))))
(def prices-fixture (load-from-file (java.io.File. (str "./test/betfair_lib/data/" market-id ".prices"))))
(def bets-fixture (load-from-file (java.io.File. (str "./test/betfair_lib/data/" market-id ".bets"))))
