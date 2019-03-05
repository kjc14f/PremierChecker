package com.model;

import java.time.LocalDateTime;
import java.util.List;

public class Gameweek {

    private List<Fixture> gameweeks;
    private LocalDateTime deadline;

    public Gameweek(List<Fixture> gameweeks) {
        this.gameweeks = gameweeks;
    }

    public List<Fixture> getGameweeks() {
        return gameweeks;
    }

    public void setGameweeks(List<Fixture> gameweeks) {
        this.gameweeks = gameweeks;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }
}
