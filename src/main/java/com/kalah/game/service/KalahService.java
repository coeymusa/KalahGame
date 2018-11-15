package com.kalah.game.service;

import com.kalah.game.model.Game;
import com.kalah.game.model.Player;
import com.kalah.game.repository.KalahRepository;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KalahService {
  private final Logger LOGGER = LoggerFactory.getLogger(getClass());

  @Value("${server.port:9000}")
  String port;

  @Autowired
  private KalahRepository repo;

  @Autowired
  private MovementEngine movementEngine;

  @Autowired
  private MoveValidator validator;

  public Game createNewGame() {
    Game game = new Game();
    game.setUri(buildUrlForNewGame(game.getId()));
    System.out.println("CREATED GAME WITH ID: " + game.getId());
    LOGGER.info("Creating new game with id: " + game.getId().toString());
    return repo.save(game);
  }

  public Game move(String gameId, int pitId) throws KalahGameException {
    LOGGER.info("Moving seeds in pit: " + pitId + "for game: " + gameId);

    List<Game> repoGames = repo.findAll();
    Game game = repoGames.stream()
        .filter(currentGame -> currentGame.getId().toString().contains(gameId)).findFirst()
        .orElseThrow(() -> new KalahGameException("Cannot find game with an id: " + gameId));

    validator.validateMove(game, pitId);

    int[] movedPits = movementEngine.movePits(game.getStatus(), pitId);

    Player gameWinner = GameDecider.decideWinner(movedPits);

    if (gameWinner != null) {
      game.setWinningRow(gameWinner);
      game.setGameFinished(true);
    }
    game.setStatus(movedPits);

    return repo.save(game);
  }

  private String buildUrlForNewGame(UUID gameId) {
    return "http://localhost:" + port + "/games/" + gameId.toString();
  }
}
