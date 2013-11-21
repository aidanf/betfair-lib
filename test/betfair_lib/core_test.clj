(ns betfair-lib.core-test
  (:require [clojure.test :refer :all]
            [betfair-lib.core :refer :all]
            [betfair-lib.config :as config :refer :all]
            [betfair-lib.exchange :refer :all]
            [betfair-lib.global :refer :all]
            [betfair-lib.markets :refer :all]
            [clj-time.core :as tm]
            [clj-time.format :as tmf]))

(deftest login-and-get-balance
  (testing "Login and check balance"
    (is (nil?  (login config/username config/password)))
    (is (number? (get-balance)))
    ))
(deftest login-with-wrong-password
  (testing "Logging in with incorrect password"
    (is (thrown? IllegalArgumentException (login config/username "asdasdasd")))
    ))

(deftest win-only-horse-racing-markets-test
  (testing "retrieving list of markets"
    (login config/username config/password)
    (is (not (empty? (win-only-horse-racing-markets))))))
