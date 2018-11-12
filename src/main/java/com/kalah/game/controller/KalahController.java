package com.kalah.game.controller;

import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.kalah.game.service.Game;
import com.kalah.game.service.KalahGameException;
import com.kalah.game.service.KalahService;

@RestController
@RequestMapping("/games")
public class KalahController {
  private static final Logger LOGGER= Logger.getLogger(KalahController.class.getName());
  @Autowired
  KalahService kalahService;

  @RequestMapping(method = RequestMethod.POST)
  public @ResponseBody ResponseEntity<Game> createNewGame() {
      LOGGER.info("Received request to make new game");
      return ResponseEntity.ok(kalahService.createNewGame());
  }

  @RequestMapping(value = "{gameId}/pits/{pitId}", method = RequestMethod.POST)
  public @ResponseBody ResponseEntity<Game> move(@PathVariable("gameId") String gameId,@PathVariable("pitId") int pitId) throws KalahGameException {
    LOGGER.info("Received request to move seeds in game: " + gameId);
    return ResponseEntity.ok(kalahService.move(gameId,pitId));
  }

}
