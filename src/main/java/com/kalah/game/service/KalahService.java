package com.kalah.game.service;

import java.util.UUID;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.kalah.game.repository.KalahRepository;

@Service
public class KalahService {
  private static final Logger LOGGER= Logger.getLogger(KalahService.class.getName());

  @Value("${server.port:9000}")
  String port;

  @Autowired
  private KalahRepository repo;

  public Game createNewGame() {
    Game game = new Game();
    game.setUri(buildUrlForNewGame(game.getId()));
    LOGGER.info("Creating new game with id: " + game.getId().toString());
    return game;
  }

  public Game move(String gameId, int pitId) throws KalahGameException {
    LOGGER.info("Moving seeds in pit: "+ pitId + "for game: " +gameId);
    Game game = repo.findGame(gameId);
    
    if(game == null){

      throw new KalahGameException("Cannot find game with an id: " +gameId);
    }
    
    int[] movedPits = movePits(game.getPits(), pitId);
    game.setPits(movedPits);
    return game;
  }

  private int[] movePits(int[] pits, int pitId) {
    int index = pitId - 1;
    pits[index] = 0;
    int[] oldPits = pits.clone();

    for(int i =1; i <7; i++){
      pits[index + i] = oldPits[index +i] + 1;
    }
    return pits;
  }

  private String buildUrlForNewGame(UUID gameId) {
    return "http://localhost:"+ port + "/games/" + gameId.toString();
  }
}
