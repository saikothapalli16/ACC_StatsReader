import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


//helper methods that can either load games into a list that already exists on a specific date
public class DailyGameMethods {

    private String url = "https://ncaa-api.henrygd.me/scoreboard/basketball-men/d1/2024/12/15/ACC";

    private ArrayNode parseJSON(Date day){
        url = "https://ncaa-api.henrygd.me/scoreboard/basketball-men/d1/" + day.toString() + "/ACC";
        System.out.println(url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = null;

        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;
        try {
            root = mapper.readTree(response.body());
        } catch (IOException e) {
            e.printStackTrace();
        throw new RuntimeException();
        }

        ArrayNode games = (ArrayNode) root.get("games");
        ArrayNode filteredGames = new ObjectMapper().createArrayNode();

        if (games != null) {
            Iterator<JsonNode> iterator = games.iterator();
            while (iterator.hasNext()) {
                JsonNode gameNode = iterator.next();
                JsonNode game = gameNode.get("game");

                JsonNode homeConferences = game.get("home").get("conferences");
                JsonNode awayConferences = game.get("away").get("conferences");

                boolean ACCHome = hasConference(homeConferences, "ACC");
                boolean ACCAway = hasConference(awayConferences, "ACC");

                if(ACCAway || ACCHome){
                    filteredGames.add(gameNode);
                }
            }
        }

        return filteredGames;
    }

    private static boolean hasConference(JsonNode conferences, String conferenceName) {
        if (conferences != null) {
            for (JsonNode conference : conferences) {
                if (conference.get("conferenceName").asText().equals(conferenceName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private ArrayList<String> loadGamesOfDay(Date day){
        ArrayList<String> gamesOfDay = new ArrayList<>();
        ArrayNode games = parseJSON(day);
        if (games != null) {
            Iterator<JsonNode> iterator = games.iterator();
            while (iterator.hasNext()) {
                JsonNode game = iterator.next();
                gamesOfDay.add(game.get("game").get("url").asText());
            }
        }
        else{
            System.out.println("No games found on " + day);
        }
        return gamesOfDay;
    }

    public List<String> addGamesOfDay(List<String> mainList, Date day){
        mainList.addAll(loadGamesOfDay(day));
//        System.out.println(mainList);
        return mainList;
    }
}
