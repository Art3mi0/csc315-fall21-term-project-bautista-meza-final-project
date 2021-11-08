package com.hastellc.workoutapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "MainActivity";

    private final FirebaseFirestore mDb = FirebaseFirestore.getInstance();

    private ArrayAdapter<Workout> adapter;

    private ConstraintLayout mLoggedInGroup;
    private ConstraintLayout mLoggedOutGroup;
    private TextView mNameLabel;
    private EditText mEmailField;
    private EditText mPasswordField;
    private ListView mWorkoutList;

    private boolean logoutItemView = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLoggedInGroup = findViewById(R.id.logged_in_group);
        mLoggedOutGroup = findViewById(R.id.logged_out_group);
        mNameLabel = findViewById(R.id.hello);
        mEmailField = findViewById(R.id.email);
        mPasswordField = findViewById(R.id.password);
        mWorkoutList = findViewById(R.id.workoutListView);

        adapter = new WorkoutAdapter(this, new ArrayList<Workout>());
        mWorkoutList.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

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
            TextView workoutRep = convertView.findViewById(R.id.workoutRep);

            Workout w = workouts.get(position);
            workoutName.setText(w.getName());
            workoutRep.setText(w.getReps());

            return convertView;
        }
    }

    public void onGetWorkouts(View view) {
        mDb.collection("workouts")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<Workout> workouts = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Workout w = document.toObject(Workout.class);
                            workouts.add(w);
                            Log.d(TAG, w.getType() + " " + w.getName() + " " + w.getReps());
                        }
                        adapter.clear();
                        adapter.addAll(workouts);
                    }
                });
    }

    private void updateUI(FirebaseUser currentUser) {
        // Changes between signin/up page and home page if the user is not null
        if (currentUser != null) {
            logoutItemView = true;
            invalidateOptionsMenu();
            mLoggedOutGroup.setVisibility(View.GONE);
            mLoggedInGroup.setVisibility(View.VISIBLE);
            mNameLabel.setText(String.format(getResources().getString(R.string.hello), currentUser.getEmail()));
            mWorkoutList = findViewById(R.id.workoutListView);
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
        }
    }
}
