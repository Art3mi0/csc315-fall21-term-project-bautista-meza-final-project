package com.hastellc.workoutapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ChadWorkout extends AppCompatActivity {

    private final FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private static final String TAG = "ChadWorkout";

    public final static String WORKOUT_KEY = "";
    private ListView mWorkoutList;
    private EditText mFavoriteName;
    private Button mFavoriteButton;
    private ArrayList<Workout> workouts;

    private String COLLECTION;

    private ArrayAdapter<Workout> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chad_workout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFavoriteName = findViewById(R.id.favorite_text);
        mFavoriteButton= findViewById(R.id.favorite_button);
        mWorkoutList = findViewById(R.id.workoutListView);
        mAuth = FirebaseAuth.getInstance();

        adapter = new ChadWorkout.WorkoutAdapter(this, new ArrayList<Workout>());
        mWorkoutList.setAdapter(adapter);

        workouts = MainActivity.mRandomWorkout;

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

    public void onFavorite(View view) {
        if (checkText()) {
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();

        String email = user.getEmail();
        String name = mFavoriteName.getText().toString();
        COLLECTION = email + "'s Favorites";
        Favorite favorite = new Favorite(name);

        Log.d(TAG, "Submitted name: " + favorite.getName());
        mDb.collection(COLLECTION)
                .add(favorite)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Workout added successfully.");
                        Toast.makeText(ChadWorkout.this,
                                "Workout added!",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Could not add workout!");
                        Toast.makeText(ChadWorkout.this,
                                "Failed to add workout!",
                                Toast.LENGTH_SHORT).show();
                    }
                });

        COLLECTION = email + " " + name;
        Workout w;
        for (int i = 0; i < workouts.size(); i++) {
            w = workouts.get(i);
            mDb.collection(COLLECTION)
                    .add(w)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "Workout added successfully.");
                            Toast.makeText(ChadWorkout.this,
                                    "Workout added!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Could not add workout!");
                            Toast.makeText(ChadWorkout.this,
                                    "Failed to add workout!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private boolean checkText() {
        String text = mFavoriteName.getText().toString();

        if (!text.isEmpty()) {
            return false;
        } else {
            Toast.makeText(ChadWorkout.this, "Name can't be empty", Toast.LENGTH_LONG).show();
            return true;
        }
    }

}