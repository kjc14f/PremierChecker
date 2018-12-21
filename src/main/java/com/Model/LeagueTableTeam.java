package com.Model;

import javafx.scene.image.ImageView;

public class LeagueTableTeam {

    private String name;
    private int played;
    private int wins;
    private int draws;
    private int losses;
    private int goalsFor;
    private int goalsAgainst;
    private int points;
    private ImageView positionChangeImage;

    public LeagueTableTeam(String name, int played, int wins, int draws, int losses, int goalsFor, int goalsAgainst, int points, ImageView positionChangeImage) {
        this.name = name;
        this.played = played;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
        this.goalsFor = goalsFor;
        this.goalsAgainst = goalsAgainst;
        this.points = points;
        this.positionChangeImage = positionChangeImage;
    }

    public String getName() {
        return name;
    }

    public int getPlayed() {
        return played;
    }

    public int getWins() {
        return wins;
    }

    public int getDraws() {
        return draws;
    }

    public int getLosses() {
        return losses;
    }

    public int getGoalsFor() {
        return goalsFor;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public int getPoints() {
        return points;
    }

    public ImageView getPositionChangeImage() {
        return positionChangeImage;
    }
}
