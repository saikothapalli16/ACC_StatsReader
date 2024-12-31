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

public class SingleGameStats {
    private String justUrl;
    private String gameURL;
    private final ArrayList<String> homeTeamStats;
    private final ArrayList<String> awayTeamStats;
    private final ArrayList<String> differentialStats;


    public SingleGameStats(String gameURL) {
        this.justUrl = gameURL;
        this.gameURL = "https://ncaa-api.henrygd.me" + gameURL + "/boxscore";
        this.homeTeamStats = new ArrayList<>();
        this.awayTeamStats = new ArrayList<>();
        this.differentialStats = new ArrayList<>();
    }

    public JsonNode getGameJSON(){
//        System.out.println(gameURL);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(gameURL))
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
        return root;
    }
    private void getTeamName(JsonNode gameJSON, int i){
        ArrayNode name = (ArrayNode) gameJSON.get("meta").get("teams");
        String homeTeamShortName = null;
        String awayTeamShortName = null;
        for(JsonNode team : name){
            String isHomeTeam = team.get("homeTeam").asText();
            String shortName = team.get("shortName").asText();
            if (isHomeTeam.equals("true")) {
                homeTeamShortName = shortName;
            } else if (isHomeTeam.equals("false")) {
                awayTeamShortName = shortName;
            }
        }

        if(i == 0){
        this.homeTeamStats.add(homeTeamShortName);}
        else{
        this.awayTeamStats.add(awayTeamShortName);}
    }

    public void setHomeTeamStats(JsonNode gameJSON){
        getTeamName(gameJSON, 0);
        ArrayNode name = (ArrayNode) gameJSON.get("teams");
        JsonNode secondTeamStats = name.get(1).get("playerTotals");
        Iterator<String> fieldNames = secondTeamStats.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            if(!fieldName.contains("Percentage")){
                JsonNode valueNode = secondTeamStats.get(fieldName);
                String value = valueNode.asText();
                homeTeamStats.add(value);
            }

        }
        for(int i = 1; i < 4; i ++ ){
            percentage(i, true);
        }

//        System.out.println(homeTeamStats);
    }
    public void setAwayTeamStats(JsonNode gameJSON){
        getTeamName(gameJSON, 1);
        ArrayNode name = (ArrayNode) gameJSON.get("teams");
        JsonNode firstTeamStats = name.get(0).get("playerTotals");
        Iterator<String> fieldNames = firstTeamStats.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            if(!fieldName.contains("Percentage")){
            JsonNode valueNode = firstTeamStats.get(fieldName);
            String value = valueNode.asText();
            awayTeamStats.add(value);}
        }

        for(int i = 1; i < 4; i ++ ){
            percentage(i, false);
        }

//        System.out.println(awayTeamStats);
    }

    public void percentage(int i, boolean isHome){
        if(isHome){
            String[] parts = homeTeamStats.get(i).toString().split("-");
            int firstNumber = Integer.parseInt(parts[0]);
            int secondNumber = Integer.parseInt(parts[1]);
            String percentage = String.valueOf((double) firstNumber / secondNumber);
            homeTeamStats.set(i, percentage);
        }
        else{
            String[] parts = awayTeamStats.get(i).toString().split("-");
            int firstNumber = Integer.parseInt(parts[0]);
            int secondNumber = Integer.parseInt(parts[1]);
            String percentage = String.valueOf((double) firstNumber / secondNumber);
            awayTeamStats.set(i, percentage);
        }
    }

    public ArrayList<String> setDifferentialStats(JsonNode gameJSON){
        System.out.println(justUrl);
        setHomeTeamStats(gameJSON);
        setAwayTeamStats(gameJSON);

        differentialStats.add(homeTeamStats.getFirst());
        differentialStats.add(awayTeamStats.getFirst());

        awayTeamStats.removeIf(String::isEmpty);
        homeTeamStats.removeIf(String::isEmpty);

        for(int i = 1; i < homeTeamStats.size(); i ++ ){
            double awayStat = Double.parseDouble(awayTeamStats.get(i).toString());
            double homeStat = Double.parseDouble(homeTeamStats.get(i).toString());

            double differential = homeStat - awayStat;
            double rounded = Math.round(differential * 10000.0) / 10000.0;
            differentialStats.add(String.valueOf(rounded));
        }


//        System.out.println(differentialStats);
        return differentialStats;
    }



}

