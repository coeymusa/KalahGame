package com.kalah.game.service;

import com.kalah.game.repository.KalahRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KalahService {
  // private static final // LOGGER // LOGGER= // LOGGER.get// LOGGER(KalahService.class.getName());

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

    int[] movedPits = movePits(foundGame.getPits(), pitId);
    if (playerWonGame(movedPits)) {
      foundGame.setGameFinished(true);
    }
    foundGame.setPits(movedPits);

    return repo.save(foundGame);
  }

  private boolean playerWonGame(int[] movedPits) {
    if (movedPits[0] == 0 && movedPits[1] == 0 && movedPits[2] == 0 && movedPits[3] == 0
        && movedPits[4] == 0 && movedPits[5] == 0) {
      return true;
    } else if (movedPits[7] == 0 && movedPits[8] == 0 && movedPits[9] == 0 && movedPits[10] == 0
        && movedPits[11] == 0 && movedPits[12] == 0) {
      return true;
    }
    return false;
  }


  private int[] movePits(int[] pits, int pitId) {
    int index = pitId - 1;
    pits[index] = 0;
    int[] oldPits = pits.clone();

    for (int i = 1; i < 7; i++) {
      pits[index + i] = oldPits[index + i] + 1;
    }
    return pits;
  }

  private String buildUrlForNewGame(UUID gameId) {
    return "http://localhost:" + port + "/games/" + gameId.toString();
  }
}
