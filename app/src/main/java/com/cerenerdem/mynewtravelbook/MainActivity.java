package com.cerenerdem.mynewtravelbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.jar.Attributes;

import static com.cerenerdem.mynewtravelbook.R.menu.menu;

public class MainActivity extends AppCompatActivity {

    ListView TravelLocationList;
    static ArrayList<String> Names = new ArrayList<String>();
    static ArrayList<LatLng> LatLongLoc = new ArrayList<LatLng>();
    static ArrayAdapter arrayAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == R.id.add_TravelLocation) {

            Intent IntentMaps = new Intent(getApplicationContext(), MapsActivity.class);
            IntentMaps.putExtra("info", "new");
            startActivity(IntentMaps);

        }

        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TravelLocationList = (ListView) findViewById(R.id.lst_TravelLocationList);

        try {
            MapsActivity.database = this.openOrCreateDatabase("Places", MODE_PRIVATE, null);
            Cursor cursor = MapsActivity.database.rawQuery("SELECT * FROM places", null);

            int NameIX = cursor.getColumnIndex("name");
            int LatitudeIX = cursor.getColumnIndex("latitude");
            int LongitudeIX = cursor.getColumnIndex("longitude");


            while (cursor.moveToNext()) {


                String NameDatabase = cursor.getString(NameIX);
                String LatitudeDatabase = cursor.getString(LatitudeIX);
                String LongitudeDatabase = cursor.getString(LongitudeIX);

                Names.add(NameDatabase);
                Double l1 = Double.parseDouble(LatitudeDatabase);
                Double l2 = Double.parseDouble(LongitudeDatabase);

                LatLng LatLongLocationDatabase = new LatLng(l1, l2);
                LatLongLoc.add(LatLongLocationDatabase);


            }

            cursor.close();

        } catch (Exception e) {
            e.toString();
        }


        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, Names);
        TravelLocationList.setAdapter(arrayAdapter);


        TravelLocationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent IntentMaps = new Intent(getApplicationContext(), MapsActivity.class);

                IntentMaps.putExtra("info", "old");
                IntentMaps.putExtra("position", i);

                startActivity(IntentMaps);

            }
        });


    }
}
