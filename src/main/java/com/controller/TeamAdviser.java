package com.controller;

import com.model.Player;
import com.model.PlayerValue;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.controller.Controller.FPL_DATA;

public class TeamAdviser {

    private Map<Integer, Player> players = new HashMap<>();

    public TeamAdviser() {
        processPlayers();
    }

    private void processPlayers() {
        JSONArray ja = FPL_DATA.getJSONArray("elements");

        for (Object obj : ja) {
            JSONObject jo = (JSONObject)obj;

            int id = jo.getInt("id");
            String name = new String((jo.getString("first_name") + " " + jo.getString("second_name"))
                    .getBytes(), StandardCharsets.UTF_8);
            float cost = jo.getFloat("now_cost") / 10;
            float form = jo.getFloat("form");
            int points = jo.getInt("total_points");
            int minutes = jo.getInt("minutes");
            int playerType = jo.getInt("element_type");
            int chancePlayingThis = jo.isNull("chance_of_playing_this_round") ? -1 : jo.getInt("chance_of_playing_this_round");
            int chancePlayingNext = jo.isNull("chance_of_playing_next_round") ? -1 : jo.getInt("chance_of_playing_next_round");
            float costChange = jo.getFloat("cost_change_start") / 10;
            String influence = jo.getString("influence");
            String threat = jo.getString("threat");
            double ictIndex = Double.parseDouble(jo.getString("ict_index"));
            int team = jo.getInt("team");
            String news = jo.getString("news");
            int cleanSheets = jo.getInt("clean_sheets");
            int goalsConceded = jo.getInt("goals_conceded");
            int ownGoals = jo.getInt("own_goals");
            int penaltiesSaved = jo.getInt("penalties_saved");
            int penaltiesScored = jo.getInt("penalties_missed");
            int yellowCards = jo.getInt("yellow_cards");
            int redCards = jo.getInt("red_cards");
            int saves = jo.getInt("saves");
            int bonus = jo.getInt("bonus");

            double valueToCost = Math.round((points/cost) * 100);
            valueToCost /= 100;
            double valueToICT = Math.round((ictIndex/cost) * 100);
            valueToICT /= 100;
            double valueToMinute;
            if (points != 0) {
                valueToMinute = Math.round(((double) minutes / points) * 100);
                valueToMinute /= 100;
            } else {
                valueToMinute = 0;
            }

            double weightedValue;
            if (playerType == 1) { //Keeper
                weightedValue = points / (cost - 4);
            } else if (playerType == 2) { //Defender
                weightedValue = points / (cost - 5);
            } else if (playerType == 3) { //Midfielder
                weightedValue = points / (cost - 7);
            } else {
                weightedValue = points / (cost - 6.5);
            }
            weightedValue = Math.round(weightedValue * 100);
            weightedValue /= 100;

            players.put(id, new Player(name, cost, form, points, minutes, playerType, chancePlayingThis, chancePlayingNext,
                    costChange, influence, threat, ictIndex, team, news,
                    valueToCost, valueToICT, valueToMinute, weightedValue,
                    cleanSheets, goalsConceded, ownGoals, penaltiesSaved, penaltiesScored, yellowCards,
                    redCards, saves, bonus));
        }
    }

    public Map<PlayerValue, List<Player>> findGoodBadBuys() {

        try {
            File historyDirectory = new File(getClass().getClassLoader().getResource("pre-season/" + Year.now().toString()).toURI());

            List<Player> goodPrice = new ArrayList<>();
            List<Player> badPrice = new ArrayList<>();

            for (File playerFile : historyDirectory.listFiles()) {

                StringBuilder contentBuilder = new StringBuilder();
                try (Stream<String> stream = Files.lines(playerFile.toPath(), StandardCharsets.UTF_8)) {
                    stream.forEach(s -> contentBuilder.append(s).append("\n"));
                }

                String id = playerFile.getName().split("\\.")[0];

                JSONObject jo = new JSONObject(contentBuilder.toString());
                JSONArray ja = jo.getJSONArray("history_past");

                // New player in Premier League
                if (ja.length() == 0) {
                    continue;
                }

                JSONObject lastYear = ja.getJSONObject(ja.length() - 1);
                double oldPrice = lastYear.getDouble("end_cost") / 10;

                Player player = players.get(Integer.parseInt(id));
                double newPrice;
                if (player == null) {
                    newPrice = Double.MAX_VALUE;
                } else {
                    newPrice = player.getCost();
                }

                double difference = oldPrice - newPrice;

                if (difference >= 1) {
                    goodPrice.add(player);
                } else if (difference <= -1) {
                    badPrice.add(player);
                }
            }

            Map<PlayerValue, List<Player>> result = new HashMap();
            result.put(PlayerValue.GOOD, goodPrice);
            result.put(PlayerValue.BAD, badPrice);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Map<Integer, Player> getPlayers() {
        return players;
    }
}
