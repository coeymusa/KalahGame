package com.kalah.game.service;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import org.junit.Ignore;

@Entity
@Table(name = "game_store")
public class Game {
  
  @Id
  UUID id;
  @Column
  String uri;
  
  @JsonIgnore
  @Column(name = "pits")
  String pitsString;
  
  @JsonIgnore
  @Transient
  int[] pits;
  
   Game(){
    this.id = UUID.randomUUID();
    this.pits = new int[]{6,6,6,6,6,6,0,6,6,6,6,6,6,0};
    this.pitsString = arrayToString(this.pits);
  }
  
  public int[] getPits() {
    return pits;
  }

  public String getPitsString() {
    return arrayToString(this.pits);
  }

  public void setPitsString(String pitsString) {
    this.pitsString = pitsString;
  }

  public void setPits(int[] pits) {
    this.pits = pits;
  }

  public UUID getId() {
    return id;
  }
  
  public String getUri() {
    return uri;
  }
  
  public void setUri(String uri) {
    this.uri = uri;
  }

  public String arrayToString(int[] pits2){
    StringBuilder sb = new StringBuilder();
    for(int i : pits2){
      sb.append(i + ",");
    }
    return sb.toString();
    
  }
  
  
}
