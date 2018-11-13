package com.kalah.game.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.kalah.game.service.Game;

@Repository
public interface KalahRepository extends CrudRepository<Game, String> {

}
