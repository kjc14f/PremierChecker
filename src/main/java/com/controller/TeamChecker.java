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
import static com.controller.FPLUtil.*;

public class TeamChecker {

    private Map<Integer, Team> teams = new HashMap<>();

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

    public List<Team> calculatePlays(int future, int starting) {

        List<Team> tempTeamList = new ArrayList<>();

        for (Team prototypeTeam : teams.values()) {
            Team team = new Team(prototypeTeam);
            int strengthTotal = 0;
            int difficultyTotal = 0;
            int differenceDifficulty = 0;
            for (int i = starting; i < future; i++) {
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
        JSONArray ja = FPL_DATA.getJSONArray("teams");

        for (Object obj : ja) {
            JSONObject jo = (JSONObject) obj;
            int id = jo.getInt("id");
            int code = jo.getInt("code");
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

            teams.put(id, new Team(id, code, name, shortName, strength, awayStrength, homeStrength, homeAttackStrength, homeDefenseStrength,
                    awayAttackStrength, awayDefenseStrength));
        }
    }

    private void extractFixtures() {
        JSONArray ja = (JSONArray) makeFPLRequest(BASE_FPL_URL + "fixtures", true, false);
        List<Fixture> fixtures = new ArrayList<>();

        TimeZone timeZone = Calendar.getInstance().getTimeZone();

        LocalDateTime now = DUMMY ? LocalDateTime.parse("2018-01-01T00:00:00"): LocalDateTime.now().plusWeeks(WEEK_OFFSET);
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssz");

        for (Object obj : ja) {
            JSONObject jo = (JSONObject) obj;
            //TODO change back to deadline_time?
            LocalDateTime deadline = LocalDateTime.parse(matchDeadlineTime(jo.isNull("event") ? -1 : jo.getInt("event")), formatter);
            if (timeZone.getDisplayName().equals("Greenwich Mean Time")) {
                deadline = deadline.plusHours(1);
            }
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
        CURRENT_GAMEWEEK = Math.round(((float)CURRENT_GAMEWEEK / 10) + 1);
        Collections.sort(fixtures, Comparator.comparing(Fixture::getDeadlineTime));
        Map<LocalDateTime, List<Fixture>> mixedGameweeks = new TreeMap<>(fixtures.stream().collect(Collectors.groupingBy(e -> e.getDeadlineTime())));

        for (Map.Entry<LocalDateTime, List<Fixture>> gameweek : mixedGameweeks.entrySet()) {
            for (Team team : teams.values()) {
                team.getGroupedFixtures().put(gameweek.getKey(), gameweek.getValue().stream().filter(e -> e.getAwayTeam() == team.getId() || e.getHomeTeam() == team.getId()).collect(Collectors.toList()));
            }
        }

        if (GAMEDAYS + CURRENT_GAMEWEEK > 38) {
            GAMEDAYS = (38 - CURRENT_GAMEWEEK) + 1;
        }

    }

    private String matchDeadlineTime(int eventNumber) {
        JSONArray ja = FPL_DATA.getJSONArray("events");
        for (Object obj : ja) {
            JSONObject jo = (JSONObject)obj;
            if (jo.getInt("id") == eventNumber) {
                return jo.getString("deadline_time");
            }
        }
        return "2100-01-01T12:00:00Z";
    }

    public void getLeaguePlaces() {

        try {
            Document doc = DUMMY ? Jsoup.parse(new File(this.getClass().getClassLoader().getResource("historical/end19/table.html").getFile()), "UTF-8", "http://example.com/") : Jsoup.connect("https://www.premierleague.com/tables").get();
            Elements tableRows = doc.getElementsByTag("tbody").get(0).children();

            for (int i = 0; i < tableRows.size(); i += 2) {
                Element htmlTeam = tableRows.get(i);

                Image image;
                String positionChangeString = htmlTeam.child(1).child(0).className();
                if (positionChangeString.contains("up")) {
                    image = new Image(getClass().getResource("/UpArrow.png").toURI().toString());
                } else if (positionChangeString.contains("down")) {
                    image = new Image(getClass().getResource("/DownArrow.png").toURI().toString());
                } else {
                    image = new Image(getClass().getResource("/NoChange.png").toURI().toString());
                }
                ImageView position = new ImageView(image);
                position.setPreserveRatio(true);
                position.setFitHeight(12);

                String shortName = htmlTeam.child(2).child(0).child(2).text();

                String finalName = shortName;
                Team team = teams.values().stream().filter(e -> e.getShortName().equals(finalName)).findFirst().orElse(null);

                if (team != null) {
                    team.setPlayed(Integer.parseInt(htmlTeam.child(3).text()));
                    team.setWins(Integer.parseInt(htmlTeam.child(4).text()));
                    team.setDraws(Integer.parseInt(htmlTeam.child(5).text()));
                    team.setLosses(Integer.parseInt(htmlTeam.child(6).text()));
                    team.setGoalsScored(Integer.parseInt(htmlTeam.child(7).text()));
                    team.setGoalsConceded(Integer.parseInt(htmlTeam.child(8).text()));
                    team.setPoints(Integer.parseInt(htmlTeam.child(10).text()));
                    team.setPositionChangeImage(position);
                } else {
                    System.err.println("Could not match name '" + finalName + "' to league table name");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<Integer, Team> getTeams() {
        return teams;
    }

}
