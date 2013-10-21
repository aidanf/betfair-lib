(ns betfair-lib.exchange
  (:use [betfair.models]
        [betfair.global]
        [betfair.util]
        [clojure.contrib.string :only (split)]
        [clj-time.coerce :only (from-long to-long)])
  (:import (demo.handler ExchangeAPI)
           (java.util Calendar)
           (betfair.models MarketSummary Event Market Runner MarketPrice RunnerPrice Price MUbet)
           (generated.exchange BFExchangeServiceStub BFExchangeServiceStub$APIRequestHeader BFExchangeServiceStub$GetAllMarketsReq BFExchangeServiceStub$GetAllMarkets BFExchangeServiceStub$PlaceBets BFExchangeServiceStub$UpdateBets BFExchangeServiceStub$ArrayOfInt BFExchangeServiceStub$ArrayOfCountryCode)))


(def exchange-uk (ExchangeAPI/getUkExchange)) ;1, UK
(def exchange-aus (ExchangeAPI/getAusExchange)) ;2, AUS

(defn get-funds
  ([] (get-funds exchange-uk))
  ([exchange] 
     (let [funds (ExchangeAPI/getAccountFunds exchange *context*)]
       {:balance (.getBalance funds)
        :avail_balance (.getAvailBalance funds)
        :credit_limit (.getCreditLimit funds)
        :current_betfair_points (.getCurrentBetfairPoints funds)
        :exposure (.getExposure funds)
        :expo_limit (.getExpoLimit funds)})))
         
(defn get-balance
  ([] (get-balance exchange-uk))
  ([exchange]
     (.getBalance (ExchangeAPI/getAccountFunds exchange *context*))))

    
(defn get_market_profit_and_loss[]
    (not-implemented)
    )
(defn get_market_traded_volume[]
    (not-implemented)
    )

(defn get-market
  "Retrieve all static data for a given market"
  ([market-id] (get-market exchange-uk market-id))
  ([exchange market-id]
     (let [market (ExchangeAPI/getMarket exchange *context* market-id)]
       (Market. (.getBspMarket market)
                (.getCountryISO3 market)
                (.getCouponLinks market)
                (.getDiscountAllowed market)
                (vec (.getEventId (.getEventHierarchy market)))
                (.getEventTypeId market)
                (.getInterval market)
                (from-long (.getLastRefresh market)) 
                (.getLicenceId market)
                (.getMarketBaseRate market)
                (.getMarketDescription market)
                (.getMarketDescriptionHasDate market)
                (from-long (.getTimeInMillis (.getMarketDisplayTime market)))
                (.getMarketId market)
                (from-long (.getTimeInMillis (.getMarketSuspendTime market)))
                (from-long (.getTimeInMillis (.getMarketTime market)))
                (str (.getMarketType market)) 
                (str (.getMarketTypeVariant market))
                (.getMaxUnitValue market)
                (.getMenuPath market)
                (.getMinUnitValue market)
                (.getName market)
                (.getNumberOfWinners market)
                (.getParentEventId market)
                (let [runners (.getRunner (.getRunners market))]
                  (map
                   #(Runner. (.getAsianLineId %) (.getHandicap %) (.getName %) (.getSelectionId %))
                   runners))
                (.getRunnersMayBeAdded market)
                (.getTimezone market)
                (.getUnit market)
                nil nil nil nil nil
                )
       )
     ))


(defn- make-price
  "just makes a Price from a InflatedPrice - needed by get-market-prices because nested #() is not allowed"
  [p type]
  (Price.
   (.getAmountAvailable p)
   type
   (.getDepth p)
   (.getPrice p)
   ))
(defn make-back-price [p] (make-price p :B))
(defn make-lay-price [p] (make-price p :L))

