package com.hastellc.workoutapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Favorites extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private final FirebaseFirestore mDb = FirebaseFirestore.getInstance();

    ListView mFavoriteListView;
    private ArrayList<Favorite> favoritesList;
    private ArrayList<Workout> workouts;
    private static final String TAG = "Favorites";

    private ArrayAdapter<Favorite> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String email = user.getEmail();
        String favoriteCollection = email + "'s Favorites";
        mFavoriteListView = findViewById(R.id.favorite_listView);

        adapter = new Favorites.FavoriteAdapter(this, new ArrayList<Favorite>());
        mFavoriteListView.setAdapter(adapter);

        favoritesList = new ArrayList<>();
        Favorites f = new Favorites();

        mDb.collection(favoriteCollection)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Favorite f = document.toObject(Favorite.class);
                            favoritesList.add(f);
                            Log.d(TAG, f.getName());
                        }
                        adapter.clear();
                        adapter.addAll(favoritesList);
                        if (favoritesList.size() == 0) {
                            TextView label = findViewById(R.id.favorite_label);
                            label.setText("No favorites");
                        } else {
                            TextView label = findViewById(R.id.favorite_label);
                            label.setText(getResources().getString(R.string.favorites_instructions));
                        }
                    }
                });
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


}