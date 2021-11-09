package com.hastellc.workoutapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class History extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private ListView mWorkoutList;
    private ArrayAdapter<Workout> adapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mWorkoutList = findViewById(R.id.history_listView);
        adapter = new WorkoutAdapter(this, new ArrayList<Workout>());





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
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.workout_list_item, parent, false);
            }

            TextView workoutName = convertView.findViewById(R.id.workoutName);

            Workout w = workouts.get(position);
            workoutName.setText(w.getName());

            return convertView;
        }
    }
}