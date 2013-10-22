(ns betfair-lib.core
  (:use
   [betfair-lib.config :as config]
   [betfair-lib.exchange]
   [betfair-lib.global]

        )
   (:gen-class))

(defn -main
  [& args]
  "Login to betfair and print out your balance."
  (println "Logging into betfair...")
  (login config/username config/password)
  (println "Balance: " (get-balance)))
