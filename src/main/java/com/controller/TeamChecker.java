package com.controller;

import com.Model.Fixture;
import com.Model.Team;
import javafx.collections.FXCollections;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.controller.Controller.BASE_URL;
import static com.controller.Controller.WEEK_OFFSET;

public class TeamChecker {

    private List<Team> teams = FXCollections.observableArrayList();
    private int gameWeeks = 0;

    public TeamChecker() {
        processTeams();
        matchFixtures(extractFixtures());
    }

    public void calculatePlaces(List<Team> teams) {
        for (Team team : teams) {
            team.setPlace(teams.indexOf(team) + 1);
        }
        List<Integer> tempTeamsDifficulty = new ArrayList<>();
        for (Team team : teams) {
            if (!tempTeamsDifficulty.contains(team.getDifficultyTotal())) {
                tempTeamsDifficulty.add(team.getDifficultyTotal());
            }
        }
        List<Team> tempTeams = new ArrayList<>(teams);
        Collections.sort(tempTeams, Comparator.comparing(p -> p.getStrengthTotal()));
        for (Team team : teams) {
            team.setOutOfPlace(team.getPlace() - tempTeams.indexOf(team) - 1);
        }
    }

    public List<Team> calculatePlays(int future) {

        List<Team> tempTeamList = new ArrayList<>();

        for (Team prototypeTeam : teams) {
            Team team = new Team(prototypeTeam);
            int strengthTotal = 0;
            int difficultyTotal = 0;
            int differenceDifficulty = 0;
            for (int i = 0; i < future; i++) {
                Fixture fix = team.getFixtures().get(i);
                strengthTotal += fix.getHomeTeam() == team.getId() ? team.getHomeStrength() : team.getAwayStrength();
                difficultyTotal += fix.getHomeTeam() == team.getId() ? fix.getHomeDifficulty() : fix.getAwayDifficulty();
                differenceDifficulty += fix.getHomeTeam() == team.getId() ? fix.getAwayDifficulty() - fix.getHomeDifficulty() : fix.getHomeDifficulty() - fix.getAwayDifficulty();

            }
            Fixture weeklyFix = team.getFixtures().get(future - 1);
            if (weeklyFix.getHomeTeam() == team.getId()) {
                team.setName(team.getName() + " (H)");
                team.setWeeklyFixture(weeklyFix.getAwayTeamName() + " (+" + weeklyFix.getHomeDifficulty() + ")");
            } else {
                team.setName(team.getName() + " (A)");
                team.setWeeklyFixture(weeklyFix.getHomeTeamName() + " (+" + weeklyFix.getAwayDifficulty() + ")");
            }

            team.setStrengthTotal(strengthTotal);
            team.setDifficultyTotal(difficultyTotal);
            team.setDifferenceDifficulty(differenceDifficulty);
            tempTeamList.add(team);
        }

        Collections.sort(tempTeamList, Comparator.comparing(Team::getDifficultyTotal).reversed().thenComparing(Team::getStrengthTotal));
        return tempTeamList;
    }

    private void processTeams() {
        JSONArray ja = makeRequest("teams");

        for (Object obj : ja) {
            JSONObject jo = (JSONObject) obj;
            int id = jo.getInt("id");
            String name = jo.getString("name");
            int awayStrength = jo.getInt("strength_overall_away");
            int homeStrength = jo.getInt("strength_overall_home");
            int homeAttackStrength = jo.getInt("strength_attack_home");
            int homeDefenseStrength = jo.getInt("strength_defence_home");
            int awayAttackStrength = jo.getInt("strength_attack_away");
            int awayDefenseStrength = jo.getInt("strength_defence_away");

            teams.add(new Team(id, name, awayStrength, homeStrength, homeAttackStrength, homeDefenseStrength,
                    awayAttackStrength, awayDefenseStrength));
        }
    }

    private List<Fixture> extractFixtures() {
        JSONArray ja = makeRequest("fixtures");
        List<Fixture> fixtures = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now().plusWeeks(WEEK_OFFSET);
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssz");

        for (Object obj : ja) {
            JSONObject jo = (JSONObject) obj;
            LocalDateTime deadline = LocalDateTime.parse(jo.getString("deadline_time"), formatter);

            if (now.isBefore(deadline)) {
                int homeTeam = jo.getInt("team_h");
                int awayTeam = jo.getInt("team_a");
                int homeDifficulty = jo.getInt("team_h_difficulty");
                int awayDifficulty = jo.getInt("team_a_difficulty");
                fixtures.add(new Fixture(homeTeam, awayTeam, deadline, homeDifficulty, awayDifficulty));
            } else {
                gameWeeks++;
            }
        }
        gameWeeks = (gameWeeks / 10) + 1;
        Collections.sort(fixtures, Comparator.comparing(Fixture::getDeadlineTime));
        return fixtures;
    }

    private void matchFixtures(List<Fixture> fixtures) {

        for (Fixture fix : fixtures) {

            for (Team team : teams) {

                if (fix.getHomeTeam() == team.getId()) {
                    fix.setHomeTeamName(team.getName());
                    team.getFixtures().add(fix);

                } else if (fix.getAwayTeam() == team.getId()) {
                    fix.setAwayTeamName(team.getName());
                    team.getFixtures().add(fix);
                }
            }
        }
    }

    private JSONArray makeRequest(String parameter) {
        try {
            URL url = new URL(BASE_URL + parameter);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            con.disconnect();
            return new JSONArray(content.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public int getGameWeeks() {
        return gameWeeks;
    }
}