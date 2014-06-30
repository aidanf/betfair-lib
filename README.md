This is a partial implememtation of the Betfair API for clojure.

The entire betfair api is descrined in this document: ZZZ. It describes all the betfair api calls and the data structures returned.

In general, this api follows the naming and arguments that are described in the betfair API spec ([API documentation pdf](https://developer.betfair.com/assets/BetfairSportsExchangeAPIReferenceGuidev6.pdf) ). We don't implement the full API. Several API calls are different ways of getting the same information, in which case we only implement on of them. There is enough of the api implemented to build a full market trading bot for betfair - reading markets, tracking prices, placing and cancelling bets.

# Installation
To use this library, you'll need to install the betfair java api demo. It's not on clojars, so you'll need to install it manually into your local maven repository. It's included in this repo for convenience.

1. Clone this repo.
2. Install APIDemo.jar into your local maven repository
3. Install dependencies.
4. Generate the betfair-lib jar
5. Install it into your local maven repository.

    git clone ....
    cd betfair-lib
    mvn install:install-file -Dfile=./assets/APIDemo-1.0.jar -DgroupId=com.betfair.api -DartifactId=APIDemo -Dversion=1.0 -Dpackaging=jar
    lein install


There are quite a few dependencies (all from the betfair jar). Somethimes we can't find them all online, in which case you'll need to track them down yourself and install them into your local maven repository.

# Usage

In your project.clj file

    [betfair-lib "0.1.0-SNAPSHOT"]

```
   # Authenticating
   (login "username" "password")
   (println "Balance: " (get-balance))
```

Or use the with-login macro

```
    (with-login "username" "password"
      (println "Balance: " (get-balance))
    )
```

To see the full list of methods check out the betfair api documentation linked above or read the files global.clj and exchange.clj .
