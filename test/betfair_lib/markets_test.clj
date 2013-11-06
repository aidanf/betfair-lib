(ns betfair-lib.markets-test
  (:require [clojure.test :refer :all]
            [betfair-lib.util :refer :all]
            [betfair-lib.markets :refer :all]
            [betfair-lib.fixtures :refer :all]
            ))

(deftest ticks-test
  (testing "Ticks are defined correctly"
    (is (= 1.01 (first ticks)))
    (is (= 1.02 (second ticks)))
    (is (= 100.0 (last ticks)))
    (is (= 260 (count ticks))))
  (testing "tick-index"
    (is (= 0 (tick-index 1.01)))
    (is (= 259 (tick-index 100)))
    (is (= 184 (tick-index 5.5)))
    (is (= 185 (tick-index 5.6)))
    (is (= 186 (tick-index 5.7))))
  (testing "tick-delta"
    (is (= 2 (tick-delta 5.7 5.5)))
    (is (= 2 (tick-delta 5.5 5.7)))
    (is (= 2 (tick-delta-raw 5.7 5.5)))
    (is (= -2 (tick-delta-raw 5.5 5.7)))))

(deftest print-market-test
  (testing "We can print a market"
    (is (nil? (print-market market-fixture)))
    )
  )

(deftest race-meet-name-test
  (testing "We can retrieve the race meet name"
    (is (= "Newc 3rd Oct" (race-meet-name market-fixture)))
    )
  )

(deftest dist-to-time-test
  (testing "We can estimate race time"
    (is (= 338 (dist-to-time "2m5f")))
    )
  )
