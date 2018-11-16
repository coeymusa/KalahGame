package com.kalah.game.integration;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import com.kalah.game.KalahGameApplication;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {KalahGameApplication.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
public class KalahIntegrationTest {
  private static final String NEW_GAME_ENDPOINT = "/games";
  private static final int ACCEPTED_HTTP_STATUS = 201;
  private static final int OK_HTTP_STATUS = 200;
  private static final String UUID_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"; //https://stackoverflow.com/questions/136505/searching-for-uuids-in-text-with-regex
  private OkHttpClient client =  null;
  public static final MediaType JSON  = MediaType.parse("application/json; charset=utf-8");

  @LocalServerPort
  private int port;

  @Before()
  public void setup(){
    client = new OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build();
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void givenANewGameHasBeenRequestedThen201ReturnedWithAValidGame() throws IOException, JSONException{
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

    checkHttpResponseForNewGame(body, responseCode);
  }

  @Test
  public void givenANewMoveHasBeenRequestedThen200Returned() throws IOException, JSONException{
    //given 
    String requestGameId = createNewGame();
    String requestPitId = "1";

    RequestBody requestBody = RequestBody.create(JSON, "{}");
    Request request = new Request.Builder()
        .url(createURLWithPortForMove(requestGameId,requestPitId))
        .put(requestBody)
        .build();
    //when
    Response httpResponse = client.newCall(request).execute(); 
    //then
    int responseCode = httpResponse.code();
    String body = httpResponse.body().string();

    checkHttpResponseForMove(body,responseCode);
  }

  @Test
  public void givenANewMoveHasBeenRequestedWithAnInvalidGameIdThen500Returned() throws IOException{
    //given 
    String gameId = "INVALID_GAME_ID";
    String pitId = "1";

    RequestBody body = RequestBody.create(JSON, "{}");
    Request request = new Request.Builder()
        .url(createURLWithPortForMove(gameId,pitId))
        .put(body)
        .build();
    //when
    Response httpResponse = client.newCall(request).execute(); 
    //then
    int responseCode = httpResponse.code();
    Assert.assertEquals(500, responseCode);
  }
  
  @Test
  public void givenANewMoveHasBeenRequestedOnAnEmptyPitThen500Returned() throws IOException, JSONException{
    //given 
    String requestGameId = createNewGame();
    String pitId = "7";
    String expectedErrorMessage = "Requested move invalid for game: "+ requestGameId + ". Pit "+ pitId + " is empty or a house.";
    RequestBody requestBody = RequestBody.create(JSON, "{}");
    Request request = new Request.Builder()
        .url(createURLWithPortForMove(requestGameId,pitId))
        .put(requestBody)
        .build();
    //when
    Response httpResponse = client.newCall(request).execute(); 
    //then
    String body = httpResponse.body().string();
    int responseCode = httpResponse.code();
    final JSONObject responseObj = new JSONObject(body);
    String errorMessage = responseObj.getString("message");
    Assert.assertEquals(500, responseCode);
    Assert.assertEquals(errorMessage, expectedErrorMessage);
  }


  private String createNewGame() throws IOException, JSONException {
    RequestBody requestBody = RequestBody.create(JSON, "{}");
    Request request = new Request.Builder()
        .url(createURLWithPort(NEW_GAME_ENDPOINT))
        .post(requestBody)
        .build();

    Response httpResponse = client.newCall(request).execute(); 
    //then
    String body = httpResponse.body().string();
    final JSONObject responseObj = new JSONObject(body);
    String gameId = responseObj.getString("id");
    return gameId;
  }

  private void checkHttpResponseForMove(String body, int responseCode) {
    try {
      final JSONObject responseObj = new JSONObject(body);
      String uri = responseObj.getString("uri");
      String status = responseObj.getString("status");
      String id = responseObj.getString("id");

      Assert.assertEquals(OK_HTTP_STATUS, responseCode);
      Assert.assertTrue(validUUIDCheck(id));
      Assert.assertTrue(validURLCheck(uri,id));
      Assert.assertNotNull(status);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private void checkHttpResponseForNewGame(String body, int responseCode) throws JSONException {
    final JSONObject responseObj = new JSONObject(body);
    String uri = responseObj.getString("uri");
    String id = responseObj.getString("id");
    Assert.assertTrue(validUUIDCheck(id));
    Assert.assertTrue(validURLCheck(uri,id));
    Assert.assertEquals(ACCEPTED_HTTP_STATUS, responseCode);
  }
  private boolean validURLCheck(String url, String gameId) {
    if(url.equals("http://localhost:0/games/" + gameId)) {
      return true;
    }
    return false;
  }

  private boolean validUUIDCheck(String gameId) {
    if(gameId.matches(UUID_REGEX)){
      return true;
    }
    return false;
  }

  private String createURLWithPort(String uri) {
    return "http://localhost:" + port + uri;
  }

  private String createURLWithPortForMove(String gameId, String pitId) {
    return "http://localhost:" + port + NEW_GAME_ENDPOINT + "/"  + gameId + "/pits/" + pitId;
  }
}
