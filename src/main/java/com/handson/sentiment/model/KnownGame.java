package com.handson.sentiment.model;

import java.io.Serializable;

public enum KnownGame implements Serializable {
    Hangman("Hangman: A word-guessing game where players try to guess a hidden word by suggesting letters."),

    Memory("Memory: A card game where players flip over cards to find matching pairs."),

    Charades("Charades: A game where players act out words or phrases without speaking for others to guess."),

    Bingo("Bingo: A game where players mark off numbers on a card as they are randomly called out."),

    ScavengerHunt("Scavenger Hunt: A game where players search for a list of specific items or complete tasks.");

    public final String activityDescription;

    KnownGame(String activityDescription) {
        this.activityDescription = activityDescription;
    }

    public String getActivityDescription() {
        return activityDescription;
    }
}