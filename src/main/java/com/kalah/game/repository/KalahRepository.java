package com.kalah.game.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.kalah.game.service.Game;

public interface KalahRepository extends MongoRepository<Game, String> {

}
