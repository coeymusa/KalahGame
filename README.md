# Kalah Game Project

A project where two players can play a game of Kalah(https://en.wikipedia.org/wiki/Kalah) over HTTP

## Getting Started
```
cd clone https://github.com/coeymusa/KalahGame.git
```

```
mvn clean install
```
Should run on port 900 by default, to change this add --server.port=*DESIRED_PORT*
### Installing

Instance of MongoDB running locally

Download MongoDB - https://www.mongodb.com/download-center/community

Unzip the downloaded folder

Open the unzipped folder and find the ../bin directory

Run the mongo executable within the ./bin
```
./mongo.exe
```

Installing MongoDB guide - https://docs.mongodb.com/manual/tutorial/install-mongodb-on-windows/

## Running the tests
```
mvn clean install 
```
Should run both unit and integration tests

## Run the game

To run the project
```
cd target
```

```
java -jar KalahGame-0.0.1-SNAPSHOT.jar
 ```
 
To specify port
```
java -jar KalahGame-0.0.1-SNAPSHOT.jar --server.port=PORT
 ```
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
This will create a new game and send a response 
```
{
 "uri":"http://localhost:9001/games/49c0ad0b-b3da-4d26-81f1-594d4f5fb03b",
 "id":"49c0ad0b-b3da-4d26-81f1-594d4f5fb03b"
}
 ```
To make a move 
```
curl --header "Content-Type: application/json" \ --request PUT \ http://<host>:<port>/games/{gameId}/pits/{pitId}
 ```
 
This will make a move and send a response 
```
{
    "uri": "http://localhost:9001/games/49c0ad0b-b3da-4d26-81f1-594d4f5fb03b",
    "status": [
        0,
        7,
        7,
        7,
        7,
        7,
        1,
        6,
        6,
        6,
        6,
        6,
        6,
        0
    ],
    "id": "49c0ad0b-b3da-4d26-81f1-594d4f5fb03b"
}
 ```
Using the same gameId returned in the first request.Pit ids range from 1-14.


 
## Error Handling
The rules allow us to start and finish games. These rules make some moves invalid and in this situation a 500 response is returned. Below is a few examples of invalid move requests responses.

Move is requested on a finished game then a 500 will be returned with an error message
 ```
"status": 500,
"error": "Internal Server Error",
"message": "Cannot make a move on an ended game: 415b0cb2-05c4-4a31-a758-3b3d9dd1b5c2. Winner: BOTTOM",
"path": "/games/415b0cb2-05c4-4a31-a758-3b3d9dd1b5c2/pits/2"
 ```
 
Move is requested on a pit which is a "House" e.g 7 or 14 a 500 response should be returned
 ```
"status": 500,
"error": "Internal Server Error",
"message": "Requested move invalid for game: 09a9ed4a-0ef0-4915-9e9c-9891f0336e9b. Pit 7 is empty or a house.",
"path": "/games/09a9ed4a-0ef0-4915-9e9c-9891f0336e9b/pits/7"
 ```
 
 Move is requested for a pit with no stones a 500 response should be returned
  ```
"status": 500,
"error": "Internal Server Error",
"message": "Requested move invalid for game: 09a9ed4a-0ef0-4915-9e9c-9891f0336e9b. Pit 1 is empty or a house.",
"path": "/games/09a9ed4a-0ef0-4915-9e9c-9891f0336e9b/pits/1"
 ```
## Built With

* [Maven](https://maven.apache.org/) - Dependency Management


