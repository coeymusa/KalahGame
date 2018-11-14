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

  private static final int[] TOP_WINNING_ROWS = new int[]{0,1,2,3,4,5};
  private static final int[] BOTTOM_WINNING_ROWS = new int[]{7,8,9,10,11,12,13};
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

    List<Game> games = repo.findAll();
    Game foundGame =
        games.stream().filter(game -> game.getId().toString().contains(gameId)).findFirst()
            .orElseThrow(() -> new KalahGameException("Cannot find game with an id: " + gameId));

    if(foundGame.getGameFinished()){
      throw new KalahGameException("Cannot make a move on an ended game: " + gameId);
    }
    
    int[] movedPits = movePits(foundGame.getPits(), pitId);
    if (playerWonGame(movedPits)) {
      foundGame.setGameFinished(true);
    }
    foundGame.setPits(movedPits);

    return repo.save(foundGame);
  }

  private boolean playerWonGame(int[] movedPits) {
    int[] topRow = Arrays.copyOfRange(movedPits, 7, 13);
    int[] bottomRow = Arrays.copyOfRange(movedPits, 0, 6);
    int[] winningRow = new int[]{0,0,0,0,0,0};

    if(Arrays.equals(winningRow, topRow)){
      return true;
    }
    
    if(Arrays.equals(winningRow, bottomRow)){
      return true;
    }

    return false;
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
