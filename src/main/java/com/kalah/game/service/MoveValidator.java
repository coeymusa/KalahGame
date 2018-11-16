package com.kalah.game.service;

import com.kalah.game.model.Game;
import org.springframework.stereotype.Service;

public class MoveValidator {

  private static final int[] INVALID_PITS = new int[] {6, 13};

  public static void validateMove(Game game, int pitId) throws KalahGameException {
    String gameId = game.getId().toString();

    if (game.getGameFinished()) {
      throw new KalahGameException(
          "Cannot make a move on an ended game: " + gameId + ". Winner: " + game.getWinningRow());
    }

    if (requestedMoveInvalid(game.getStatus(), pitId)) {
      throw new KalahGameException("Requested move invalid for game: " + gameId + ". Pit " + pitId
          + " is empty or a house.");
    }
  }

  private static boolean requestedMoveInvalid(int[] pits, int pitId) {
    if (pits[pitId - 1] == 0 || pitId - 1 == INVALID_PITS[0] || pitId - 1 == INVALID_PITS[1]) {
      return true;
    }
    return false;
  }


}
