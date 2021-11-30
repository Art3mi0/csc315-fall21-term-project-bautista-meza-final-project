package com.hastellc.workoutapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;

public class ChadWorkout extends AppCompatActivity {

    private final FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private static final String TAG = "ChadWorkout";

    public final static String WORKOUT_KEY = "";
    public final static String FAVORITE_KEY = "name";
    private ListView mWorkoutList;
    private EditText mFavoriteName;
    private Button mFavoriteButton;
    private Button mDeleteButton;
    private ArrayList<Workout> mWorkouts;
    private ArrayList<Favorite> favorites;
    private ArrayList<Workout> mChosenWorkouts;
    private ArrayList<String> mExtra;

    private String COLLECTION;
    private Boolean nameCheck;
    private String email;
    private String favName;
    private int once;
    private String docId;
    private Favorite f;

    private ArrayAdapter<Workout> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chad_workout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("*Unnamed Workout");

        mFavoriteName = findViewById(R.id.favorite_text);
        mFavoriteButton = findViewById(R.id.favorite_button);
        mDeleteButton = findViewById(R.id.delete_workout);
        mWorkoutList = findViewById(R.id.workoutListView);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        email = user.getEmail();

        adapter = new ChadWorkout.WorkoutAdapter(this, new ArrayList<Workout>());
        mWorkoutList.setAdapter(adapter);

        mExtra = getIntent().getStringArrayListExtra(WORKOUT_KEY);
        favName = getIntent().getStringExtra(FAVORITE_KEY);
        if (favName != null) {
            getSupportActionBar().setTitle(favName);
            mDeleteButton.setVisibility(View.VISIBLE);
            mFavoriteButton.setText("Update Workout Name");
        }
        getWorkouts();
        once = 1;

        AdapterView.OnItemClickListener itemClickListener =
                new AdapterView.OnItemClickListener(){
                    public void onItemClick(AdapterView<?> listView,
                                            View itemView,
                                            int position,
                                            long id) {

                            Intent intent = new Intent(ChadWorkout.this,
                                    Tutorials.class);

                        String name = mChosenWorkouts.get(position).getName();
                        intent.putExtra(Tutorials.EXTRASTUFF, name);
                        startActivity(intent);
                    }
                };
        mWorkoutList.setOnItemClickListener(itemClickListener);

        String email = user.getEmail();
        COLLECTION = email + "'s Favorites";

        favorites = new ArrayList<Favorite>();
        mDb.collection(COLLECTION)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Favorite favorite = document.toObject(Favorite.class);
                            favorites.add(favorite);
                        }
                    }
                });

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

    public void getWorkouts() {
        if (once != 1) {
            mDb.collection("workouts")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            mWorkouts = new ArrayList<>();
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Workout w = document.toObject(Workout.class);
                                mWorkouts.add(w);
                                Log.d(TAG, w.getType() + " " + w.getName() + " " + w.getReps());
                            }
                            int count = 0;
                            mChosenWorkouts = new ArrayList<>();
                            getWorkouts();
                            while (count < mExtra.size()) {
                                for (int i = 0; i < mWorkouts.size(); i++) {
                                    if (mExtra.get(count).equals(mWorkouts.get(i).getName())) {
                                        mChosenWorkouts.add(mWorkouts.get(i));
                                        count++;
                                        break;
                                    }
                                }
                            }

                            adapter.clear();
                            adapter.addAll(mChosenWorkouts);
                        }
                    });
        }
    }

    public void onDelete(View view) {
        mDb.collection(COLLECTION)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Favorite f = document.toObject(Favorite.class);
                            if (f.getName().equals(favName)) {
                                docId = document.getId();
                                break;
                            }
                        }
                        mDb.collection(COLLECTION).document(docId).delete();
                        Toast.makeText(getApplicationContext(),"Deleted " + favName + " from favorites", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ChadWorkout.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
    }

    public void onFavorite(View view) {
        if (checkText()) {
            return;
        }

        String name = mFavoriteName.getText().toString();
        if (checkName(name)) {
            Toast.makeText(ChadWorkout.this, "You already named a workout this", Toast.LENGTH_LONG).show();
            return;
        }
        Favorite favorite = new Favorite(name, mChosenWorkouts.get(0).getName(), mChosenWorkouts.get(1).getName(),
                mChosenWorkouts.get(2).getName(), mChosenWorkouts.get(3).getName());

        if (favName != null) {
            String oldName = (String) getSupportActionBar().getTitle();
            getSupportActionBar().setTitle(name);
            invalidateOptionsMenu();
            for (int i = 0; i < favorites.size(); i ++) {
                if (oldName.equals(favorites.get(i).getName())) {
                    favorites.remove(i);
                    break;
                }
            }
            favorites.add(favorite);

            mDb.collection(COLLECTION)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                f = document.toObject(Favorite.class);
                                if (f.getName().equals(oldName)) {
                                    docId = document.getId();
                                    break;
                                }
                            }
                            mDb.collection(COLLECTION).document(docId).set(favorite);
                            Toast.makeText(getApplicationContext(),"Updated " + f.getName() + " from favorites" +
                                    " to " + name, Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            favorites.add(favorite);
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
            favName = favorite.getName();
            mDeleteButton.setVisibility(View.VISIBLE);
            mFavoriteButton.setText("Update Workout Name");
            getSupportActionBar().setTitle(favName);
            invalidateOptionsMenu();
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

    private boolean checkName(String name) {
        nameCheck = false;
        for (int i = 0; i < favorites.size(); i ++) {
            Log.d(TAG, "Checking if " + name + " = " + favorites.get(i));
            if (name.equals(favorites.get(i).getName())) {
                nameCheck = true;
            }
        }

        return nameCheck;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}