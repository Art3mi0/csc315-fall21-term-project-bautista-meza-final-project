package com.hastellc.workoutapp;

import androidx.annotation.NonNull;

public class Workout {

    private String type;
    private String name;
    private String reps;
    private String equipment;

    public Workout() {
    }

    public Workout(String type, String name, String reps, String equipment) {
        this.type = type;
        this.name = name;
        this.reps = reps;
        this.equipment = equipment;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getReps() {
        return reps;
    }



    @NonNull
    @Override
    public String toString() {return this.name + " " + this.reps;}
}
