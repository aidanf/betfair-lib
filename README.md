This is a partial implememtation of the Betfair API for clojure.

In general, this api follows the naming and arguments that are described in the betfair [API documentation (pdf)](https://developer.betfair.com/assets/BetfairSportsExchangeAPIReferenceGuidev6.pdf). That document describes all the api calls available and the data-structures returned.

This wrapper doesn't implement the full API - several API calls are different ways of getting the same information, in which case we only implement on of them - but it implements a fully functional subset.  There is enough of the api implemented to build a full market trading bot for betfair - reading markets, tracking prices, placing and cancelling bets.

The list of methods that are implemented are:

### Global API
* login
* logout
* usage
* usage-methods
* usage-timed
* get-active-event-types
* get-all-event-types
* get-events
* get-sub-events
* get-markets-summaries

### Exchange API
* get-funds
* get-balance
* get-market
* get-market-prices
* get-all-markets
* get-mu-bets
* place-bets
* cancel-bets
* update-bet

See the betfair documentation for descriptions of each of these methods. There are also some higher level functions for interacting with betfair. Check the source for details.

# Installation
To use this library, you'll need to install the betfair java api demo. It's not on clojars, so you'll need to install it manually into your local maven repository. It's included in this repo for convenience.

1. Clone this repo.
2. Install APIDemo.jar into your local maven repository
3. Install dependencies.
4. Generate the betfair-lib jar
5. Install it into your local maven repository.

```
git clone git@github.com:aidanf/betfair-lib.git
cd betfair-lib
mvn install:install-file -Dfile=./extras/APIDemo-1.0.jar -DgroupId=com.betfair.api -DartifactId=APIDemo -Dversion=1.0 -Dpackaging=jar
lein install
```

There are quite a few dependencies (all from the betfair jar). Somethimes lein can't find them all online, in which case you'll need to track them down yourself and manually install them into your local maven repository.

# Usage

In your project.clj file add the dependency

```clojure
[betfair-lib "0.1.0-SNAPSHOT"]
```

Then authenticate with betfair and start calling API methods.

```clojure
;;  Authenticating with global context
(login "username" "password")
(println "Balance: " (get-balance))

```

To see the full list of methods check out the betfair api documentation linked above or read the files global.clj and exchange.clj.
