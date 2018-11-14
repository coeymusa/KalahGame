package com.kalah.game.controller.integration;

import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.kalah.game.KalahGameApplication;
import com.kalah.game.repository.KalahRepository;
import com.kalah.game.service.Game;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {KalahGameApplication.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
public class KalahControllerIntegrationTest {
  private static final String NEW_GAME_ENDPOINT = "/games";
  private static final String UUID_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"; //https://stackoverflow.com/questions/136505/searching-for-uuids-in-text-with-regex
  private OkHttpClient client = new OkHttpClient();
  public static final MediaType JSON  = MediaType.parse("application/json; charset=utf-8");

  @LocalServerPort
  private int port;
  
  @Autowired
  private KalahRepository repo;

  @Before()
  public void setup(){
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void givenANewGameHasBeenRequestedThen200ReturnedWithAValidGame() throws IOException{
    //given 
    RequestBody requestBody = RequestBody.create(JSON, "{}");
    Request request = new Request.Builder()
        .url(createURLWithPort(NEW_GAME_ENDPOINT))
        .post(requestBody)
        .build();
    
    System.out.println(request.url());
    //when
    Response httpResponse = client.newCall(request).execute(); 
    //then
    int responseCode = httpResponse.code();
    String body = httpResponse.body().string();
    String url = parseUrl(body);
    String gameId = parseBody(body);
    
    Assert.assertTrue(validUUIDCheck(gameId));
    Assert.assertTrue(validURLCheck(url,gameId));
    Assert.assertEquals(200, responseCode);
  }
  
  
  @Test
  public void givenANewMoveHasBeenRequestedThen200Returned() throws IOException{
    //given 
    String gameId = createNewGame();
    String pitId = "1";

    RequestBody body = RequestBody.create(JSON, "{}");
    Request request = new Request.Builder()
        .url(createURLWithPortForMove(gameId,pitId))
        .post(body)
        .build();
    //when
    Response httpResponse = client.newCall(request).execute(); 
    //then
    int responseCode = httpResponse.code();

    Assert.assertEquals(200, responseCode);
  }
  
  @Test
  public void givenANewMoveHasBeenRequestedWithAnInvalidGameIdThen500Returned() throws IOException{
    //given 
    String gameId = "INVALID_GAME_ID";
    String pitId = "1";

    RequestBody body = RequestBody.create(JSON, "{}");
    Request request = new Request.Builder()
        .url(createURLWithPortForMove(gameId,pitId))
        .post(body)
        .build();
    //when
    Response httpResponse = client.newCall(request).execute(); 
    //then
    int responseCode = httpResponse.code();
    Assert.assertEquals(500, responseCode);
  }
  
 

  private String createNewGame() throws IOException {
    RequestBody requestBody = RequestBody.create(JSON, "{}");
    Request request = new Request.Builder()
        .url(createURLWithPort(NEW_GAME_ENDPOINT))
        .post(requestBody)
        .build();
    
    Response httpResponse = client.newCall(request).execute(); 
    //then
    String body = httpResponse.body().string();
    String gameId = parseBody(body);
    return gameId;
  }
  
  private boolean validURLCheck(String url, String gameId) {
    if(url.equals("http://localhost:0/games/" + gameId)) {
        return true;
    }
    return false;
  }

  private String parseUrl(String body) {
    String urlParts[] = body.split("\"");
    String url = urlParts[3];
    return url;
  }

  private boolean validUUIDCheck(String gameId) {
    if(gameId.matches(UUID_REGEX)){
      return true;
    }
    return false;
  }
  
  //breaks on reordering of Game model
  private String parseBody(String body) {
    String UUIDParts[] = body.split(":");
    String uuid = UUIDParts[4].substring(1, UUIDParts[4].length()-2);
    return uuid;
  }

  private String createURLWithPort(String uri) {
    return "http://localhost:" + port + uri;
  }

  private String createURLWithPortForMove(String gameId, String pitId) {
    return "http://localhost:" + port + NEW_GAME_ENDPOINT + "/"  + gameId + "/pits/" + pitId;
  }
}
