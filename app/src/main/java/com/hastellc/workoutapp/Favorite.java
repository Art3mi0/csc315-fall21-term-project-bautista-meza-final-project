package com.hastellc.workoutapp;

import androidx.annotation.NonNull;

public class Favorite {

    private String name;


    public Favorite() {
    }

    public Favorite(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public String toString() {return this.name;}
}
