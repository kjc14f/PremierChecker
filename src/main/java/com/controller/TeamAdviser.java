package com.controller;

import com.model.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.controller.FPLUtil.makeFPLRequest;

public class TeamAdviser {

    private List<Player> players = new ArrayList<>();

    public TeamAdviser() {
        processPlayers();
    }

    private void processPlayers() {
        JSONArray ja = makeFPLRequest("elements");

        for (Object obj : ja) {
            JSONObject jo = (JSONObject)obj;

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

            players.add(new Player(name, cost, form, points, minutes, playerType, chancePlayingThis, chancePlayingNext,
                    costChange, influence, threat, ictIndex, team, news,
                    valueToCost, valueToICT, valueToMinute, weightedValue));
        }
    }

    public List<Player> getPlayers() {
        return players;
    }
}
