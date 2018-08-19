import java.util.ArrayList;
import java.util.List;

public class Team {

    private int id;
    private String name;
    private int awayStrength;
    private int homeStrength;
    private int homeAttackStrength;
    private int homeDefenseStrength;
    private int awayAttackStrength;
    private int awayDefenseStrength;
    private List<Fixture> fixtures = new ArrayList<>();
    private int strengthTotal;
    private int difficultyTotal;

    public Team(int id, String name, int awayStrength, int homeStrength, int homeAttackStrength, int homeDefenseStrength, int awayAttackStrength, int awayDefenseStrength) {
        this.id = id;
        this.name = name;
        this.awayStrength = awayStrength;
        this.homeStrength = homeStrength;
        this.homeAttackStrength = homeAttackStrength;
        this.homeDefenseStrength = homeDefenseStrength;
        this.awayAttackStrength = awayAttackStrength;
        this.awayDefenseStrength = awayDefenseStrength;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAwayStrength() {
        return awayStrength;
    }

    public int getHomeStrength() {
        return homeStrength;
    }

    public int getHomeAttackStrength() {
        return homeAttackStrength;
    }

    public int getHomeDefenseStrength() {
        return homeDefenseStrength;
    }

    public int getAwayAttackStrength() {
        return awayAttackStrength;
    }

    public int getAwayDefenseStrength() {
        return awayDefenseStrength;
    }

    public List<Fixture> getFixtures() {
        return fixtures;
    }

    public void setFixtures(List<Fixture> fixtures) {
        this.fixtures = fixtures;
    }

    public int getStrengthTotal() {
        return strengthTotal;
    }

    public void setStrengthTotal(int strengthTotal) {
        this.strengthTotal = strengthTotal;
    }

    public int getDifficultyTotal() {
        return difficultyTotal;
    }

    public void setDifficultyTotal(int difficultyTotal) {
        this.difficultyTotal = difficultyTotal;
    }
}
