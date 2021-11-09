package com.hastellc.workoutapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class ChadWorkout extends AppCompatActivity {

    public final static String WORKOUT_KEY = "";
    private ListView mWorkoutList;

    private ArrayAdapter<Workout> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chad_workout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mWorkoutList = findViewById(R.id.workoutListView);

        adapter = new ChadWorkout.WorkoutAdapter(this, new ArrayList<Workout>());
        mWorkoutList.setAdapter(adapter);

        ArrayList<Workout> workouts = MainActivity.mRandomWorkout;

        adapter.clear();
        adapter.addAll(workouts);

        AdapterView.OnItemClickListener itemClickListener =
                new AdapterView.OnItemClickListener(){
                    public void onItemClick(AdapterView<?> listView,
                                            View itemView,
                                            int position,
                                            long id) {

                            Intent intent = new Intent(ChadWorkout.this,
                                    Tutorials.class);
                        //intent.putExtra(Tutorials., (int) id);
                        startActivity(intent);
                    }
                };
        mWorkoutList.setOnItemClickListener(itemClickListener);

    }

    class WorkoutAdapter extends ArrayAdapter<Workout> {

        ArrayList<Workout> workouts;
        WorkoutAdapter(Context context, ArrayList<Workout> workouts) {
            super(context, 0, workouts);
            this.workouts = workouts;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.generated_workout_list_item, parent, false);
            }

            TextView workoutName = convertView.findViewById(R.id.workoutName);
            TextView workoutReps = convertView.findViewById(R.id.workoutReps);
            TextView workoutEquipment = convertView.findViewById(R.id.workoutEquipment);

            Workout w = workouts.get(position);
            workoutName.setText(w.getName());
            workoutReps.setText(w.getReps());
            workoutEquipment.setText(w.getEquipment());

            return convertView;
        }
    }



}