package com.kalah.game.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import com.kalah.game.repository.KalahRepository;
import static org.mockito.BDDMockito.*;

public class KalahServiceTest {

  private static final String PORT = "9000";
  private static final String UUID_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"; //https://stackoverflow.com/questions/136505/searching-for-uuids-in-text-with-regex
  
  @InjectMocks
  private KalahService underTest;
  
  @Mock
  private KalahRepository repo;

  @Before()
  public void setup(){
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void shouldCreateANewGameWithAValidUUID(){
    //given
    underTest.port = PORT;
    //when
    Game result =  underTest.createNewGame();
    //then
    Assert.assertTrue(validUUIDCheck(result.getId().toString()));
  }
  
  @Test
  public void shouldCreateANewGameWithAValidUrl(){
    //given
    underTest.port = PORT;
    //when
    Game result =  underTest.createNewGame();
    //then
    Assert.assertEquals(urlBuilder(result.getId().toString()) , result.getUri());
  }
  
  @Test
  public void shouldCreateANewGameWithValidKalahConfiguration(){
    //given
    underTest.port = PORT;
    int[] pits = new int[]{6,6,6,6,6,6,0,6,6,6,6,6,6,0};
    //when
    Game result =  underTest.createNewGame();
    //then
    int i =0;
    for (int pit : pits) {
      Assert.assertEquals(pit, result.getPits()[i]);  
      i++;
    }
  }
  
  @Test(expected = KalahGameException.class)
  public void shouldThrowErrorWhenGameCannotBeFound() throws KalahGameException{
    //given
    String gameId = "2";
    int pitId = 1;
    //when
    underTest.move(gameId, pitId);
    //then
  }
  @Test
  public void shouldMovePits() throws KalahGameException{
    //given
    Game testGame = new Game();
    int[] expectedPits = new int[]{0,7,7,7,7,7,1,6,6,6,6,6,6};
    int[] givenPits = new int[]{6,6,6,6,6,6,0,6,6,6,6,6,6,0};
    testGame.setPits(givenPits);
    int pitId = 1;

    given(repo.findGame(testGame.getId().toString())).willReturn(testGame);
    //when
    Game result = underTest.move(testGame.getId().toString(), pitId);
    //then
    int i =0;
    for (int pit : expectedPits) {
      Assert.assertEquals(pit, result.getPits()[i]);  
      i++;
    }
  }

  private boolean validUUIDCheck(String gameId) {
    if(gameId.matches(UUID_REGEX)){
      return true;
    }
    return false;
  }

  private String urlBuilder(String gameId){
    return "http://localhost:" + PORT +"/games/" + gameId;
  }
}
