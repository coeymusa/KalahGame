package com.kalah.game.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import com.kalah.game.model.Game;
import com.kalah.game.repository.KalahRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class KalahServiceTest {

  private static final String PORT = "9000";
  private static final String UUID_REGEX =
      "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"; // https://stackoverflow.com/questions/136505/searching-for-uuids-in-text-with-regex
  private static String testGameId = null;
  private static Game testGame = new Game();
  private static List<Game> gameList = new ArrayList<Game>();
  @InjectMocks
  private KalahService underTest;

  @Mock
  private KalahRepository repo;

  @Mock
  private MoveValidator validator;

  @Mock
  private MovementEngine movementEngine;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Before()
  public void setup() {
    testGame = new Game();
    testGameId = testGame.getId().toString();

    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void shouldCreateANewGameWithAValidUUID() {
    // given
    underTest.port = PORT;
    given(repo.findById(testGameId)).willReturn(Optional.of(testGame));
    // when
    underTest.createNewGame();
    // then
    ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);
    verify(repo, times(1)).save(captor.capture());
    Game result = captor.getValue();
    Assert.assertTrue(validUUIDCheck(result.getId().toString()));
  }

  @Test
  public void shouldCreateANewGameWithAValidUrl() {
    // given
    underTest.port = PORT;
    given(repo.save(any(Game.class))).willReturn(testGame);
    // when
    underTest.createNewGame();
    // then
    ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);
    verify(repo, times(1)).save(captor.capture());
    Game result = captor.getValue();
    Assert.assertEquals(urlBuilder(result.getId().toString()), result.getUri());
  }

  @Test
  public void shouldCreateANewGameWithValidKalahConfiguration() {
    // given
    underTest.port = PORT;
    int[] expectedPits = new int[] {6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};
    given(repo.findById(testGameId)).willReturn(Optional.of(testGame));
    // when
    underTest.createNewGame();
    // then
    ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);
    verify(repo, times(1)).save(captor.capture());
    Game result = captor.getValue();
    assertPitsAreCorrect(result.getStatus(), expectedPits);
  }

  @Test
  public void shouldThrowErrorWhenGameCannotBeFound() throws Exception {
    // given
    String gameId = "2";
    int pitId = 1;
    expectedEx.expect(KalahGameException.class);
    expectedEx.expectMessage("Cannot find game with an id: " + gameId);
    // when
    underTest.move(gameId, pitId);
    // then
  }

  private void assertPitsAreCorrect(int[] result, int[] expectedPits) {
    int i = 0;
    for (int pit : expectedPits) {
      Assert.assertEquals(pit, result[i]);
      i++;
    }
  }

  private boolean validUUIDCheck(String gameId) {
    if (gameId.matches(UUID_REGEX)) {
      return true;
    }
    return false;
  }

  private String urlBuilder(String gameId) {
    return "http://localhost:" + PORT + "/games/" + gameId;
  }
}
