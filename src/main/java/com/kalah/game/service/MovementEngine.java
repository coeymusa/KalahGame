package com.kalah.game.service;

import com.kalah.game.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MovementEngine {
  private final Logger LOGGER = LoggerFactory.getLogger(getClass());
  private static final int TOP_HOUSE = 13;
  private static final int BOTTOM_HOUSE = 6;
  private static final int OPPOSITE_PIT = 7;
  private static final int EMPTY_PIT = 0;
  private static final int SINGLE_STONE_PIT = 1;
  private static final int ARRAY_LIMIT = 14;

  public int[] movePits(int[] status, int pitId) {
    int index = pitId - 1;
    int stonesInPit = status[index];
    status[index] = 0;
    int[] oldPits = status.clone();
    int counter = index + 1; // starting position after picking up at pit
    Player player = PlayerDecider.whichPlayerIsMoving(pitId - 1);

    for (int stonesDropped = 1; stonesDropped <= stonesInPit;) {
      if (counter == ARRAY_LIMIT) {
        counter = 0;
      }
      if (isNotOpponentsHouse(player, counter)) {
        status[counter] = oldPits[counter] + 1;
        if (isFinalStoneDroppedInEmptyPitNotInOwnHouseOrOpponentsPit(counter, stonesDropped, player,
            status, stonesInPit)) {
          takeOpponentsStones(status, counter, player);
        }
        stonesDropped++;
      }
      counter++;
    }
    return status;
  }

  private boolean isNotOpponentsHouse(Player player, int counter) {
    if (player == Player.TOP && counter != BOTTOM_HOUSE) {
      return true;
    }

    if (player == Player.BOTTOM && counter != TOP_HOUSE) {
      return true;
    }
    return false;
  }

  private boolean isFinalStoneDroppedInEmptyPitNotInOwnHouseOrOpponentsPit(int currentIndex,
      int stonesDropped, Player player, int[] pits, int stonesInPit) {
    // last stone
    if (stonesDropped == stonesInPit) {
      if (player == Player.BOTTOM && currentIndex < BOTTOM_HOUSE
          && pits[currentIndex] == SINGLE_STONE_PIT) {
        return true;
      }

      if (player == Player.TOP && currentIndex > BOTTOM_HOUSE
          && pits[currentIndex] == SINGLE_STONE_PIT) {
        return true;
      }
    }
    return false;
  }

  private void takeOpponentsStones(int[] pits, int currentIndex, Player player) {
    if (player == Player.BOTTOM) {
      LOGGER.info("Taking opponents stones in pit: " + (currentIndex + OPPOSITE_PIT));
      pits[BOTTOM_HOUSE] = pits[BOTTOM_HOUSE] + SINGLE_STONE_PIT; // always one due to being placed
      // in empty pit
      pits[currentIndex] = 0;
      pits[BOTTOM_HOUSE] = pits[BOTTOM_HOUSE] + pits[currentIndex + OPPOSITE_PIT];
      pits[currentIndex + OPPOSITE_PIT] = 0;
    } else {
      LOGGER.info("Taking opponents stones in pit: " + (currentIndex + OPPOSITE_PIT));
      pits[TOP_HOUSE] = pits[TOP_HOUSE] + SINGLE_STONE_PIT; // always one due to being placed in empty pit
      pits[currentIndex] = 0;
      pits[TOP_HOUSE] = pits[TOP_HOUSE] + pits[currentIndex - OPPOSITE_PIT];
      pits[currentIndex - OPPOSITE_PIT] = 0;
    }
  }
}
