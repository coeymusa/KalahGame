package com.kalah.game.service;

import com.kalah.game.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MovementEngine {
  private final Logger LOGGER = LoggerFactory.getLogger(getClass());
  private static final int TOP_HOUSE = 13;
  private static final int BOTTOM_HOUSE = 6;
  private static final int OPPOSITE_PIT = 7;
  private static final int EMPTY_PIT = 0;
  private static final int SINGLE_STONE_PIT = 1;
  private static final int ARRAY_LIMIT = 14;

  public int[] movePits(int[] pits, int pitId) {
    int pitArrayId = pitId - 1;
    int stonesToDrop = pits[pitArrayId];
    pits[pitArrayId] = 0; //pick up stones
    int[] originalPits = pits.clone();
    int index = pitId; //start of array pit id + 1 -- it's first place to drop a stone  
    
    Player player = PlayerDecider.whichPlayerIsMoving(pitArrayId);
    int[] movedPits = makeMovements(originalPits,player, stonesToDrop,index);
    
    return movedPits;
  }

  private int[] makeMovements(int[] originalPits, Player player, int stonesToDrop, int index) {
    int[] movedPits = originalPits.clone();
    
    for (int stonesDropped = 1; stonesDropped <= stonesToDrop;) {
      if (index == ARRAY_LIMIT) {
        index = 0; //cycle arrray
      }
      if (isNotOpponentsHouse(player, index)) {
        movedPits[index] = originalPits[index] + 1;
        if (isFinalStoneDroppedInEmptyPitNotInOwnHouseOrOpponentsPit(index, stonesDropped, player,
            movedPits, stonesToDrop)) {
          takeOpponentsStones(movedPits, index, player);
        }
        stonesDropped++;
      }
      index++;
    }
    
    return movedPits;
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

  private void takeOpponentsStones(int[] pits, int index, Player player) {
    if (player == Player.BOTTOM) {
      int opponentsPit = index + OPPOSITE_PIT;
      LOGGER.info("Taking opponents stones in pit: " + opponentsPit);
      pits[index] = 0;  //remove final stone dropped
      pits[BOTTOM_HOUSE] += pits[opponentsPit] + 1; //add final stone dropped 
      pits[opponentsPit] = 0;
    } else {
      int opponentsPit = index - OPPOSITE_PIT;
      LOGGER.info("Taking opponents stones in pit: " + opponentsPit);
      pits[index] = 0; //remove final stone dropped
      pits[TOP_HOUSE] += pits[opponentsPit] + 1; //add final stone dropped
      pits[opponentsPit] = 0;
    }
  }
}
