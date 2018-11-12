package com.kalah.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.kalah.game.service.Game;
import com.kalah.game.service.KalahService;

@RestController("/games")
public class KalahController {

  @Autowired
  KalahService kalahService;

  @RequestMapping(method = RequestMethod.POST)
  public @ResponseBody ResponseEntity<Game> createNewGame() {
    return ResponseEntity.ok(kalahService.createNewGame());
  }

  @RequestMapping(value = "{gameId}/pits/{pitId}", method = RequestMethod.POST)
  public @ResponseBody ResponseEntity<String> move(@PathVariable("gameId") String gameId,@PathVariable("pitId") String pitId) {
    kalahService.move(gameId,pitId);
    return null;
  }

}
