package com.Model;

import java.time.LocalDateTime;

public class Fixture {

    private int homeTeam;
    private int awayTeam;
    private String homeTeamName;
    private String awayTeamName;
    private LocalDateTime deadlineTime;
    private int homeDifficulty;
    private int awayDifficulty;

    public Fixture(int homeTeam, int awayTeam, LocalDateTime deadlineTime, int homeDifficulty, int awayDifficulty) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.deadlineTime = deadlineTime;
        this.homeDifficulty = homeDifficulty;
        this.awayDifficulty = awayDifficulty;
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

    public void setHomeTeamName(String homeTeamName) {
        this.homeTeamName = homeTeamName;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public void setAwayTeamName(String awayTeamName) {
        this.awayTeamName = awayTeamName;
    }
}
