package com.kalah.game.controller;

import static com.monitorjbl.json.Match.match;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalah.game.model.Game;
import com.kalah.game.service.KalahGameException;
import com.kalah.game.service.KalahService;
import com.monitorjbl.json.JsonView;
import com.monitorjbl.json.JsonViewModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/games")
public class KalahController {
  private final Logger LOGGER = LoggerFactory.getLogger(getClass());

  @Autowired
  KalahService kalahService;

  @RequestMapping(method = RequestMethod.POST)
  public @ResponseBody ResponseEntity<String> createNewGame() throws JsonProcessingException {
    LOGGER.info("Received request to make new game");
    ObjectMapper mapper = new ObjectMapper().registerModule(new JsonViewModule());
    Game newGame = kalahService.createNewGame();
    return ResponseEntity.status(201).body(mapper
        .writeValueAsString(JsonView.with(newGame).onClass(Game.class, match().exclude("status"))));
  }

  @RequestMapping(value = "{gameId}/pits/{pitId}", method = RequestMethod.PUT)
  public @ResponseBody ResponseEntity<Game> move(@PathVariable("gameId") String gameId,
      @PathVariable("pitId") int pitId) throws KalahGameException {
    LOGGER.info("Received request to move seeds in game: " + gameId);
    return ResponseEntity.ok(kalahService.move(gameId, pitId));
  }

}
