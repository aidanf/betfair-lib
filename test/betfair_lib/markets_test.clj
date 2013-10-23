(ns betfair-lib.markets-test
  (:require [clojure.test :refer :all]
            [betfair-lib.markets :refer :all]
            [betfair-lib.fixtures :refer :all]
            ))

(deftest ticks-test
  (testing "Ticks are defined correctly"
    (is (= 1.01 (first ticks)))
    (is (= 1.02 (second ticks)))
    (is (= 100.0 (last ticks)))
    (is (= 260 (count ticks)))))

(deftest print-market-test
  (testing "We can print a market"
    (is (nil? (print-market market-fixture)))
    )
  )

(deftest race-meet-name-test
  (testing "We can retrieve the race meet name"
    (is (= "Wolv 14th Mar" (race-meet-name market-fixture)))
    )
  )
(run-tests)
