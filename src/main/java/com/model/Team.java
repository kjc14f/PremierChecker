package com.model;

import javafx.scene.image.ImageView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Team {

    private int id;
    private String name;
    private String shortName;
    private int strength;
    private int awayStrength;
    private int homeStrength;
    private int homeAttackStrength;
    private int homeDefenseStrength;
    private int awayAttackStrength;
    private int awayDefenseStrength;
    private List<Fixture> allFixtures = new ArrayList<>();
    private Map<LocalDateTime, List<Fixture>> groupedFixtures = new TreeMap<>();
    private int strengthTotal;
    private int difficultyTotal;
    private int differenceDifficulty;
    private int place;
    private int outOfPlace;
    private String weeklyFixture;

    // League Table Data
    private int played;
    private int wins;
    private int draws;
    private int losses;
    private int goalsScored;
    private int goalsConceded;
    private int points;
    private ImageView positionChangeImage;
    private int cleanSheets;

    public Team(int id, String name, String shortName, int strength, int awayStrength, int homeStrength, int homeAttackStrength, int homeDefenseStrength, int awayAttackStrength, int awayDefenseStrength) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.strength = strength;
        this.awayStrength = awayStrength;
        this.homeStrength = homeStrength;
        this.homeAttackStrength = homeAttackStrength;
        this.homeDefenseStrength = homeDefenseStrength;
        this.awayAttackStrength = awayAttackStrength;
        this.awayDefenseStrength = awayDefenseStrength;
    }

    public Team(int id, String name, String shortName, int strength, int awayStrength, int homeStrength, int homeAttackStrength, int homeDefenseStrength, int awayAttackStrength, int awayDefenseStrength, Map<LocalDateTime, List<Fixture>> groupedFixtures, int strengthTotal, int difficultyTotal, int differenceDifficulty, int place, int outOfPlace) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.strength = strength;
        this.awayStrength = awayStrength;
        this.homeStrength = homeStrength;
        this.homeAttackStrength = homeAttackStrength;
        this.homeDefenseStrength = homeDefenseStrength;
        this.awayAttackStrength = awayAttackStrength;
        this.awayDefenseStrength = awayDefenseStrength;
        this.groupedFixtures = groupedFixtures;
        this.strengthTotal = strengthTotal;
        this.difficultyTotal = difficultyTotal;
        this.differenceDifficulty = differenceDifficulty;
        this.place = place;
        this.outOfPlace = outOfPlace;
    }

    public Team(Team team) {
        this(team.getId(),
                team.getName(),
                team.getShortName(),
                team.getStrength(),
                team.getAwayStrength(),
                team.getHomeStrength(),
                team.getHomeAttackStrength(),
                team.getHomeDefenseStrength(),
                team.getAwayAttackStrength(),
                team.getAwayDefenseStrength(),
                team.getGroupedFixtures(),
                team.getStrengthTotal(),
                team.getDifficultyTotal(),
                team.getDifferenceDifficulty(),
                team.getPlace(),
                team.getOutOfPlace());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getAwayStrength() {
        return awayStrength;
    }

    public void setAwayStrength(int awayStrength) {
        this.awayStrength = awayStrength;
    }

    public int getHomeStrength() {
        return homeStrength;
    }

    public void setHomeStrength(int homeStrength) {
        this.homeStrength = homeStrength;
    }

    public int getHomeAttackStrength() {
        return homeAttackStrength;
    }

    public void setHomeAttackStrength(int homeAttackStrength) {
        this.homeAttackStrength = homeAttackStrength;
    }

    public int getHomeDefenseStrength() {
        return homeDefenseStrength;
    }

    public void setHomeDefenseStrength(int homeDefenseStrength) {
        this.homeDefenseStrength = homeDefenseStrength;
    }

    public int getAwayAttackStrength() {
        return awayAttackStrength;
    }

    public void setAwayAttackStrength(int awayAttackStrength) {
        this.awayAttackStrength = awayAttackStrength;
    }

    public int getAwayDefenseStrength() {
        return awayDefenseStrength;
    }

    public void setAwayDefenseStrength(int awayDefenseStrength) {
        this.awayDefenseStrength = awayDefenseStrength;
    }

    public List<Fixture> getAllFixtures() {
        return allFixtures;
    }

    public void setAllFixtures(List<Fixture> allFixtures) {
        this.allFixtures = allFixtures;
    }

    public Map<LocalDateTime, List<Fixture>> getGroupedFixtures() {
        return groupedFixtures;
    }

    public void setGroupedFixtures(Map<LocalDateTime, List<Fixture>> groupedFixtures) {
        this.groupedFixtures = groupedFixtures;
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

    public int getDifferenceDifficulty() {
        return differenceDifficulty;
    }

    public void setDifferenceDifficulty(int differenceDifficulty) {
        this.differenceDifficulty = differenceDifficulty;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public int getOutOfPlace() {
        return outOfPlace;
    }

    public void setOutOfPlace(int outOfPlace) {
        this.outOfPlace = outOfPlace;
    }

    public String getWeeklyFixture() {
        return weeklyFixture;
    }

    public void setWeeklyFixture(String weeklyFixture) {
        this.weeklyFixture = weeklyFixture;
    }

    public int getPlayed() {
        return played;
    }

    public void setPlayed(int played) {
        this.played = played;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getGoalsScored() {
        return goalsScored;
    }

    public void setGoalsScored(int goalsScored) {
        this.goalsScored = goalsScored;
    }

    public int getGoalsConceded() {
        return goalsConceded;
    }

    public void setGoalsConceded(int goalsConceded) {
        this.goalsConceded = goalsConceded;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public ImageView getPositionChangeImage() {
        return positionChangeImage;
    }

    public void setPositionChangeImage(ImageView positionChangeImage) {
        this.positionChangeImage = positionChangeImage;
    }

    public int getCleanSheets() {
        return cleanSheets;
    }

    public void setCleanSheets(int cleanSheets) {
        this.cleanSheets = cleanSheets;
    }
}
