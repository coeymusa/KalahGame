package com.kalah.game.service;

import java.util.Arrays;
import com.kalah.game.model.Player;

public class GameDecider {

  private static final int TOP_HOUSE = 13;
  private static final int BOTTOM_HOUSE = 6;
  private static final String TOP = "TOP";
  private static final String BOTTOM = "BOTTOM";
  private static int[] topRow,bottomRow;
  private static final int[] EMPTY_ROW = new int[] {0, 0, 0, 0, 0, 0};

  public static Player findWinner(int[] pits) {
    int[] pitsClone = pits.clone();
    int [] summedPits = null;
    topRow = Arrays.copyOfRange(pits, 7, 13);
    bottomRow = Arrays.copyOfRange(pits, 0, 6);
    
    String emptyRow = whichRowIsEmpty(pitsClone);
    
    if(emptyRow == null){
      return null;
    }
    
    switch(emptyRow){
      case TOP:
        summedPits = sumPits(pitsClone, BOTTOM_HOUSE); //sum remaining pits in bottom row
        return winningPlayer(summedPits);
      case BOTTOM:
        summedPits = sumPits(pitsClone, TOP_HOUSE); //sum remaining pits in top row
        return winningPlayer(summedPits);
    }
 
    return null;
  }

  private static int[] sumPits(int[] pitsClone, int oppositeHouse) {
    for (int remaningPit : topRow) {
      pitsClone[oppositeHouse] = pitsClone[oppositeHouse] + remaningPit; //sum opposite pits
    }
    return pitsClone;
  }

  private static Player winningPlayer(int[] pits) {
    if (pits[BOTTOM_HOUSE] > pits[TOP_HOUSE]) {
      return Player.BOTTOM;
    }
    
    if (pits[BOTTOM_HOUSE] < pits[TOP_HOUSE]) {
      return Player.TOP;
    }
    return null;
  }

  private static String whichRowIsEmpty(int[] pits) {
    if (Arrays.equals(EMPTY_ROW, topRow)) {
      return TOP;
    }

    if (Arrays.equals(EMPTY_ROW, bottomRow)) {
      return BOTTOM;
    }
    return null;
  }
}
