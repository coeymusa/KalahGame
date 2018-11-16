package com.kalah.game.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class Game {
  @Id
  @Field("_id")
  private UUID _id;
  @Field("uri")
  private String uri;
  @Field("status")
  private int[] status;
  @JsonIgnore
  private boolean gameFinished;
  @JsonIgnore
  private Player winningRow;

  public Game() {
    this._id = UUID.randomUUID();
    this.status = new int[] {6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};
    this.gameFinished = false;
  }

  public int[] getStatus() {
    return status;
  }

  public Player getWinningRow() {
    return winningRow;
  }

  public void setWinningRow(Player winningRow) {
    this.winningRow = winningRow;
    this.gameFinished = true;
  }

  public void setStatus(int[] status) {
    this.status = status;
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
