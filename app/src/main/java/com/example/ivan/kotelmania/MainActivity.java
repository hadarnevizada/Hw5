package com.example.ivan.kotelmania;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yayandroid.locationmanager.LocationManager;
import com.yayandroid.locationmanager.configuration.DefaultProviderConfiguration;
import com.yayandroid.locationmanager.configuration.GooglePlayServicesConfiguration;
import com.yayandroid.locationmanager.configuration.LocationConfiguration;
import com.yayandroid.locationmanager.listener.LocationListener;

import java.util.ArrayList;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    EditText heading;
    EditText content;
    ListView listView;
    ArrayList<Note> notes;
    private FusedLocationProviderClient locationClient;


    public static DBHelper getDbHelper() {
        return dbHelper;
    }

    static DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        heading = findViewById(R.id.add_heading);
        content = findViewById(R.id.add_content);
        listView = findViewById(R.id.listView);
        locationClient = LocationServices.getFusedLocationProviderClient(this);

        java.util.Timer t = new java.util.Timer();
        t.schedule(new TimerTask() {

            @Override
            public void run() {
                Looper.prepare();

                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION }, 1);


                    Toast.makeText(MainActivity.this, "no no no...", Toast.LENGTH_LONG).show();

                } else {

                    LocationConfiguration configuration = new LocationConfiguration.Builder()
                            .keepTracking(true)
                            .useGooglePlayServices(new GooglePlayServicesConfiguration.Builder()
                                    .askForGooglePlayServices(true)
                                    .build())
                            .useDefaultProviders(new DefaultProviderConfiguration.Builder()
                                    .acceptableAccuracy(10.0f)
                                    .acceptableTimePeriod(1 * 60 * 1000)
                                    .requiredTimeInterval(10 * 60 * 1000)
                                    .build())
                            .build();

                    LocationManager manager = new LocationManager
                            .Builder(getApplicationContext())
                            .configuration(configuration)
                            .notify(locationListener)
                            .build();


                    manager.get();
                    startGettingLocation();
                }
                Looper.loop();
            }
        },5000,5000);

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        final Button fundraising = (Button)findViewById(R.id.fundraisingButton);
        fundraising.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FundraisingActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        listView.setOnScrollListener(new ListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem == totalItemCount && totalItemCount > visibleItemCount) {
                    fab.setVisibility(View.INVISIBLE);
                } else {
                    fab.setVisibility(View.VISIBLE);
                }
            }
        });


        final Context originThis = this;
        FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getUid()).child("notes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notes = new ArrayList<>();
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot noteSnap : dataSnapshot.getChildren()) {
                        String key = noteSnap.getKey();
                        String content = noteSnap.child("content").getValue().toString();
                        String heading = noteSnap.child("heading").getValue().toString();
                        String status = noteSnap.child("status").getValue().toString();
                        String date = noteSnap.child("date").getValue().toString();
                        int id = Integer.parseInt(noteSnap.child("id").getValue().toString());

                        notes.add(new Note(id,key,heading,content,status,date));
                    }
                    listView.setAdapter(new MyCustomAdapter(notes,originThis));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @SuppressLint("MissingPermission")
    private void startGettingLocation() {
        locationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            double lat = location.getLatitude();
                            double lng = location.getLongitude();
                            Toast.makeText(MainActivity.this, "lat: " + lat + ", lng: " + lng, Toast.LENGTH_LONG).show();
                            if (Math.abs(lat - 33.237465) < 0.00005 && Math.abs(lng - 35.606734) < 0.00005) {
                                Snackbar.make(MainActivity.this.content, "note has been removed!", Snackbar.LENGTH_LONG)
                                        .setAction("Action ", null).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "no location found!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String headingText = data.getStringExtra("heading");
                String contentText = data.getStringExtra("content");
                String activity = data.getStringExtra("activity");
                String dbKey = data.getStringExtra("dbKey");
                String date = data.getStringExtra("date");
                int id = data.getIntExtra("id", -1);

                if (activity.equals("add")) {
                    Note note = new Note(0, dbKey, headingText, contentText, "sent", date);
                } else if (activity.equals("edit")) {
                    Note note = new Note(id, dbKey, headingText, contentText, "sent", date);
                    note.updateDB(dbHelper.getWritableDatabase());
                    Snackbar.make(findViewById(R.id.listView), "note has been edited!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        }
    }

    private void refreshNotes() {
        notes.clear();
        notes.addAll(Note.getAllNotes(dbHelper.getReadableDatabase()));
        ((ArrayAdapter<Note>) listView.getAdapter()).notifyDataSetChanged();
    }


    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onProcessTypeChanged(int processType) {
        }

        public void onLocationChanged(Location location) {
            Toast.makeText(MainActivity.this, "lat: " + location.getLatitude() +
                    ", lng: " + location.getLongitude(), Toast.LENGTH_LONG).show();
        }

        public void onLocationFailed(int type) {
        }

        public void onPermissionGranted(boolean alreadyHadPermission) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    @Override
    protected void onPause(){
        super.onPause();

    }
}
