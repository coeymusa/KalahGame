package com.kalah.game.service;

import com.kalah.game.model.Player;

public class PlayerDecider {
  private static final int BOTTOM_HOUSE = 6;

  public static Player whichPlayerIsMoving(int pitId) {
    if (pitId < BOTTOM_HOUSE) {
      return Player.BOTTOM;
    }

    if (pitId > BOTTOM_HOUSE) {
      return Player.TOP;
    }
    return null;
  }
}
