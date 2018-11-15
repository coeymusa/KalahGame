package com.kalah.game.service;

public enum Winner {

  TOP("TOP"),
  BOTTOM("BOTTOM");
  
  String row;
  
  Winner(String row){
   this.row = row;
  }
  
  
}
