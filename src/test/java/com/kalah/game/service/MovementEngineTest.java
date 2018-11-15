package com.kalah.game.service;

import org.junit.Assert;
import org.junit.Test;

public class MovementEngineTest {

  private MovementEngine underTest = new MovementEngine();

  @Test
  public void shouldMovePits() throws KalahGameException {
    // given
    int[] expectedPits = new int[] {0, 7, 7, 7, 7, 7, 1, 6, 6, 6, 6, 6, 6};
    int[] givenPits = new int[] {6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};
    int pitId = 1;

    // when
    int[] result = underTest.movePits(givenPits, pitId);
    // then
    assertPitsAreCorrect(result, expectedPits);
  }


  @Test
  public void shouldMovePitsInACycle() throws KalahGameException {
    // given
    int pitId = 12;
    int[] givenPits = new int[] {6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};
    int[] expectedPits = new int[] {7, 7, 7, 7, 6, 6, 0, 6, 6, 6, 6, 0, 7, 1};
    // when
    int[] result = underTest.movePits(givenPits, pitId);
    // then
    assertPitsAreCorrect(result, expectedPits);
  }

  @Test
  public void shouldMoveOppositePitsStonesToBottomHouse() throws KalahGameException {
    // given
    int pitId = 6;
    int[] givenPits = new int[] {0, 0, 0, 0, 0, 12, 1000, 12, 13, 14, 15, 16, 17, 31};
    int[] expectedPits = new int[] {1, 1, 1, 1, 0, 0, 1019, 13, 14, 15, 16, 0, 18, 31};
    // when
    int[] result = underTest.movePits(givenPits, pitId);
    // then
    assertPitsAreCorrect(result, expectedPits);
  }

  @Test
  public void shouldMoveOppositePitsStonesToTopHouse() throws KalahGameException {
    // given
    int pitId = 13;
    int[] givenPits = new int[] {0, 0, 0, 0, 0, 0, 1000, 12, 13, 14, 15, 16, 13, 31};
    int[] expectedPits = new int[] {1, 1, 1, 1, 1, 0, 1000, 13, 14, 15, 16, 17, 0, 34};
    // when
    int[] result = underTest.movePits(givenPits, pitId);
    // then
    assertPitsAreCorrect(result, expectedPits);
  }

  private void assertPitsAreCorrect(int[] result, int[] expectedPits) {
    int i = 0;
    for (int pit : expectedPits) {
      Assert.assertEquals(pit, result[i]);
      i++;
    }
  }

}
