package com.controller;

import com.model.Pick;
import com.model.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.controller.FPLUtil.BASE_FPL_URL;
import static com.controller.FPLUtil.makeFPLRequest;

public class LeagueAdviser {

    private static final String MY_TEAM_ID = "717475";
    private List<Pick> picks;

    public List<Pick> getMyTeam() {
        JSONObject teamData = (JSONObject) makeFPLRequest(BASE_FPL_URL + "my-team/" + MY_TEAM_ID, false, true);
        JSONArray pickArray = teamData.getJSONArray("picks");
        List<Pick> picks = new ArrayList<>();
        for (int i = 0; i < pickArray.length(); i++) {
            JSONObject jsonPick = pickArray.getJSONObject(i);
            Pick pick = new Pick(jsonPick.getInt("element"),
                    jsonPick.getInt("position"),
                    jsonPick.getInt("selling_price"),
                    jsonPick.getInt("multiplier"),
                    jsonPick.getInt("purchase_price"),
                    jsonPick.getBoolean("is_captain"),
                    jsonPick.getBoolean("is_vice_captain"));
            picks.add(pick);
        }
        this.picks = picks;
        return picks;
    }

    /**
     * Add a player object onto the results returned from the my-team request
     */
    public List<Pick> matchPlayers(Map<Integer, Player> players) {
        picks.stream().forEach(e -> e.setPlayer(players.get(e.getElement())));
        return picks;
    }

    public List<Pick> getPicks() {
        return picks;
    }
}
