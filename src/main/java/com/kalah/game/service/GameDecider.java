package com.kalah.game.service;

import com.kalah.game.model.Player;
import java.util.Arrays;
import org.springframework.stereotype.Service;

public class GameDecider {

  private static final int TOP_HOUSE = 13;
  private static final int BOTTOM_HOUSE = 6;

  public static Player decideWinner(int[] movedPits) {
    int[] copyOfPits = movedPits.clone();
    int[] topRow = Arrays.copyOfRange(movedPits, 7, 13);
    int[] bottomRow = Arrays.copyOfRange(movedPits, 0, 6);
    int[] winningRow = new int[] {0, 0, 0, 0, 0, 0};

    if (Arrays.equals(winningRow, topRow)) {
      for (int remaningPit : bottomRow) {
        copyOfPits[BOTTOM_HOUSE] = copyOfPits[TOP_HOUSE] + remaningPit;
      }
      if (copyOfPits[BOTTOM_HOUSE] > movedPits[TOP_HOUSE]) {
        return Player.BOTTOM;
      }
      if (copyOfPits[BOTTOM_HOUSE] < movedPits[TOP_HOUSE]) {
        return Player.TOP;
      }
    }

    if (Arrays.equals(winningRow, bottomRow)) {
      for (int remaningPit : topRow) {
        copyOfPits[TOP_HOUSE] = copyOfPits[TOP_HOUSE] + remaningPit;
      }
      if (copyOfPits[BOTTOM_HOUSE] > movedPits[TOP_HOUSE]) {
        return Player.BOTTOM;
      }
      if (copyOfPits[BOTTOM_HOUSE] < movedPits[TOP_HOUSE]) {
        return Player.TOP;
      }
    }
    return null;
  }
}
