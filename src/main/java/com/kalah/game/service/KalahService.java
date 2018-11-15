package com.kalah.game.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.kalah.game.repository.KalahRepository;

@Service
public class KalahService {
  // private static final // LOGGER // LOGGER= // LOGGER.get// LOGGER(KalahService.class.getName());

  private static final int[] INVALID_PITS = new int[]{6,13};

  private static final int TOP_HOUSE = 13;
  private static final int BOTTOM_HOUSE = 6;
  private static final int OPPOSITE_PIT = 7;
  private static final int EMPTY_PIT = 0;
  private static final int SINGLE_STONE_PIT = 1;
  private static final int ARRAY_LIMIT= 14;
  @Value("${server.port:9000}")
  String port;

  @Autowired
  private KalahRepository repo;



  public Game createNewGame() {
    Game game = new Game();
    game.setUri(buildUrlForNewGame(game.getId()));
    System.out.println("CREATED GAME WITH ID: " + game.getId());
    // LOGGER.info("Creating new game with id: " + game.getId().toString());
    return repo.save(game); 
  }

  public Game move(String gameId, int pitId) throws KalahGameException {
    // LOGGER.info("Moving seeds in pit: "+ pitId + "for game: " +gameId);

    List<Game> repoGames = repo.findAll();
    Game game =
        repoGames.stream().filter(currentGame -> currentGame.getId().toString().contains(gameId)).findFirst()
        .orElseThrow(() -> new KalahGameException("Cannot find game with an id: " + gameId));

    if(game.getGameFinished()){
      throw new KalahGameException("Cannot make a move on an ended game: " + gameId + ". Winner: " + game.getWinningRow());
    }

    if(requestedMoveInvalid(game.getStatus(), pitId)){
      throw new KalahGameException("Requested move invalid for game: " + gameId + ". Pit " + pitId + " is empty or a house." );
    }

    int[] movedPits = movePits(game.getStatus(), pitId);

    Player gameWinner = playerWonGame(movedPits);
    if (gameWinner != null) {
      game.setWinningRow(gameWinner);
      game.setGameFinished(true);
    }
    game.setStatus(movedPits);

    return repo.save(game);
  }

  private Player whichPlayerIsMoving(int pitId) {
    if(pitId < 6){
      return Player.BOTTOM;
    }

    if(pitId > 6){
      return Player.TOP;
    }
    return null;
  }

  private boolean requestedMoveInvalid(int[] pits, int pitId) {
    if(pits[pitId -1] == 0 || pitId-1 == INVALID_PITS[0] || pitId-1 == INVALID_PITS[1]){
      return true;
    }
    return false;
  }

  private Player playerWonGame(int[] movedPits) {
    int[] copyOfPits = movedPits.clone();
    int[] topRow = Arrays.copyOfRange(movedPits, 7, 13);
    int[] bottomRow = Arrays.copyOfRange(movedPits, 0, 6);
    int[] winningRow = new int[]{0,0,0,0,0,0};

    if(Arrays.equals(winningRow, topRow)){
      for(int remaningPit: bottomRow){
        copyOfPits[BOTTOM_HOUSE] = copyOfPits[TOP_HOUSE] + remaningPit;
      }
      if(copyOfPits[BOTTOM_HOUSE] > movedPits[TOP_HOUSE]){
        return Player.BOTTOM;
      } 
      if(copyOfPits[BOTTOM_HOUSE] < movedPits[TOP_HOUSE]){
        return Player.TOP;
      } 
    }

    if(Arrays.equals(winningRow, bottomRow)){
      for(int remaningPit: topRow){
        copyOfPits[TOP_HOUSE] = copyOfPits[TOP_HOUSE] + remaningPit;
      }
      if(copyOfPits[BOTTOM_HOUSE] > movedPits[TOP_HOUSE]){
        return Player.BOTTOM;
      } 
      if(copyOfPits[BOTTOM_HOUSE] < movedPits[TOP_HOUSE]){
        return Player.TOP;
      } 
    }
    return null;
  }

  private int[] movePits(int[] pits, int pitId) {
    int index = pitId - 1;
    int stonesInPit = pits[index];
    pits[index] = 0;
    int[] oldPits = pits.clone();
    int counter = index + 1; //starting position after picking up at pit
    Player player = whichPlayerIsMoving(pitId-1);

    for (int stonesDropped = 1; stonesDropped <= stonesInPit;) { 
      if(counter == ARRAY_LIMIT){
        counter = 0;
      }
      if(isNotOpponentsHouse(player, counter)){
        pits[counter] = oldPits[counter] + 1;
        if(isFinalStoneDroppedInEmptyPitNotInOwnHouseOrOpponentsPit(counter , stonesDropped, player, pits,stonesInPit)){
          takeOpponentsStones(pits,counter, player);
        }
        stonesDropped++;
      }
      counter++;
    }
    return pits;
  }

  private boolean isNotOpponentsHouse(Player player, int counter) {
    if(player == Player.TOP && counter != BOTTOM_HOUSE){
      return true;
    }
    
    if(player == Player.BOTTOM && counter != TOP_HOUSE){
      return true;
    }
    return false;
  }

  private boolean isFinalStoneDroppedInEmptyPitNotInOwnHouseOrOpponentsPit(int currentIndex, int stonesDropped, Player player, int[] pits, int stonesInPit) {
    //last stone
    if( stonesDropped == stonesInPit){
      if(player == Player.BOTTOM && currentIndex < BOTTOM_HOUSE && pits[currentIndex] == SINGLE_STONE_PIT){
        return true;
      }

      if(player == Player.TOP && currentIndex > BOTTOM_HOUSE  && pits[currentIndex] == SINGLE_STONE_PIT){
        return true;
      }
    }
    return false;
  }

  private void takeOpponentsStones(int[] pits, int currentIndex, Player player) {
    if(player == Player.BOTTOM){
      pits[BOTTOM_HOUSE] = pits[BOTTOM_HOUSE] + SINGLE_STONE_PIT ; //always one due to being placed in empty pit
      pits[currentIndex] = 0;
      pits[BOTTOM_HOUSE] = pits[BOTTOM_HOUSE] + pits[currentIndex + OPPOSITE_PIT] ;
      pits[currentIndex + OPPOSITE_PIT] = 0;
    } else {
      pits[TOP_HOUSE] = pits[TOP_HOUSE] + SINGLE_STONE_PIT ; //always one due to being placed in empty pit
      pits[currentIndex] = 0;
      pits[TOP_HOUSE] = pits[TOP_HOUSE] + pits[currentIndex - OPPOSITE_PIT] ;
      pits[currentIndex - OPPOSITE_PIT] = 0;
    }
  }

  private String buildUrlForNewGame(UUID gameId) {
    return "http://localhost:" + port + "/games/" + gameId.toString();
  }
}
