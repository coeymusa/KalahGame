package com.kalah.game.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.kalah.game.service.KalahService;


public class KalahControllerTest {

  @InjectMocks
  private KalahController underTest;

  @Mock
  private KalahService kalahService;

  @Before()
  public void setup(){
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void shouldCreateNewGame(){
    //given
    //when
    underTest.createNewGame();
    //then
    verify(kalahService,times(1)).createNewGame();
  }

  @Test
  public void shouldMakeAMove(){
    //given
    String gameId = "123";
    int pitId = 1;
    //when
    underTest.move(gameId, pitId);
    //then
    verify(kalahService, times(1)).move(gameId,pitId);
  }

}
