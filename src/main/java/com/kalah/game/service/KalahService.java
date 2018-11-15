package com.kalah.game.service;

import com.kalah.game.repository.KalahRepository;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KalahService {
  // private static final // LOGGER // LOGGER= // LOGGER.get// LOGGER(KalahService.class.getName());

  private static final int[] INVALID_PITS = new int[]{6,13};
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
    
    Winner gameWinner = playerWonGame(movedPits);
    if (gameWinner != null) {
      game.setWinningRow(gameWinner);
      game.setGameFinished(true);
    }
    game.setStatus(movedPits);

    return repo.save(game);
  }

  private boolean requestedMoveInvalid(int[] pits, int pitId) {
    if(pits[pitId -1] == 0 || pitId-1 == INVALID_PITS[0] || pitId-1 == INVALID_PITS[1]){
      return true;
    }
    return false;
  }

  private Winner playerWonGame(int[] movedPits) {
    int[] topRow = Arrays.copyOfRange(movedPits, 7, 13);
    int[] bottomRow = Arrays.copyOfRange(movedPits, 0, 6);
    int[] winningRow = new int[]{0,0,0,0,0,0};

    if(Arrays.equals(winningRow, topRow)){
      if(movedPits[6] > movedPits[13]){
        return Winner.BOTTOM;
      } 
      if(movedPits[6] < movedPits[13]){
        return Winner.TOP;
      } 
    }

    if(Arrays.equals(winningRow, bottomRow)){
      if(movedPits[6] > movedPits[13]){
        return Winner.BOTTOM;
      } 
      if(movedPits[6] < movedPits[13]){
        return Winner.TOP;
      } 
    }
    return null;
  }

  private int[] movePits(int[] pits, int pitId) {
    int index = pitId - 1;
    pits[index] = 0;
    int[] oldPits = pits.clone();
    int counterFromReset = 0;

    for (int i = 1; i < 7; i++) {
      //if next index is outOfBounds cycle back to 0
      if(index + i > 13){
        pits[0 + counterFromReset] = oldPits[0 + counterFromReset] + 1;
        counterFromReset++;
      } else {
        pits[index + i] = oldPits[index + i] + 1; //index + 1 due to user is shown array starting at 1 not 0
      }
    }
    return pits;
  }

  private String buildUrlForNewGame(UUID gameId) {
    return "http://localhost:" + port + "/games/" + gameId.toString();
  }
}
