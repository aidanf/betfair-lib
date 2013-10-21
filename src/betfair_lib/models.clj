(ns betfair-lib.models
  (:use [betfair.persistence]))

(defrecord EventType [id
                      name
                      exchange-id
                      next-market-id])

(defrecord Event [event-id
                  name
                  type-id
                  menu-level
                  order-index
                  start-time
                  timezone])

(defrecord MarketSummary [event-type-id
                          exchange-id
                          event-parent-id
                          market-id
                          market-name
                          market-type
                          market-type-variant
                          menu-level
                          order-index
                          start-time
                          timezone
                          venue
                          bet-delay
                          number-of-winners])

(defrecord Market [bsp-market
                   country-ISO3
                   coupon-links
                   discount-allowed
                   event-hierarchy
                   event-type-id
                   interval
                   last-refresh
                   licence-id
                   market-base-rate
                   market-description
                   market-description-has-date
                   market-display-time
                   market-id
                   market-suspend-time
                   market-time
                   market-type
                   market-type-variant
                   max-unit-value
                   menu-path
                   min-unit-value
                   name
                   number-of-winners
                   parent-event-id
                   runners
                   runners-may-be-added
                   timezone
                   unit
                   status
                   delay
                   exchange-id
                   total-amount-matched
                   turning-in-play
                   ])


(defrecord MarketPrice [bsp-market
                        currency-code
                        delay
                        discount-allowed
                        last-refresh
                        market-base-rate
                        market-id
                        market-info
                        market-status
                        number-of-winners
                        removed-runners
                        runner-prices])

(defrecord RunnerPrice [actual-bsp
                        asian-line-id
                        best-prices-to-back
                        best-prices-to-lay
                        far-bsp
                        handicap
                        last-price-matched
                        near-bsp
                        reduction-factor
                        selection-id
                        sort-order
                        total-amount-matched
                        vacant])

(defrecord Price [amount-available bet-type depth price])

(defrecord Runner [asian-line-id handicap name selection-id])

(defrecord MUbet [bet-category-type
                  bet-id
                  bet-persistence-type
                  bet-type
                  bet-status
                  bsp-liability
                  market-id
                  matched-date
                  placed-date
                  price
                  selection-id
                  size
                  transaction-id])
