package com.Model;

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
    private int differenceDifficulty;
    private int place;
    private int outOfPlace;
    String weeklyFixture;

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

    public Team(int id, String name, int awayStrength, int homeStrength, int homeAttackStrength, int homeDefenseStrength, int awayAttackStrength, int awayDefenseStrength, List<Fixture> fixtures, int strengthTotal, int difficultyTotal, int differenceDifficulty, int place, int outOfPlace) {
        this.id = id;
        this.name = name;
        this.awayStrength = awayStrength;
        this.homeStrength = homeStrength;
        this.homeAttackStrength = homeAttackStrength;
        this.homeDefenseStrength = homeDefenseStrength;
        this.awayAttackStrength = awayAttackStrength;
        this.awayDefenseStrength = awayDefenseStrength;
        this.fixtures = fixtures;
        this.strengthTotal = strengthTotal;
        this.difficultyTotal = difficultyTotal;
        this.differenceDifficulty = differenceDifficulty;
        this.place = place;
        this.outOfPlace = outOfPlace;
    }

    public Team(Team team) {
        this(team.getId(),
                team.getName(),
                team.getAwayStrength(),
                team.getHomeStrength(),
                team.getHomeAttackStrength(),
                team.getHomeDefenseStrength(),
                team.getAwayAttackStrength(),
                team.getAwayDefenseStrength(),
                team.getFixtures(),
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
}