(defn get-market-prices
  "Get market prices for a market"
  ;; catch org.apache.axis2.AxisFault

  ([market-id] (get-market-prices exchange-uk market-id))
  ([exchange market-id]
     (let [prices (ExchangeAPI/getMarketPrices exchange *context* market-id)]
       (MarketPrice. (.isBSPMarket prices)
                     (.getCurrency prices)
                     (.getInPlayDelay prices)
                     (.isDiscountAllowed prices)
                     (from-long (.getRefreshTime prices)) 
                     (.getMarketBaseRate prices)
                     (.getMarketId prices)
                     (.getMarketInformation prices)
                     (.getMarketStatus prices)
                     (.getNumberofWinners prices)
                     (vec (map #(.getName %) (.getRemovedRunners prices)))
                     (let [runners (.getRunners prices)]
                       (map
                        #(RunnerPrice.
                          (.getActualSPPrice %)
                          nil;asian-line-id
                          (let [prices (.getBackPrices %)]
                               (map make-back-price
                                    prices))
                          (let [prices (.getLayPrices %)]
                            (map make-lay-price
                                 prices))
                          (.getFarSPPrice %)
                          (.getHandicap %)
                          (.getLastPriceMatched %)
                          (.getNearSPPrice %)
                          (.getReductionFactor %)
                          (.getSelectionId %)
                          (.getOrderIndex %)
                          (.getTotalAmountMatched %)
                          (.isVacant %))
                        runners)
                       )))))

(defn get-complete-market-prices
  ([market-id]
     (get-complete-market-prices exchange-uk market-id))
  ([exchange market-id]
     ;; returns a InflatedCompleteMarketPrices object. Some useful methods:
     ;; [ 3] getInPlayDelay : int ()
     ;; [ 4] getMarketId : int ()
     ;; [ 5] getRemovedRunners : List ()
     ;; [ 6] getRunners : List ()
     (let [prices (ExchangeAPI/getCompleteMarketPrices exchange *context* market-id)] prices)
     (not-implemented)
     ))


(defn get-header
  "sets the request header session token"
  [token]
  (assert (string? token))
  (let [header (BFExchangeServiceStub$APIRequestHeader.)]
    (.setSessionToken header token)
    header))

(defn get-stub
  "gets the appropriate endpoint stub"
  [exchange]
  (let [url (if (= exchange exchange-aus)
              "https://api-au.betfair.com/exchange/v5/BFExchangeService"
              "https://api.betfair.com/exchange/v5/BFExchangeService")
        stub (BFExchangeServiceStub. url)]
    (.. stub _getServiceClient getOptions (setTimeOutInMilliSeconds (* 20 1000)))
    (.. stub _getServiceClient getOptions (setProperty (org.apache.axis2.transport.http.HTTPConstants/MC_ACCEPT_GZIP) "true"))
    (.. stub _getServiceClient getOptions (setProperty (org.apache.axis2.transport.http.HTTPConstants/MC_GZIP_RESPONSE) "true"))

    stub)
  )

(defn decode-market-data
  "Transform the string returned for betfair get-all-markets call into a vec of markets"
  [data]
  (let [lines (rest (split #":" data))
        fields (map #(split #"~" %) lines)
        markets (map
                 #(Market. (if (= (nth % 14) "Y") true false)  ; BSP market
                              (nth % 9) ; ISO3 country code
                              nil ; coupon links
                              nil ; discount allowed
                              (vec (map parse-int (rest (vec (split #"/" (nth % 6)))))) ; event hierarchy
                              nil ; event type id
                              nil ; interval
                              (from-long (Long/parseLong (nth % 10))) ; last refresh
                              nil ; licence id
                              nil ; market base rate
                              nil ; description
                              nil ; market description has date
                              nil ; market display time
                              (Integer/parseInt (nth % 0))  ; market_id
                              nil ; market suspend time
                              (from-long (Long/parseLong (nth % 4))) ; market time
                              (nth % 2) ; market type
                              nil ; market type variant 
                              nil ; max unit value 
                              (nth % 5) ; menuPath
                              nil ; min unit value 
                              (nth % 1) ; market name 
                              (Integer/parseInt (nth % 12)) ; number of winners
                              nil ; parent event id
                              nil ; runners
                              nil ; runners may be added 
                              nil ; timezone 
                              nil ; unit 
                              (nth % 3); status
                              (Integer/parseInt (nth % 7)) ; bet delay
                              (Integer/parseInt (nth % 8)) ; exchange-id
                              (Double/parseDouble (nth % 13)) ; total amount matched
                              (if (= (nth % 15) "Y") true false)  ; BSP market
                              )
                 fields)]
    markets))

(defn get-all-markets
  ;; TODO Add toDate and fromDate parameters
  "Return information about all markets that are currently active or suspended on a given exchange"
  ([] (get-all-markets exchange-uk nil nil))
  ([exchange event-type-ids country-codes from-date to-date]
     (let [req (BFExchangeServiceStub$GetAllMarketsReq.)
           msg (BFExchangeServiceStub$GetAllMarkets.)
           stub (get-stub exchange)
           array-of-int (BFExchangeServiceStub$ArrayOfInt.)
           array-of-country-codes (BFExchangeServiceStub$ArrayOfCountryCode.)
           from-date-cal (Calendar/getInstance)
           to-date-cal (Calendar/getInstance)]
       (.setHeader req (get-header (.getToken *context*)))
       (if event-type-ids
         (do
           (.set_int array-of-int (int-array event-type-ids))
           (.setEventTypeIds req  array-of-int))
         )
       (if country-codes
         (do
           (doseq [c country-codes]
             (.addCountry array-of-country-codes c)) 
           (.setCountries req array-of-country-codes)))
       (if from-date
         (do
           (.setTimeInMillis from-date-cal (to-long from-date))
           (.setFromDate req from-date-cal)
           )
         )
       (if to-date
         (do
           (.setTimeInMillis to-date-cal (to-long to-date))
           (.setToDate req to-date-cal)
           )
         )

       (.setRequest msg req)
       (.. *context* getUsage (addCall "getAllMarkets"))
       (decode-market-data  (.getMarketData (.getResult (.getAllMarkets stub msg)))))))

(defn get-complete-market-prices-compressed
  "Retrieve all back and lay stakes for each price on teh exchange for a given market-id in a compressed format"
  []
  (not-implemented))

(defn get-in-play-markets
  "Retrieve the markets that will be turned in-play in the next 24 hours"
  []
  (not-implemented))

;;;;;;;; Bet info
(defn get-bet
  "Retrieve information about a particular bet"
  []
  (not-implemented))

(defn get-bet-history
  "Retrieve information about the bets you have placed on a particular exchange"
  []
  (not-implemented))

(defn get-bet-lite
  "Retrieve information about a bet. lite version of get-bet"
  []
  (not-implemented))

(defn get-bet-matches-lite
  "Retrieve information about the matched portion of a bet. This is a lite version of get-bet that returns info on matched bets"
  []
  (not-implemented))

(defn get-current-bets
  "Retrieve information about your current bets on a particular exchange server
  BF recommends: use GetMUBets instead"
  []
  (not-implemented))

(defn get-current-bets-lite
  "Retrieve information about your current bets on a particular exchange server"
  []
  (not-implemented))

;;;;;;;;;;;;; Bet Placement ;;;;;;;;;;;;;;
(defn cancel-bets
  "cancel multiple unmatched bets placed on a single market"
  [exchange bet-ids]
  (let [bets (map
              #(let [bet (generated.exchange.BFExchangeServiceStub$CancelBets.)]
                 (.setBetId bet %)
                 bet)
              bet-ids)
        results (ExchangeAPI/cancelBets exchange *context* (into-array bets))]
    (map
     #(hash-map
       :bet-id (.getBetId %)
       :result-code (.getResultCode %)
       :success (.getSuccess %)
       :size-cancelled (.getSizeCancelled %)
       :size-matched (.getSizeMatched %))
     results)
    )
  )

(defn cancel-bets-by-market
  "cancel all unmatched bets (or unmatched portions of bets) placed on one or more Markets"
  []
  (not-implemented))

(defn place-bets
  "place multiple bets (1-60) on a single market"
  [exchange bets]
  (let [result (ExchangeAPI/placeBets exchange *context*  (into-array bets))]
    (vec (map #(hash-map
           :average-price-matched (.getAveragePriceMatched %)
           :bet-id (.getBetId %)
           :result-code (.getResultCode %)
           :size-matched (.getSizeMatched %)
           :success (.getSuccess %))
     result))
    )  
  )

(defn make-bet [market-id selection-id price size bet-type]
  ;; FIXME - support all enumerable options
  (let [bet (generated.exchange.BFExchangeServiceStub$PlaceBets.)]
    (.setMarketId bet market-id)
    (.setSelectionId bet selection-id)
    (.setPrice bet (double price))
    (.setSize bet (double size))
    (.setBetPersistenceType bet (generated.exchange.BFExchangeServiceStub$BetPersistenceTypeEnum/NONE)) ; NONE or IP or SP
    (.setBetType bet (if (= :LAY bet-type)
                       (generated.exchange.BFExchangeServiceStub$BetTypeEnum/L)
                       (generated.exchange.BFExchangeServiceStub$BetTypeEnum/B))) ; B or L
    (.setBetCategoryType bet (generated.exchange.BFExchangeServiceStub$BetCategoryTypeEnum/E)) ; NONE or E or M or L
    ;;(.setBspLiability "")
    bet))

(defn make-updated-bet [bet-id new-price new-size new-bet-persistence-type old-price old-size old-bet-persistence-type]
  (let [bet (generated.exchange.BFExchangeServiceStub$UpdateBets.)]
    (.setBetId bet bet-id)
    (if new-price (.setNewPrice bet new-price)) 
    (if new-size (.setNewSize bet new-size)) 
    (.setNewBetPersistenceType bet new-bet-persistence-type)
    (.setOldPrice bet old-price)
    (.setOldSize bet old-size)
    (.setOldBetPersistenceType bet old-bet-persistence-type)
    bet
    )
  )

(defn update-bet [bet new-price]
  (let [bp (generated.exchange.BFExchangeServiceStub$BetPersistenceTypeEnum/NONE)
        size (:size bet)
        u-bet (make-updated-bet (:bet-id bet) new-price size bp (:price bet) size bp)
        result (ExchangeAPI/updateBets exchange-uk *context* (into-array (vector u-bet)))]
    result)
  )

(defn make-small-back-bet
  "Place a back bet below the betfair minimum bet of 2"
  [market-id selection-id price size]
  (let [first-bet (make-bet market-id selection-id 1000 2.0 :BACK)
        res (place-bets exchange-uk (vector first-bet))
        u-bet (make-updated-bet (:bet-id (first res)) 1000 (+ 2.0 size) (generated.exchange.BFExchangeServiceStub$BetPersistenceTypeEnum/NONE) 1000 2.0 (generated.exchange.BFExchangeServiceStub$BetPersistenceTypeEnum/NONE))
        u-res  (ExchangeAPI/updateBets exchange-uk *context* (into-array (vector u-bet)))
        ]
    (cancel-bets exchange-uk [(:bet-id (first res))])
    (let [u-bet-p (make-updated-bet (.getNewBetId (first u-res)) price size (generated.exchange.BFExchangeServiceStub$BetPersistenceTypeEnum/NONE) 1000 size (generated.exchange.BFExchangeServiceStub$BetPersistenceTypeEnum/NONE))]
      (ExchangeAPI/updateBets exchange-uk *context* (into-array (vector u-bet-p))))))


(defn make-small-lay-bet
  "Place a lay bet below the betfair minimum size bet of 2"
  [market-id selection-id price size]
  (let [first-bet (make-bet market-id selection-id 1.01 2.0 :LAY)
        res (place-bets exchange-uk (vector first-bet))
        u-bet (make-updated-bet (:bet-id (first res)) 1.01 (+ 2.0 size) (generated.exchange.BFExchangeServiceStub$BetPersistenceTypeEnum/NONE) 1.01 2.0 (generated.exchange.BFExchangeServiceStub$BetPersistenceTypeEnum/NONE))
        u-res  (ExchangeAPI/updateBets exchange-uk *context* (into-array (vector u-bet)))
        ]
    (cancel-bets exchange-uk [(:bet-id (first res))])
    (let [u-bet-p (make-updated-bet (.getNewBetId (first u-res)) price size (generated.exchange.BFExchangeServiceStub$BetPersistenceTypeEnum/NONE) 1.01 size (generated.exchange.BFExchangeServiceStub$BetPersistenceTypeEnum/NONE))]
      (ExchangeAPI/updateBets exchange-uk *context* (into-array (vector u-bet-p))))))

(defn make-small-bet [market-id selection-id price size bet-type]
     (if (= :LAY bet-type)
       (make-small-lay-bet market-id selection-id price size)
       (make-small-back-bet market-id selection-id price size)))

(defn reprice-bet 
  "Cancells a bet and replaces it with a bet at a new price with size corresponding to the unmatched part of the cancelled bet"
  [bet new-price]
  ;; FIXME
  ;; cancel existing bet
  (let [bet-type (if (= (:bet-type bet) "L") :LAY :BACK)
        cancelled-bet (first (cancel-bets exchange-uk (vector (:bet-id bet))))
        new-size  (:size-cancelled cancelled-bet)
        new-bets (vector (make-bet (:market-id bet) (:selection-id bet) new-price new-size bet-type))
        ]
    (println "REPRICE:> " new-price new-size " : " bet)
    (println "REPRICE: cancel old-bet" cancelled-bet)
    (if (> (:size bet) new-size) (println "Repricing partial match: old: " (:size bet) "new:" new-size))
    (let [res (if (> new-size 2.0)
                (first (place-bets exchange-uk new-bets)) 
                (make-small-bet (:market-id bet) (:selection-id bet) new-price new-size bet-type)
                )]
      (println "REPRICE: place new bet" res)
      res)
    )
  )

(defn update-bets
  "edit multiple (1-15) bets on a single market"
  []
  (not-implemented))



(defn get-market-info
  "Retrieve market data for a given market"
  [market-id]
  (not-implemented))


(defn get-market-prices-compressed
  "Retrieve dynamic market data for a given market"
  [market-id]
  (not-implemented))



(defn get-mu-bets
  "Retrieve info about all your matched and unmatched bets on a particular exchange"
  ;; TODO allow setting of additional parameters (betstatus
  ;; record-count etc)
  ;; TODO transform java enums to clj symbols
  [exchange market-id]
  (let [result (ExchangeAPI/getMUBets exchange *context* market-id)]
   (map #(MUbet.
          (str(.getBetCategoryType %)) 
          (.getBetId %)
          (str (.getBetPersistenceType %))
          (str (.getBetType %))
          (str (.getBetStatus %)) 
          (.getBspLiability %)
          (.getMarketId %)
          (let [matched (.getTimeInMillis (.getMatchedDate %))]
            (if (< matched 0) nil (from-long matched)))         
          (from-long (.getTimeInMillis (.getPlacedDate %)))
          (.getPrice %)
          (.getSelectionId %)
          (.getSize %)
          (.getTransactionId %))
        result
        )))

(defn get-mu-bets-lite
  "Retrieve info about all your matched and unmatched bets on a particular exchange"
  []
  (not-implemented))

(defn get-market-profit-and-loss
  "Retrieve your P&L in a given market"
  []
  (not-implemented))

(defn get-market-traded-volume
  "Get all current odds and matched amounts on a single runner in a particular event"
  []
  (not-implemented))

(defn get-market-traded-volume-compressed
  "Get all current odds and matched amounts on a single runner in a particular event"
  []
  (not-implemented))

(defn get-private-markets
  "Retrieve active and suspended private markets that are within an EventType that is not visible on Betfair.com or with the GetEvents or GetActiveEvents services."
  []
  (not-implemented))

(defn get-silks
  "Retrieve a URL to the jockey silk image and data about each selection"
  []
  (not-implemented))

(defn get-silks-v2
  "Retrieve a URL to the jockey silk image and data about each selection"
  []
  (not-implemented))
