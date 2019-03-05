package com.model;

import java.time.LocalDateTime;

public class Fixture {

    private int homeTeam;
    private int awayTeam;
    private String homeTeamName;
    private String awayTeamName;
    private LocalDateTime deadlineTime;
    private int homeDifficulty;
    private int awayDifficulty;

    public Fixture(int homeTeam, int awayTeam, LocalDateTime deadlineTime, int homeDifficulty, int awayDifficulty, String homeTeamName, String awayTeamName) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.deadlineTime = deadlineTime;
        this.homeDifficulty = homeDifficulty;
        this.awayDifficulty = awayDifficulty;
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
    }

    public int getHomeTeam() {
        return homeTeam;
    }

    public int getAwayTeam() {
        return awayTeam;
    }

    public LocalDateTime getDeadlineTime() {
        return deadlineTime;
    }

    public int getHomeDifficulty() {
        return homeDifficulty;
    }

    public int getAwayDifficulty() {
        return awayDifficulty;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

}
