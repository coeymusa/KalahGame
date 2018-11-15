package com.kalah.game.repository;

import com.kalah.game.model.Game;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface KalahRepository extends MongoRepository<Game, String> {

}
