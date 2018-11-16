package com.kalah.game.model;

public enum Player {

  TOP("TOP"),
  BOTTOM("BOTTOM");
  
  private String row;
  
  Player(String row){
   this.row = row;
  }

  public String getRowValue() {
    return row;
  }

}
