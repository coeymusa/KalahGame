package com.kalah.game.service;

import com.kalah.game.model.Player;
import com.kalah.game.repository.KalahRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class GameDeciderTest {


  @InjectMocks
  private GameDecider underTest;

  @Mock
  private KalahRepository repo;

  @Before()
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void topRowWinsTheGameWhenTopSideisEmpty() throws KalahGameException {
    // given
    int[] givenPits = new int[] {0, 0, 0, 0, 0, 0, 31, 7, 7, 7, 7, 7, 6, 100};
    // when
    Player winner = underTest.decideWinner(givenPits);
    // then
    Assert.assertEquals(Player.TOP, winner);
  }

  @Test
  public void bottomRowWinsGameWhenTopSideisEmpty() throws KalahGameException {
    // given
    int[] givenPits = new int[] {13, 14, 15, 16, 17, 17, 31, 0, 0, 0, 0, 0, 0, 1};
    // when
    Player winner = underTest.decideWinner(givenPits);
    // then
    Assert.assertEquals(Player.BOTTOM, winner);
  }

  @Test
  public void topRowWinsTheGameWhenBottomSideisEmpty() throws KalahGameException {
    // given
    int[] givenPits = new int[] {0, 0, 0, 0, 0, 0, 1, 13, 14, 15, 16, 17, 17, 31};
    // when
    Player winner = underTest.decideWinner(givenPits);
    // then
    Assert.assertEquals(Player.TOP, winner);
  }

  @Test
  public void bottomRowWinsGameWhenBottomSideisEmpty() throws KalahGameException {
    // given
    int[] givenPits = new int[] {0, 0, 0, 0, 0, 0, 1001, 13, 14, 15, 16, 17, 17, 31};
    // when
    Player winner = underTest.decideWinner(givenPits);
    // then
    Assert.assertEquals(Player.BOTTOM, winner);
  }

}
