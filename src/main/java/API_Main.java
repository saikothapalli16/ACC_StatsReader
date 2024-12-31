import org.example.Main;

import java.io.FileWriter;
import java.net.URI;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class API_Main {
    public static void main(String[] args){
//        String url = "https://ncaa-api.henrygd.me/game/6292814";
//        SingleGameStats game1 = new SingleGameStats("/game/6292814");
//        game1.setDifferentialStats(game1.getGameJSON());

        List<ArrayList<String>> list = new ArrayList<>();
        Date start = new Date(2024, 11, 1);
        Date end = new Date(2024, 12, 31);

        GameURLList twentyThreeSeason = new GameURLList(start, end);



        for(String url: twentyThreeSeason.getUrlList()){

            SingleGameStats game1 = new SingleGameStats(url);
            list.add(game1.setDifferentialStats(game1.getGameJSON()));
        }

    //        System.out.println(list);

        String fileName = start.getMonth() + "_" + start.getYear() + "(1).csv";

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.append("Team1,Team2,FG % differential,3 pt % differential,FT % differential,Rebound differential, Offensive Rebound differential, Assists differential, Personal foul differentials, Steals differential,Turnovers differential, Blocks differential, Points differential\n");
            for (ArrayList<String> row : list) {
                // Join each ArrayList<String> as a CSV row
                writer.append(String.join(",", row));
                writer.append("\n");
            }
            System.out.println("CSV file " + fileName +  " written successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    }

