package com.hastellc.workoutapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import pl.droidsonroids.gif.GifImageView;

public class Tutorials extends AppCompatActivity {

    public static final String EXTRASTUFF = null;


    private final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    private final FirebaseFirestore mDb = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorials);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CharSequence text = "GIF Loading. Please Be Patient.";
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(this, text, duration);
        toast.show();


        Intent intent = getIntent();
        String stringz = intent.getStringExtra(EXTRASTUFF);



        String thegif = "workoutGifs/" + stringz + ".gif";
        GifImageView mWorkoutGIF = findViewById(R.id.workout_GIF);
        StorageReference image = mStorageRef.child(thegif);
        Glide.with(Tutorials.this).asGif()
                .load(image)
                .into(mWorkoutGIF);




    }
}