import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {

    List<Team> teams = new ArrayList<>();
    private static final int GAMEDAYS = 8;

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        getTeams();
        matchFixtures(getFixtures());

        for (int i = 1; i <= GAMEDAYS; i ++) {

            calculatePlays(i);
            calculatePlaces();

            System.out.println("***** GAMEDAY WEEK " + i + " *****");
            System.out.format("%24s%13s%13s%13s%13s%13s%13s%13s\n", "NAME", "STRENGTH", "DIFFICULTY", "NET PLACE",
                    "H ATTACK", "H DEFENSE", "A ATTACK", "A DEFENSE");
            System.out.format("%54s\n", "-----------------------------------------------------------------------------------------------------------------------");
            for (Team team : teams) {
                System.out.format("%24s%13d%13d%13d%13d%13d%13d%13d\n", team.getName(), team.getStrengthTotal(),
                        team.getDifficultyTotal(), team.getOutOfPlace(), team.getHomeAttackStrength(),
                        team.getHomeDefenseStrength(), team.getAwayAttackStrength(), team.getAwayDefenseStrength());
            }
            System.out.println("\n\n\n");
        }
    }

    public void calculatePlaces() {
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

    public void calculatePlays(int future) {
        for (Team team : teams) {
            int strengthTotal = 0;
            int difficultyTotal = 0;
            for (int i = 0; i < future; i++) {
                Fixture fix = team.getFixtures().get(i);
                strengthTotal += fix.getHomeTeam() == team.getId() ? team.getHomeStrength() : team.getAwayStrength();
                difficultyTotal += fix.getHomeTeam() == team.getId() ? fix.getAwayDifficulty() - fix.getHomeDifficulty() : fix.getHomeDifficulty() - fix.getAwayDifficulty();
            }
            team.setStrengthTotal(strengthTotal);
            team.setDifficultyTotal(difficultyTotal);
        }

        Collections.sort(teams, Comparator.comparing(Team::getDifficultyTotal).thenComparing(Team::getStrengthTotal));

    }

    public void getTeams() {
        JSONArray ja = makeRequest("teams");

        for (Object obj : ja) {
            JSONObject jo = (JSONObject)obj;
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

    public List<Fixture> getFixtures() {
        JSONArray ja = makeRequest("fixtures");
        List<Fixture> fixtures = new ArrayList<>();

        for (Object obj : ja) {
            JSONObject jo = (JSONObject)obj;
            LocalDateTime now = LocalDateTime.now();
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssz");
            LocalDateTime deadline = LocalDateTime.parse(jo.getString("deadline_time"), formatter);

            if (now.isBefore(deadline)) {
                int homeTeam = jo.getInt("team_h");
                int awayTeam = jo.getInt("team_a");
                LocalDateTime deadlineTime = deadline;
                int homeDifficulty = jo.getInt("team_h_difficulty");
                int awayDifficulty = jo.getInt("team_a_difficulty");
                fixtures.add(new Fixture(homeTeam, awayTeam, deadlineTime, homeDifficulty, awayDifficulty));
            }
        }
        Collections.sort(fixtures, Comparator.comparing(Fixture::getDeadlineTime));
        return fixtures;
    }

    public void matchFixtures(List<Fixture> fixtures) {
        for (Fixture fix : fixtures) {
            for (Team team : teams) {
                if (fix.getHomeTeam() == team.getId() || fix.getAwayTeam() == team.getId()) {
                    team.getFixtures().add(fix);
                }
            }
        }
    }

    public JSONArray makeRequest(String parameter) {
        try {
            URL url = new URL("https://fantasy.premierleague.com/drf/" + parameter);
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

}
