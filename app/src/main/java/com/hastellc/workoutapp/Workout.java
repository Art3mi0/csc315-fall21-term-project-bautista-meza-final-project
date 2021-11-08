package com.hastellc.workoutapp;

import androidx.annotation.NonNull;

public class Workout {

    private String type;
    private String name;
    private String reps;

    public Workout() {
    }

    public Workout(String type, String name, String reps) {
        this.type = type;
        this.name = name;
        this.reps = reps;
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

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReps(String reps) {
        this.reps = reps;
    }

    @NonNull
    @Override
    public String toString() {return this.name + " " + this.reps;}
}
