package com.kalah.game.service;

import com.kalah.game.model.Player;
import org.junit.Assert;
import org.junit.Test;

public class GameDeciderTest {

  @Test
  public void topRowWinsTheGameWhenTopSideisEmpty() throws KalahGameException {
    // given
    int[] givenPits = new int[] {0, 0, 0, 0, 0, 0, 31, 7, 7, 7, 7, 7, 6, 100};
    // when
    Player winner = GameDecider.findWinner(givenPits);
    // then
    Assert.assertEquals(Player.TOP, winner);
  }

  @Test
  public void bottomRowWinsGameWhenTopSideisEmpty() throws KalahGameException {
    // given
    int[] givenPits = new int[] {13, 14, 15, 16, 17, 17, 31, 0, 0, 0, 0, 0, 0, 1};
    // when
    Player winner = GameDecider.findWinner(givenPits);
    // then
    Assert.assertEquals(Player.BOTTOM, winner);
  }

  @Test
  public void topRowWinsTheGameWhenBottomSideisEmpty() throws KalahGameException {
    // given
    int[] givenPits = new int[] {0, 0, 0, 0, 0, 0, 1, 13, 14, 15, 16, 17, 17, 31};
    // when
    Player winner = GameDecider.findWinner(givenPits);
    // then
    Assert.assertEquals(Player.TOP, winner);
  }

  @Test
  public void bottomRowWinsGameWhenBottomSideisEmpty() throws KalahGameException {
    // given
    int[] givenPits = new int[] {0, 0, 0, 0, 0, 0, 1001, 13, 14, 15, 16, 17, 17, 31};
    // when
    Player winner = GameDecider.findWinner(givenPits);
    // then
    Assert.assertEquals(Player.BOTTOM, winner);
  }
  
  @Test
  public void handleNoPlayerWinning(){
    // given
    int[] givenPits = new int[] {0, 0,2, 2, 0, 0, 1001, 13, 14, 15, 16, 17, 17, 31};
    // when
    Player winner = GameDecider.findWinner(givenPits);
    // then
    Assert.assertEquals(null, winner);
  }

}
