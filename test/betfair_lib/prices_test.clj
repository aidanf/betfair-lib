(ns betfair-lib.markets-test
  (:require [clojure.test :refer :all]
            [betfair-lib.prices :refer :all]
            [betfair-lib.fixtures :refer :all]))

(deftest get-runner-price-test
  (testing "get-runner-prices"

    ))

(deftest take-fav-runner-prices-test
  (testing "take-fav-runner-prices"
    (is (= 2 (count (take-fav-runner-prices 2 (first prices-fixture)))))
    ))
