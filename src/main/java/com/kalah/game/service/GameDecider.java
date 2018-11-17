package com.kalah.game.service;

import java.util.Arrays;
import com.kalah.game.model.Player;

public class GameDecider {

  private static final int TOP_HOUSE_INDEX = 13;
  private static final int BOTTOM_HOUSE_INDEX = 6;
  private static final String TOP = "TOP";
  private static final String BOTTOM = "BOTTOM";
  private static int[] topRowValues,bottomRowValues;
  private static final int[] EMPTY_ROW = new int[] {0, 0, 0, 0, 0, 0};

  public static Player findWinner(int[] pits) {
    int [] summedPits = null;
    topRowValues = Arrays.copyOfRange(pits, 7, 13);
    bottomRowValues = Arrays.copyOfRange(pits, 0, 6);
    
    String emptyRow = whichRowIsEmpty(pits);
    
    if(emptyRow == null){
      return null;
    }
    
    switch(emptyRow){
      case TOP:
        summedPits = sumPits(pits, bottomRowValues, BOTTOM_HOUSE_INDEX); //sum remaining pits in bottom row
        return winningPlayer(summedPits);
      case BOTTOM:
        summedPits = sumPits(pits,topRowValues,TOP_HOUSE_INDEX); //sum remaining pits in top row
        return winningPlayer(summedPits);
    }
 
    return null;
  }

  private static int[] sumPits(int[] pitsClone, int[] rowToSum, int oppositeHouseIndex) {
    for (int currentPit : rowToSum) {
      pitsClone[oppositeHouseIndex] += currentPit; //sum opposite pits
    }
    return pitsClone;
  }

  private static Player winningPlayer(int[] summedPits) {
    if (summedPits[BOTTOM_HOUSE_INDEX] > summedPits[TOP_HOUSE_INDEX]) {
      return Player.BOTTOM;
    }
    
    if (summedPits[BOTTOM_HOUSE_INDEX] < summedPits[TOP_HOUSE_INDEX]) {
      return Player.TOP;
    }
    return null;
  }

  private static String whichRowIsEmpty(int[] pits) {
    if (Arrays.equals(EMPTY_ROW, topRowValues)) {
      return TOP;
    }

    if (Arrays.equals(EMPTY_ROW, bottomRowValues)) {
      return BOTTOM;
    }
    return null;
  }
}
