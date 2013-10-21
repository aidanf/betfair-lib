(ns betfair-lib.global
  (:import (demo.util APIContext)
           (betfair_lib.models Event EventType MarketSummary)
           (demo.handler GlobalAPI))
  (:use [betfair-lib.util]))

(def *context* (APIContext.)) ; default to new empty context

(defmacro with-login [username password & body]
  `(binding [*context* (APIContext.)]
     (let [ret# (do (login ~username ~password) ~@body)]
       (logout)
       ret#)))

(defn login [username password]
	(GlobalAPI/login *context* username password))

(defn logout [] (GlobalAPI/logout *context*))

(defn usage [] (.getUsage *context*))

(defn usage-methods []
    (let [usage (.getUsage *context*)
          methods (.getAllMethodsCalled usage)]
        (zipmap methods
            (map
                #(.getTotalCallsForMethod usage %)
                methods))))

(defn- usage-for-timebucket [usage methods time_bucket]
    (zipmap methods (map #(.getMethodCallsForBucket usage % time_bucket) methods)))


(defn usage-timed []
    (let [usage (.getUsage *context*)
          methods (.getAllMethodsCalled usage)
          time_buckets (.getAllTimeBuckets usage)]
        (zipmap
            time_buckets
            (map
                #(usage-for-timebucket usage methods %)
                time_buckets))) )

(defn get-active-event-types
  "Returns a list of all categories of sporting events (Games, Event Types) that are available to bet on"
  []
    (map
        #(EventType. (.getId %) (.getName %) (.getExchangeId %) (.getNextMarketId %))
        (demo.handler.GlobalAPI/getActiveEventTypes *context*)))

(defn get-all-event-types
  "Return a list of all categories of sports (Games, Event Types) that have at least one market associated with them, regardless of whether that market is now closed for betting."
  []
  (not-implemented))

(defn get-events
  "Allows you to navigate through the events hierarchy until you reach details of the betting market for an event you are interested in."
  [event-id]
  "returns either events or markets"
  (let [resp (demo.handler.GlobalAPI/getEvents *context* event-id)
        events (map
                #(Event. (.getEventId %) (.getEventName %) (.getEventTypeId %)
                         (.getMenuLevel %) (.getOrderIndex %) (.getStartTime %) (.getTimezone %)
                         )
                (.getBFEvent (.getEventItems resp)))]
    (if (empty? events) nil events)))


(defn get-sub-events
     "Recursively get all sub-events for an event"
     ([event-id] (get-sub-events [] (vec (get-events event-id))))
     ([done todo]
        (if (empty? todo)
          done
          (let [sub-first-events (get-events (:event-id (first todo)))]
            (println (count done) " done, " (count todo) " todo")
            (if (not (empty? sub-first-events))
              (get-sub-events done (vec (concat sub-first-events (rest todo))))
              (recur (cons (first todo) done) (rest todo)))))))

; MarketTypeVariantEnum values are: ADL : ASL : COUP : D
; MarketTypeEnum values are: A, L, O, R
(defn get-markets-summaries [event_id]
    (let [resp (demo.handler.GlobalAPI/getEvents *context* event_id)
          markets (map
                   #(MarketSummary. (.getEventTypeId %)
                                    (.getExchangeId %)
                                    (.getEventParentId %)
                                    (.getMarketId %)
                                    (.getMarketName %)
                                    (.getValue (.getMarketType %))
                                    (.getValue (.getMarketTypeVariant %))
                                    (.getMenuLevel %)
                                    (.getOrderIndex %)
                                    (.getStartTime %)
                                    (.getTimezone %)
                                    (.getVenue %)
                                    (.getBetDelay %)
                                    (.getNumberOfWinners %)
                            )
                   (.getMarketSummary (.getMarketItems resp)))]
      (if (empty? markets) nil markets)))
