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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.kalah.game.KalahGameApplication;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {KalahGameApplication.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
public class FullGameSimulationTest {
  private static final String NEW_GAME_ENDPOINT = "/games";
  private static final String UUID_REGEX =
      "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"; // https://stackoverflow.com/questions/136505/searching-for-uuids-in-text-with-regex
  private OkHttpClient client = null;
  private static final int ACCEPTED_HTTP_STATUS = 201;
  public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
  private String gameId;
  
  @LocalServerPort
  private int port;

  @Before()
  public void setup() {
    client = new OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build();

    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void fullGameSimulationWhereTopRowWins() throws IOException, JSONException {
    gameId = createGame();
    submitMove(1); // Player 1
    submitMoveError(7); // error
    submitMove(8); // Player 2
    submitMoveError(1); // error
    submitMove(9); // Player 2
    submitMove(1); // Player 1
    submitMove(10); // Player 2
    submitMoveError(14); // error
    submitMove(1); // Player 1
    submitMove(11); // Player 2
    submitMove(1); // Player 1
    submitMove(12); // Player 2
    submitMove(1); // Player 1
    submitMove(13); // Player 2
    submitMove(1); // Player 1
    submitMove(8); // Player 2
    submitMove(2); // Player 1
    submitMove(9); // Player 2
    submitMove(6); // Player 1
    submitMove(11); // Player 2
    submitMove(3); // Player 1
    submitMove(12); // Player 2
    submitMove(4); // Player 1
    submitMove(13); // Player 2
    submitMove(5); // Player 1
    submitMove(8); // Player 2
    submitMove(1); // Player 1
    submitMove(9); // Player 2
    submitMove(2); // Player 1
    submitMove(13); // Player 2
    submitMove(6); // Player 1
    submitMove(10); // Player 2
    submitMove(3); // Player 1
    submitMove(12); // Player 2
    submitMove(4); // Player 1
    submitMove(13); // Player 2
    submitMove(6); // Player 1
    submitMove(11); // Player 2
    submitMove(5); // Player 1
    submitMove(12); // Player 2
    submitMove(1); // Player 1
    submitMove(8); // Player 2
    submitMove(2); // Player 1
    verifyWin(gameId);
  }

  private void submitMoveError(int pitId) throws IOException {
    RequestBody requestBody = RequestBody.create(JSON, "{}");
    Request request = new Request.Builder().url(createURLWithPortForMove(gameId, pitId))
        .put(requestBody).build();
    Response httpResponse = client.newCall(request).execute();
    String body = httpResponse.body().string();

    checkHttpResponseForMoveError(body, httpResponse.code());

  }

  private void checkHttpResponseForMoveError(String body, int responseCode) {
    try {
      final JSONObject responseObj = new JSONObject(body);
      String status = responseObj.getString("status");
      String errorMessage = responseObj.getString("error");

      Assert.assertTrue(errorMessage.contains("Internal Server Error"));
      Assert.assertEquals(500, responseCode);
      Assert.assertNotNull(status);
    } catch (JSONException e) {
      e.printStackTrace();
    }

  }

  private void submitMove(int pitId) throws IOException {
    RequestBody requestBody = RequestBody.create(JSON, "{}");
    Request request = new Request.Builder().url(createURLWithPortForMove(gameId, pitId))
        .put(requestBody).build();
    Response httpResponse = client.newCall(request).execute();
    String body = httpResponse.body().string();
    Assert.assertEquals(200, httpResponse.code());
    checkHttpResponseForMove(body);
  }

  private void verifyWin(String gameId2) throws IOException, JSONException {
    int pitId = 13;
    String expectedErrorMessage =
        "Cannot make a move on an ended game: " + gameId + ". Winner: TOP";
    RequestBody requestBody = RequestBody.create(JSON, "{}");
    Request request = new Request.Builder().url(createURLWithPortForMove(gameId, pitId))
        .put(requestBody).build();
    // when
    Response httpResponse = client.newCall(request).execute();
    // then
    String body = httpResponse.body().string();
    int responseCode = httpResponse.code();
    final JSONObject responseObj = new JSONObject(body);
    String errorMessage = responseObj.getString("message");
    Assert.assertEquals(500, responseCode);
    Assert.assertEquals(errorMessage, expectedErrorMessage);
  }

  private String createGame() throws IOException, JSONException {
    // given
    RequestBody requestBody = RequestBody.create(JSON, "{}");
    Request request =
        new Request.Builder().url(createURLWithPort(NEW_GAME_ENDPOINT)).post(requestBody).build();

    System.out.println(request.url());
    // when
    Response httpResponse = client.newCall(request).execute();
    // then
    int responseCode = httpResponse.code();
    String body = httpResponse.body().string();
    Assert.assertEquals(ACCEPTED_HTTP_STATUS, responseCode);
    final JSONObject responseObj = new JSONObject(body);
    gameId = responseObj.getString("id");
    return gameId;
  }

  private void checkHttpResponseForMove(String body) {
    try {
      final JSONObject responseObj = new JSONObject(body);
      String uri = responseObj.getString("uri");
      String status = responseObj.getString("status");
      String id = responseObj.getString("id");

      Assert.assertTrue(validUUIDCheck(id));
      Assert.assertTrue(validURLCheck(uri, id));
      Assert.assertNotNull(status);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private boolean validURLCheck(String url, String gameId) {
    if (url.equals("http://localhost:0/games/" + gameId)) {
      return true;
    }
    return false;
  }

  private boolean validUUIDCheck(String gameId) {
    if (gameId.matches(UUID_REGEX)) {
      return true;
    }
    return false;
  }

  private String createURLWithPort(String uri) {
    return "http://localhost:" + port + uri;
  }

  private String createURLWithPortForMove(String gameId, int pitId) {
    return "http://localhost:" + port + NEW_GAME_ENDPOINT + "/" + gameId + "/pits/" + pitId;
  }
}
