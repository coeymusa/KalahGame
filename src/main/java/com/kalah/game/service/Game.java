package com.kalah.game.service;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Game {

  String uri;
  UUID id;
  
  @JsonIgnore
  int[] pits;
  
  Game(){
    this.id = UUID.randomUUID();
    this.pits = new int[]{6,6,6,6,6,6,0,6,6,6,6,6,6,0};
  }
  
  public int[] getPits() {
    return pits;
  }

  public void setPits(int[] pits) {
    this.pits = pits;
  }

  public UUID getId() {
    return id;
  }
  
  public String getUri() {
    return uri;
  }
  
  public void setUri(String uri) {
    this.uri = uri;
  }

  
  
}
