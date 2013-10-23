(ns betfair-lib.markets-test
  (:require [clojure.test :refer :all]
            [betfair-lib.markets :refer :all]
            ))

(deftest ticks-test
  (testing "Ticks are defined correctly"
    (is (= 1.01 (first ticks)))
    (is (= 1.02 (second ticks)))
    (is (= 100.0 (last ticks)))
    (is (= 260 (count ticks)))))
