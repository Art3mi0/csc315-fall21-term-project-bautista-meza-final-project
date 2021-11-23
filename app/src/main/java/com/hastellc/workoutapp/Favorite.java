package com.hastellc.workoutapp;

import androidx.annotation.NonNull;

public class Favorite {

    private String name;
    private String workout1;
    private String workout2;
    //private String workout3;
    //private String workout4;


    public Favorite() {
    }

    public Favorite(String name, String workout1, String workout2) {
        this.name = name;
        this.workout1 = workout1;
        this.workout2 = workout2;
        //this.workout3 = workout3;
        //this.workout4 = workout4;
    }

    public String getName() {
        return name;
    }

    public String getWorkout1() {
        return workout1;
    }

    public String getWorkout2() {
        return workout2;
    }

//    public String getWorkout3() {
//        return workout3;
//    }
//
//    public String getWorkout4() {
//        return workout4;
//    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {return this.name;}
}
