# Kalah Game Project

A project where two players can play a game of Kalah(https://en.wikipedia.org/wiki/Kalah) over HTTP

## Getting Started
```
cd clone https://github.com/coeymusa/KalahGame.git
```

```
mvn clean install
```

To run the project
```
cd target -java -jar KalahGame-0.0.1-SNAPSHOT.jar
```
 
OR
```
java -jar KalahGame-0.0.1-SNAPSHOT.jar - on given jar
```

Should run on port 900 by default, to change this add --server.port=*DESIRED_PORT*
### Installing

Instance of MongoDB running locally

Installing MongoDB locally - https://docs.mongodb.com/manual/tutorial/install-mongodb-on-windows/

## Running the tests
```
mvn clean install 
```
Should run both unit and integration tests
## Rules of the game

Each of the two players has six pits in front of him/her. To the right of the six pits, each player has a larger pit, his
Kalah or house.

At the start of the game, six stones are put in each pit.

The player who begins picks up all the stones in any of their own pits, and sows the stones on to the right, one in
each of the following pits, including his own Kalah. No stones are put in the opponent's' Kalah. If the players last
stone lands in his own Kalah, he gets another turn. This can be repeated any number of times before it's the other
player's turn.

When the last stone lands in an own empty pit, the player captures this stone and all stones in the opposite pit (the
other players' pit) and puts them in his own Kalah.

The game is over as soon as one of the sides run out of stones. The player who still has stones in his/her pits keeps
them and puts them in his/hers Kalah. The winner of the game is the player who has the most stones in his Kalah.


## How to play the game

To create a new game open a terminal and run the command
```
curl --header "Content-Type: application/json" \ --request POST \ http://<host>:<port>/games
```
This will create a new game and send a response of 
HTTP code: 201
Response Body: { "id": "1234", "uri": "http://<host>:<port>/games/1234" }

To make a move 
```
curl --header "Content-Type: application/json" \ --request PUT \ http://<host>:<port>/games/{gameId}/pits/{pitId}
 ```
Using the same gameId returned in the first request and pit id from 1-14.

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

