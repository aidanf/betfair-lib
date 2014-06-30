This is a partial implememtation of the Betfair API for clojure.

The entire betfair api is descrined in this document: ZZZ. It describes all the betfair api calls and the data structures returned.

In general, this api follows the names that are described in the betfair API spec. We don't implement the full API. Several API calls are different ways of getting the same information, in which case we only implement on of them. There is enough of the api implemented to build a full market trading bot for betfair - reading markets, tracking prices, placing and cancelling bets.

# Installation
You'll need to download the jar of the betfair api and install it into your local maven repository.

Next time you run lein, it should install all the dependencies.

There are quite a few dependencies (all from the betfair jar). Somethimes we can't find them all online, in which case you'll need to track them down yourself and install them into your local maven repository.

Also download betfair-lib.jar and install it into your local maven repository.

??? Betfair apidemo on clojars ???
??? Install on Clojars ???

# Example

## Authenticating

## List markets

## Track price for runners in a market

## Placing bets
-blog post?
-video?
