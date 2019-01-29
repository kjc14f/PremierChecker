package com.controller;

import com.Model.Fixture;
import com.Model.Team;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.controller.Controller.*;

public class TeamChecker {

    private Map<Integer, Team> teams = new HashMap<>();
    private int gameWeeks = 0;

    public TeamChecker() {
        processTeams();
        extractFixtures();
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

        for (Team prototypeTeam : teams.values()) {
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
        JSONArray ja = makeFPLRequest("teams");

        for (Object obj : ja) {
            JSONObject jo = (JSONObject) obj;
            int id = jo.getInt("id");
            String name = jo.getString("name");
            String shortName = jo.getString("short_name");
            int strength = jo.getInt("strength");
            int awayStrength = jo.getInt("strength_overall_away");
            int homeStrength = jo.getInt("strength_overall_home");
            int homeAttackStrength = jo.getInt("strength_attack_home");
            int homeDefenseStrength = jo.getInt("strength_defence_home");
            int awayAttackStrength = jo.getInt("strength_attack_away");
            int awayDefenseStrength = jo.getInt("strength_defence_away");

            if (name.equals("Spurs")) {
                name = "Tottenham";
            }

            teams.put(id, new Team(id, name, shortName, strength, awayStrength, homeStrength, homeAttackStrength, homeDefenseStrength,
                    awayAttackStrength, awayDefenseStrength));
        }
    }

    private List<Fixture> extractFixtures() {
        JSONArray ja = makeFPLRequest("fixtures");
        List<Fixture> fixtures = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now().plusWeeks(WEEK_OFFSET);
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssz");

        for (Object obj : ja) {
            JSONObject jo = (JSONObject) obj;
            LocalDateTime deadline = LocalDateTime.parse(jo.optString("deadline_time", "2100-01-01T12:00:00Z"), formatter);
            int homeTeamID = jo.getInt("team_h");
            int awayTeamID = jo.getInt("team_a");
            Team homeTeam = teams.get(homeTeamID);
            Team awayTeam = teams.get(awayTeamID);

            if (now.isBefore(deadline)) {
                int homeDifficulty = jo.getInt("team_h_difficulty");
                int awayDifficulty = jo.getInt("team_a_difficulty");
                Fixture fix = new Fixture(homeTeamID, awayTeamID, deadline, homeDifficulty, awayDifficulty, homeTeam.getName(), awayTeam.getName());
                fixtures.add(fix);
                homeTeam.getFixtures().add(fix);
                awayTeam.getFixtures().add(fix);
            } else {
                if (!jo.isNull("team_a_score") && jo.getInt("team_a_score") == 0) homeTeam.setCleanSheets(homeTeam.getCleanSheets() + 1);
                if (!jo.isNull("team_h_score") && jo.getInt("team_h_score") == 0) awayTeam.setCleanSheets(awayTeam.getCleanSheets() + 1);
                gameWeeks++;
            }
        }
        gameWeeks = (gameWeeks / 10) + 1;
        Collections.sort(fixtures, Comparator.comparing(Fixture::getDeadlineTime));
        return fixtures;
    }

    private JSONArray makeFPLRequest(String parameter) {
        try {
            URL url = new URL(BASE_FPL_URL + parameter);
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

    public void getLeaguePlaces() {

        try {
            Document doc = Jsoup.connect("https://www.premierleague.com/tables").get();
            Elements tableRows = doc.getElementsByTag("tbody").get(0).children();

            for (int i = 0; i < tableRows.size(); i += 2) {
                Element htmlTeam = tableRows.get(i);

                Image image;
                String positionChangeString = htmlTeam.child(1).child(1).className();
                if (positionChangeString.equals("movement up")) {
                    image = new Image(getClass().getResource("/UpArrow.png").toURI().toString());
                } else if (positionChangeString.equals("movement down")) {
                    image = new Image(getClass().getResource("/DownArrow.png").toURI().toString());
                } else {
                    image = new Image(getClass().getResource("/NoChange.png").toURI().toString());
                }
                ImageView position = new ImageView(image);
                position.setPreserveRatio(true);
                position.setFitHeight(12);

                String name = htmlTeam.child(2).child(0).child(1).text();
                if (name.equals("Manchester United")) {
                    name = "Man Utd";
                } else if (name.equals("Manchester City")) {
                    name = "Man City";
                } else if (name.equals("Wolverhampton Wanderers")) {
                    name = "Wolves";
                } else if (name.equals("West Ham United")) {
                    name = "West Ham";
                } else if (name.equals("Brighton and Hove Albion")) {
                    name = "Brighton";
                } else if (name.equals("Newcastle United")) {
                    name = "Newcastle";
                } else if (name.equals("Cardiff City")) {
                    name = "Cardiff";
                } else if (name.equals("Huddersfield Town")) {
                    name = "Huddersfield";
                } else if (name.equals("Tottenham Hotspur")) {
                    name = "Tottenham";
                } else if (name.equals("Leicester City")) {
                    name = "Leicester";
                }

                String finalName = name;
                Team team = teams.values().stream().filter(e -> e.getName().equals(finalName)).findFirst().get();

                team.setPlayed(Integer.parseInt(htmlTeam.child(3).text()));
                team.setWins(Integer.parseInt(htmlTeam.child(4).text()));
                team.setDraws(Integer.parseInt(htmlTeam.child(5).text()));
                team.setLosses(Integer.parseInt(htmlTeam.child(6).text()));
                team.setGoalsFor(Integer.parseInt(htmlTeam.child(7).text()));
                team.setGoalsAgainst(Integer.parseInt(htmlTeam.child(8).text()));
                team.setPoints(Integer.parseInt(htmlTeam.child(10).text()));
                team.setPositionChangeImage(position);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<Integer, Team> getTeams() {
        return teams;
    }

    public int getGameWeeks() {
        return gameWeeks;
    }

}
