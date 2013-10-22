(ns betfair-lib.core-test
  (:require [clojure.test :refer :all]
            [betfair-lib.core :refer :all]
            [betfair-lib.config :as config :refer :all]
            [betfair-lib.exchange :refer :all]
            [betfair-lib.global :refer :all]
            ))

(deftest login-and-get-balance
  (testing "Login and check balance"
    (is (nil?  (login config/username config/password)))
    (is (number? (get-balance)))
    ))
(deftest login-with-wrong-password
  (testing "Logging in with incorrect password"
    (is (thrown? IllegalArgumentException (login config/username "asdasdasd")))
    ))
