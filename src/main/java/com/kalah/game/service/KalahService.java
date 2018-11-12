package com.kalah.game.service;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KalahService {

  @Value("${server.port:9000}")
  String port;
  
  public Game createNewGame() {
      Game game = new Game();
      game.setUri(buildUrlForNewGame(game.getGameId()));
      return game;
  }
  
  public void move(String gameId, String pitId) {
    // TODO Auto-generated method stub
  }
  
  private String buildUrlForNewGame(UUID gameId) {
    return "http://localhost:"+ port + "/games/" + gameId.toString();
  }
}
