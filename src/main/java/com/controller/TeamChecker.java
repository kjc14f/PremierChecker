package com.controller;

import com.model.Fixture;
import com.model.Team;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.Main.DUMMY;
import static com.controller.Controller.*;
import static com.controller.FPLUtil.makeFPLRequest;

public class TeamChecker {

    private Map<Integer, Team> teams = new HashMap<>();
    Map<LocalDateTime, List<Fixture>> gameweeks = new TreeMap<>();

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
                List<Fixture> fix = (List<Fixture>) team.getGroupedFixtures().values().toArray()[i];
                if (fix.isEmpty()) {
                    strengthTotal += 1000;
                    difficultyTotal += 6;
                    differenceDifficulty += 0;
                } else {
                    for (Fixture singleFix : fix) {
                        strengthTotal += singleFix.getHomeTeam() == team.getId() ? team.getHomeStrength() : team.getAwayStrength();
                        difficultyTotal += singleFix.getHomeTeam() == team.getId() ? singleFix.getHomeDifficulty() : singleFix.getAwayDifficulty();
                        differenceDifficulty += singleFix.getHomeTeam() == team.getId() ? singleFix.getAwayDifficulty() - singleFix.getHomeDifficulty() : singleFix.getHomeDifficulty() - singleFix.getAwayDifficulty();
                    }
                }
            }
            List<Fixture> weeklyFix = (List<Fixture>) team.getGroupedFixtures().values().toArray()[future - 1];
            if (weeklyFix.isEmpty()) {
                team.setWeeklyFixture("BLANK");
            } else {
                for (Fixture fix : weeklyFix) {
                    if (fix.getHomeTeam() == team.getId()) {
                        team.setName(team.getName() + " (H)");
                        team.setWeeklyFixture(fix.getAwayTeamName() + " (+" + fix.getHomeDifficulty() + ")");
                    } else {
                        team.setName(team.getName() + " (A)");
                        team.setWeeklyFixture(fix.getHomeTeamName() + " (+" + fix.getAwayDifficulty() + ")");
                    }
                }
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

    private void extractFixtures() {
        JSONArray ja = makeFPLRequest("fixtures");
        List<Fixture> fixtures = new ArrayList<>();

        LocalDateTime now = DUMMY ? LocalDateTime.parse("2018-01-01T00:00:00"): LocalDateTime.now().plusWeeks(WEEK_OFFSET);
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
                homeTeam.getAllFixtures().add(fix);
                awayTeam.getAllFixtures().add(fix);

            } else {
                if (!jo.isNull("team_a_score") && jo.getInt("team_a_score") == 0) homeTeam.setCleanSheets(homeTeam.getCleanSheets() + 1);
                if (!jo.isNull("team_h_score") && jo.getInt("team_h_score") == 0) awayTeam.setCleanSheets(awayTeam.getCleanSheets() + 1);
                CURRENT_GAMEWEEK++;
            }
        }
        CURRENT_GAMEWEEK = (CURRENT_GAMEWEEK / 10) + 1;
        Collections.sort(fixtures, Comparator.comparing(Fixture::getDeadlineTime));
        Map<LocalDateTime, List<Fixture>> mixedGameweeks = new TreeMap<>(fixtures.stream().collect(Collectors.groupingBy(e -> e.getDeadlineTime())));

        for (Map.Entry<LocalDateTime, List<Fixture>> gameweek : mixedGameweeks.entrySet()) {
            for (Team team : teams.values()) {
                team.getGroupedFixtures().put(gameweek.getKey(), new ArrayList<>());
                team.getGroupedFixtures().put(gameweek.getKey(), gameweek.getValue().stream().filter(e -> e.getAwayTeam() == team.getId() || e.getHomeTeam() == team.getId()).collect(Collectors.toList()));
            }
        }

        if (GAMEDAYS + CURRENT_GAMEWEEK > 38) {
            GAMEDAYS = (38 - CURRENT_GAMEWEEK) + 1;
        }

    }

    public void getLeaguePlaces() {

        try {
            Document doc = DUMMY ? Jsoup.parse(new File(this.getClass().getClassLoader().getResource("historical/table.html").getFile()), "UTF-8", "http://example.com/") : Jsoup.connect("https://www.premierleague.com/tables").get();
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
                team.setGoalsScored(Integer.parseInt(htmlTeam.child(7).text()));
                team.setGoalsConceded(Integer.parseInt(htmlTeam.child(8).text()));
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

}
