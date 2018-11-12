package com.kalah.game.service;

import static org.hamcrest.CoreMatchers.any;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

public class KalahServiceTest {

  private static final String PORT = "9000";
  private static final String UUID_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"; //https://stackoverflow.com/questions/136505/searching-for-uuids-in-text-with-regex
  
  @InjectMocks
  private KalahService underTest;

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
    Assert.assertTrue(validUUIDCheck(result.getGameId().toString()));
  }
  
  @Test
  public void shouldCreateANewGameWithAValidUrl(){
    //given
    underTest.port = PORT;
    //when
    Game result =  underTest.createNewGame();
    //then
    Assert.assertEquals(urlBuilder(result.getGameId().toString()) , result.getUri());
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
