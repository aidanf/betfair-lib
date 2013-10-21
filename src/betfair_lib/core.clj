(ns betfair-lib.core
  (:use
   [betfair-lib.config :as config]
   [betfair-lib.exchange]
   [betfair-lib.global]

        )
  )

(defn -main
  "Login to betfair and print out your balance."
  (println "Loggin into betfair...")
  (login config/username config/password)
  )
