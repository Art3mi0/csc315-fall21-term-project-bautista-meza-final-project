package com.hastellc.workoutapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "MainActivity";
    private String COLLECTION;

    private final FirebaseFirestore mDb = FirebaseFirestore.getInstance();

    private ArrayAdapter<Workout> adapterWorkouts;
    private ArrayAdapter<Favorite> adapterFavorites;

    private ConstraintLayout mLoggedInGroup;
    private ConstraintLayout mLoggedOutGroup;
    private TextView mNameLabel;
    private EditText mEmailField;
    private EditText mPasswordField;
    private ListView mWorkoutList;
    private ListView mFavoritesList;
    private ArrayList<Workout> mWorkouts;
    private ArrayList<Workout> mRandomWorkout;
    private ArrayList<Favorite> mFavorites;
    private ArrayList<String> mExtra;

    private boolean logoutItemView = true;



    ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                Boolean fineLocationGranted = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    fineLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_FINE_LOCATION, false);
                }
                Boolean coarseLocationGranted = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION, false);
                }

                if (fineLocationGranted != null && fineLocationGranted) {
                            // Precise location access granted.
                            Toast.makeText(this, "I can see precise location!", Toast.LENGTH_SHORT).show();
                        } else if (coarseLocationGranted != null && coarseLocationGranted) {
                            // Only approximate location access granted.
                            Toast.makeText(this, "I can see approximate location!", Toast.LENGTH_SHORT).show();
                        } else {
                            // No location access granted.
                            Toast.makeText(this, "I need permission to access location in order to record locations.", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private Intent intent;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        mLoggedInGroup = findViewById(R.id.logged_in_group);
        mLoggedOutGroup = findViewById(R.id.logged_out_group);
        mNameLabel = findViewById(R.id.hello);
        mEmailField = findViewById(R.id.email);
        mPasswordField = findViewById(R.id.password);
        mWorkoutList = findViewById(R.id.workoutListView);
        mFavoritesList = findViewById(R.id.favoriteListView);

        adapterWorkouts = new WorkoutAdapter(this, new ArrayList<Workout>());
        adapterFavorites = new FavoriteAdapter(this, new ArrayList<Favorite>());
        mWorkoutList.setAdapter(adapterWorkouts);
        mFavoritesList.setAdapter(adapterFavorites);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        Button recordLocationBtn = findViewById(R.id.gym_map);
        recordLocationBtn.setOnClickListener(this::findGyms);
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

    class FavoriteAdapter extends ArrayAdapter<Favorite> {

        ArrayList<Favorite> favoritesList;
        FavoriteAdapter(Context context, ArrayList<Favorite> favoritesList) {
            super(context, 0, favoritesList);
            this.favoritesList = favoritesList;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.favorite_list_item, parent, false);
            }

            TextView favoriteName = convertView.findViewById(R.id.favoriteName);

            Favorite f = favoritesList.get(position);
            favoriteName.setText(f.getName());

            return convertView;
        }
    }

    public void getWorkouts() {
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
                    }
                });
    }

    public void getFavorites() {
        mDb.collection(COLLECTION)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public  void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mFavorites = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Favorite f = document.toObject(Favorite.class);
                            mFavorites.add(f);
                            Log.d(TAG, f.getName());
                        }
                        adapterFavorites.clear();
                        if (mFavorites != null) {
                            adapterFavorites.addAll(mFavorites);
                        }
                    }
                }
                );
    }

    public void setFavoriteAdapter() {
        adapterFavorites.clear();
        if (mFavorites != null) {
            adapterFavorites.addAll(mFavorites);
        }
    }


    public void findGyms(View view) {
        boolean hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        boolean hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        if (!hasCoarseLocationPermission && !hasFineLocationPermission) {
            Toast.makeText(this, "I need permission to access location in order to record locations.", Toast.LENGTH_SHORT).show();
            locationPermissionRequest.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
        } else {
            intent = new Intent(this, GymMaps.class);

//            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(
//                    this,
//                    new OnSuccessListener<Location>() {
//                        @Override
//                        public void onSuccess(Location location) {
//                            double latitude = location.getLatitude();
//                            double longitude = location.getLongitude();
//
//
//                            intent.putExtra(GymMaps.LAT , latitude);
//                            intent.putExtra(GymMaps.LONG, longitude);
//                        }
//                    }
//            );
            startActivity(intent);
        }
    }


    public void onGenerate(View view) {
        Random rand = new Random();
        Workout workout;
        mRandomWorkout = new ArrayList<>();

        while (mRandomWorkout.size() != 2) {
            workout = mWorkouts.get(rand.nextInt(mWorkouts.size()));
            if (!mRandomWorkout.contains(workout)) {
                mRandomWorkout.add(workout);
            }
        }
        adapterWorkouts.clear();
        adapterWorkouts.addAll(mRandomWorkout);
    }

    public void onBeginChadWorkout(View view){
        if (mRandomWorkout == null) {
            Toast.makeText(MainActivity.this, "Can't begin what hasn't been made", Toast.LENGTH_SHORT).show();
            return;
        }
        mExtra = new ArrayList<String>();
        for (int i = 0; i < mRandomWorkout.size(); i ++) {
            mExtra.add(mRandomWorkout.get(i).getType());
            mExtra.add(mRandomWorkout.get(i).getName());
            mExtra.add(mRandomWorkout.get(i).getReps());
            mExtra.add(mRandomWorkout.get(i).getEquipment());
        }
        Intent intent = new Intent(this, ChadWorkout.class);
        intent.putStringArrayListExtra(ChadWorkout.WORKOUT_KEY, mExtra);
        startActivity(intent);
    }


    private void updateUI(FirebaseUser currentUser) {
        // Changes between signin/up page and home page if the user is not null
        if (currentUser != null) {
            logoutItemView = true;
            invalidateOptionsMenu();
            mLoggedOutGroup.setVisibility(View.GONE);
            mLoggedInGroup.setVisibility(View.VISIBLE);
            mNameLabel.setText(String.format(getResources().getString(R.string.hello), currentUser.getEmail()));
            COLLECTION = currentUser.getEmail() + "'s Favorites";
            getFavorites();
            getWorkouts();
        } else {
            logoutItemView = false;
            invalidateOptionsMenu();
            mLoggedInGroup.setVisibility(View.GONE);
            mLoggedOutGroup.setVisibility(View.VISIBLE);
        }
    }

    private boolean validateForm() {
        // Method for validating edit text fields on signin/up page
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    public void signIn(View view) {
        // Method for signing in a user
        if (!validateForm()) {
            return;
        }

        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Sign in Successful " ,
                                    Toast.LENGTH_SHORT).show();

                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Exception e = task.getException();
                            Log.w(TAG, "signInWithEmail:failure", e);
                            Toast.makeText(MainActivity.this, "Login failed: " + e.getLocalizedMessage(),
                                    Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }
                    }
                });
    }

    public void createAccount(View view) {
        // Method for creating user account and updating ui to homebase when successful
        if (!validateForm()) {
            return;
        }

        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Account created successfully.",
                                    Toast.LENGTH_SHORT).show();

                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Creates the options menu on start
        // Should only be called once
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        // The clean way of changing menu item visibility
        // just call the invalidateOptionsMenu() method witch calls this
        MenuItem logoutItem = menu.findItem(R.id.sign_out);
        MenuItem historyItem = menu.findItem(R.id.history);
        MenuItem favoritesItem = menu.findItem(R.id.favorites);
        if (logoutItemView) {
            logoutItem.setVisible(true);
            historyItem.setVisible(true);
            favoritesItem.setVisible(true);
            return true;
        } else {
            logoutItem.setVisible(false);
            historyItem.setVisible(false);
            favoritesItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Method for performing action from buttons in menus
        // Add another case with the item id then just add the code for
        // what you want it to do
        switch (item.getItemId()) {
            case R.id.sign_out:
                mAuth.signOut();
                FirebaseUser user = mAuth.getCurrentUser();
                updateUI(user);
                Toast.makeText(MainActivity.this, "Log Out Successful!",
                        Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);

            case R.id.history:
                Intent intent = new Intent(this, History.class);
                startActivity(intent);
                return true;

            case R.id.favorites:
                Intent intent2  = new Intent(this,Favorites.class);
                startActivity(intent2);
                return true;
        }

    }
}
