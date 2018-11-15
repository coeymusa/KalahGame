package com.kalah.game.service;

import com.kalah.game.model.Game;
import com.kalah.game.model.Player;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations;

public class MoveValidatorTest {

  private MoveValidator underTest = new MoveValidator();
  private static Game testGame = new Game();
  private static String testGameId = null;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Before()
  public void setup() {
    testGame = new Game();
    testGameId = testGame.getId().toString();

    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void shouldThrowErrorWhenTryingToMoveOnWonGame() throws Exception {
    // given
    int pitId = 13;
    testGame.setGameFinished(true);
    testGame.setWinningRow(Player.TOP);

    expectedEx.expect(KalahGameException.class);
    expectedEx.expectMessage("Cannot make a move on an ended game: " + testGameId + ". Winner: "
        + testGame.getWinningRow());
    // when
    underTest.validateMove(testGame, pitId);
    // then
  }

  @Test
  public void shouldThrowErrorWhenTryingToMoveOnEmptyPit() throws Exception {
    // given
    int pitId = 7;
    testGame.setStatus(new int[] {6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0});


    expectedEx.expect(KalahGameException.class);
    expectedEx.expectMessage("Requested move invalid for game: " + testGameId + ". Pit " + pitId
        + " is empty or a house.");
    // when
    underTest.validateMove(testGame, pitId);
    // then
  }

  @Test
  public void shouldThrowErrorForMoveFromScoringPitBottom() throws Exception {
    // given
    int pitId = 7;
    testGame.setStatus(new int[] {0, 0, 0, 0, 0, 6, 30, 6, 6, 6, 6, 6, 6, 0});
    expectedEx.expect(KalahGameException.class);
    expectedEx.expectMessage("Requested move invalid for game: " + testGameId + ". Pit " + pitId
        + " is empty or a house.");
    // when
    underTest.validateMove(testGame, pitId);
    // then
  }

}
