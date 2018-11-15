package com.kalah.game.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import com.kalah.game.repository.KalahRepository;

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
  public void  shouldThrowErrorWhenGameCannotBeFound() throws Exception {
    // given
    String gameId = "2";
    int pitId = 1;
    expectedEx.expect(KalahGameException.class);
    expectedEx.expectMessage("Cannot find game with an id: " + gameId);
    // when
    underTest.move(gameId, pitId);
    // then
  }

  @Test
  public void shouldMovePits() throws KalahGameException {
    // given
    Game testGame = new Game();
    int[] expectedPits = new int[] {0, 7, 7, 7, 7, 7, 1, 6, 6, 6, 6, 6, 6};
    int[] givenPits = new int[] {6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};
    testGame.setStatus(givenPits);
    int pitId = 1;
    String testGameId = testGame.getId().toString();
    List<Game> gameList = new ArrayList<Game>();
    gameList.add(testGame);
    given(repo.findAll()).willReturn(gameList);
    // when
    underTest.move(testGame.getId().toString(), pitId);
    // then
    ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);
    verify(repo, times(1)).save(captor.capture());
    Game result = captor.getValue();
    assertPitsAreCorrect(result.getStatus(), expectedPits);
  }
  
  @Test
  public void shouldMovePitsInACycle() throws KalahGameException {
    // given
    Game testGame = new Game();
    int[] givenPits = new int[] {6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};
    testGame.setStatus(givenPits);
    int pitId = 12;
    String testGameId = testGame.getId().toString();
    List<Game> gameList = new ArrayList<Game>();
    gameList.add(testGame);
    int[] expectedPits = new int[] {7, 7, 7, 7, 6, 6, 0, 6, 6, 6, 6, 0, 7, 1 };
    given(repo.findAll()).willReturn(gameList);
    // when
    underTest.move(testGame.getId().toString(), pitId);
    // then
    ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);
    verify(repo, times(1)).save(captor.capture());
    Game result = captor.getValue();
    assertPitsAreCorrect(result.getStatus(), expectedPits);
  }

  @Test
  public void topRowWinsTheGameWhenTopSideisEmpty() throws KalahGameException {
    // given
    int pitId = 6;
    List<Game> gameList = new ArrayList<Game>();
    testGame.setStatus(new int[]     {0, 0, 0, 0, 0, 6, 30, 6, 6, 6, 6, 6, 6, 100});
    int[] expectedPits = new int[] {0, 0, 0, 0, 0, 0, 31, 7, 7, 7, 7, 7, 6, 100};
    gameList.add(testGame);
    given(repo.findAll()).willReturn(gameList);
    // when
    underTest.move(testGameId, pitId);
    // then
    ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);
    verify(repo, times(1)).save(captor.capture());
    Game result = captor.getValue();
    Assert.assertEquals(result.getGameFinished(), true);
    Assert.assertEquals(result.getWinningRow(), Player.TOP);
    assertPitsAreCorrect(result.getStatus(), expectedPits);
  }
  
  @Test
  public void bottomRowWinsGameWhenTopSideisEmpty() throws Exception {
    // given
    int pitId = 13;
    List<Game> gameList = new ArrayList<Game>();
    testGame.setStatus(new int[]     {12, 13, 14, 15, 16, 17, 31, 0, 0, 0, 0, 0, 6, 0});
    int[] expectedPits = new int[] {13, 14, 15, 16, 17,17 ,31, 0, 0, 0, 0, 0, 0, 1};
    gameList.add(testGame);
    given(repo.findAll()).willReturn(gameList);
    // when
    underTest.move(testGameId, pitId);
    // then
    ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);
    verify(repo, times(1)).save(captor.capture());
    Game result = captor.getValue();
    Assert.assertEquals(result.getGameFinished(), true);
    Assert.assertEquals(result.getWinningRow(), Player.BOTTOM);
    assertPitsAreCorrect(result.getStatus(), expectedPits);
  }
  
  @Test
  public void topRowWinsTheGameWhenBottomSideisEmpty() throws KalahGameException {
    // given
    int pitId = 6;
    List<Game> gameList = new ArrayList<Game>();
    testGame.setStatus(new int[]   {0, 0, 0, 0, 0, 6, 0 ,12, 13, 14, 15, 16, 17, 31});
    int[] expectedPits = new int[] {0, 0, 0, 0, 0, 0, 1, 13, 14, 15, 16, 17,17 ,31 };
    gameList.add(testGame);
    given(repo.findAll()).willReturn(gameList);
    // when
    underTest.move(testGameId, pitId);
    // then
    ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);
    verify(repo, times(1)).save(captor.capture());
    Game result = captor.getValue();
    Assert.assertEquals(result.getGameFinished(), true);
    Assert.assertEquals(result.getWinningRow(), Player.TOP);
    assertPitsAreCorrect(result.getStatus(), expectedPits);
  }
  
  @Test
  public void bottomRowWinsGameWhenBottomSideisEmpty() throws Exception {
    // given
    int pitId = 6;
    List<Game> gameList = new ArrayList<Game>();
    testGame.setStatus(new int[]   {0, 0, 0, 0, 0, 6, 1000 ,12, 13, 14, 15, 16, 17, 31});
    int[] expectedPits = new int[] {0, 0, 0, 0, 0, 0, 1001, 13, 14, 15, 16, 17,17 ,31 };
    gameList.add(testGame);
    given(repo.findAll()).willReturn(gameList);
    // when
    underTest.move(testGameId, pitId);
    // then
    ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);
    verify(repo, times(1)).save(captor.capture());
    Game result = captor.getValue();
    Assert.assertEquals(result.getGameFinished(), true);
    Assert.assertEquals(result.getWinningRow(), Player.BOTTOM);
    assertPitsAreCorrect(result.getStatus(), expectedPits);
  }
  
  @Test
  public void shouldMoveOppositePitsStonesToBottomHouse() throws KalahGameException{
    //given
    int pitId = 6;
    List<Game> gameList = new ArrayList<Game>();
    testGame.setStatus(new int[]   {0, 0, 0, 0, 0, 12, 1000 ,12, 13, 14, 15, 16, 17, 31});
    int[] expectedPits = new int[] {1, 1, 1, 1, 0, 0, 1018, 13, 14, 15, 0, 17, 18, 31}; 
    gameList.add(testGame);
    given(repo.findAll()).willReturn(gameList);
    //when
    underTest.move(testGameId, pitId);
    //then
    ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);
    verify(repo, times(1)).save(captor.capture());
    Game result = captor.getValue();
    assertPitsAreCorrect(result.getStatus(), expectedPits);
  }
  
  @Test
  public void shouldMoveOppositePitsStonesToTopHouse() throws KalahGameException{
    //given
    int pitId = 13;
    List<Game> gameList = new ArrayList<Game>();
    testGame.setStatus(new int[]   {0, 0, 0, 0, 0, 12, 1000 ,12, 13, 14, 15, 16, 16, 31});
    int[] expectedPits = new int[] {1, 1, 1, 0, 0, 0, 1018, 13, 14, 15, 0, 17, 18, 32}; 
    gameList.add(testGame);
    given(repo.findAll()).willReturn(gameList);
    //when
    underTest.move(testGameId, pitId);
    //then
    ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);
    verify(repo, times(1)).save(captor.capture());
    Game result = captor.getValue();
    assertPitsAreCorrect(result.getStatus(), expectedPits);
  }

  @Test
  public void playerCannotMoveFromScoringPitBottom() throws Exception{
    //given
    int pitId = 7;
    List<Game> gameList = new ArrayList<Game>();
    testGame.setStatus(new int[]     {0, 0, 0, 0, 0, 6, 30, 6, 6, 6, 6, 6, 6, 0});
    int[] expectedPits = new int[] {0, 0, 0, 0, 0, 0, 31, 7, 7, 7, 7, 7, 6, 0};
    gameList.add(testGame);
    given(repo.findAll()).willReturn(gameList);
    
    expectedEx.expect(KalahGameException.class);
    expectedEx.expectMessage("Requested move invalid for game: "+ testGameId + ". Pit "+ pitId + " is empty or a house.");
    //when
    underTest.move(testGameId, pitId);
    //then
  }
  
  @Test
  public void shouldThrowErrorWhenTryingToMoveOnWonGame() throws Exception{
    //given
    int pitId = 13;
    List<Game> gameList = new ArrayList<Game>();
    testGame.setGameFinished(true);
    testGame.setWinningRow(Player.TOP);
    gameList.add(testGame);
    given(repo.findAll()).willReturn(gameList);
    expectedEx.expect(KalahGameException.class);
    expectedEx.expectMessage("Cannot make a move on an ended game: " + testGameId + ". Winner: " + testGame.getWinningRow());
    //when
    underTest.move(testGameId, pitId);
    //then

  }
  
  @Test
  public void shouldThrowErrorWhenTryingToMoveOnEmptyPit() throws Exception{
    //given
    int pitId = 7;
    List<Game> gameList = new ArrayList<Game>();
    testGame.setStatus(new int[] {6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0});
    gameList.add(testGame);
    given(repo.findAll()).willReturn(gameList);
    
    expectedEx.expect(KalahGameException.class);
    expectedEx.expectMessage("Requested move invalid for game: "+ testGameId + ". Pit "+ pitId + " is empty or a house.");
    //when
    underTest.move(testGameId, pitId);
    //then
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
