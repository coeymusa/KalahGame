package com.kalah.game.service;

import java.util.UUID;

public class Game {

  String uri;
  UUID id;
  
  Game(){
    this.id = UUID.randomUUID();
  }
  
  public UUID getGameId() {
    return id;
  }
  
  public String getUri() {
    return uri;
  }
  public void setUri(String uri) {
    this.uri = uri;
  }

  
  
}
