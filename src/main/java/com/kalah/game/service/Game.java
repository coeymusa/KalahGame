package com.kalah.game.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class Game {

  @Field("uri")
  private String uri;
  @Id
  @Field("_id")
  private UUID _id;
  @JsonIgnore
  @Field("pits")
  private int[] pits;
  @JsonIgnore
  private boolean gameFinished;

  Game() {
    this._id = UUID.randomUUID();
    this.pits = new int[] {6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};
    this.gameFinished = false;
  }

  public int[] getPits() {
    return pits;
  }

  public void setPits(int[] pits) {
    this.pits = pits;
  }

  public UUID getId() {
    return _id;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public void setGameFinished(boolean gameFinished) {
    this.gameFinished = gameFinished;
  }

  public boolean getGameFinished() {
    return gameFinished;
  }

}
