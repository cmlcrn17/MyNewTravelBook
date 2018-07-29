package com.cerenerdem.mynewtravelbook;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    String Adres;
    public List<Address> AdresListesi;

    public static SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);


        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

        if (info.matches("new")) {

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {


                    SharedPreferences sharedPreferences = MapsActivity.this.getSharedPreferences("com.cerenerdem.mynewtravelbook", MODE_PRIVATE);
                    Boolean FirstTimeCheck = sharedPreferences.getBoolean("notFirstTime", false); //İlk mi kullanılıyor diye kontrol ediyoruz.

                    if (FirstTimeCheck == false) {

                        LatLng UserLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UserLocation, 15));
                        //System.out.println("Location: " + UserLocation);
                        sharedPreferences.edit().putBoolean("notFirstTime", true).apply();
                    }


                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };


            if (Build.VERSION.SDK_INT >= 23) {

                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                } else {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    mMap.clear();

                    Location LastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (LastLocation != null) {

                        LatLng UserLastLocation = new LatLng(LastLocation.getLatitude(), LastLocation.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UserLastLocation, 15));

                    }


                }

            } else {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                mMap.clear();

                Location LastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (LastLocation != null) {

                    LatLng UserLastLocation = new LatLng(LastLocation.getLatitude(), LastLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UserLastLocation, 15));

                }


            }

        } else {
            mMap.clear();
            int position = intent.getIntExtra("position", 0);
            LatLng location = new LatLng(MainActivity.LatLongLoc.get(position).latitude, MainActivity.LatLongLoc.get(position).longitude);
            String placeName = MainActivity.Names.get(position);

            mMap.addMarker(new MarkerOptions().title(placeName).position(location));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if (grantResults.length > 0) {

            if (requestCode == 1) {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                    Location LastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (LastLocation != null) {

                        LatLng UserLastLocation = new LatLng(LastLocation.getLatitude(), LastLocation.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UserLastLocation, 15));

                    }


                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {


        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            List<Address> AdresListesi = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (AdresListesi != null && AdresListesi.size() > 0) {

                if (AdresListesi.get(0).getThoroughfare() != null) {
                    Adres += AdresListesi.get(0).getThoroughfare();

                    if (AdresListesi.get(0).getThoroughfare() != null) {

                        Adres += AdresListesi.get(0).getSubThoroughfare();

                    }
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        if (Adres.matches("")) {

            Adres = "Adres Bilgisi Yoktur";
        }

        mMap.addMarker(new MarkerOptions().position(latLng).title(Adres));
        Toast.makeText(getApplicationContext(), "Konum Belirlendi.", Toast.LENGTH_SHORT).show();


        try {

            Double l1 = latLng.latitude;
            Double l2 = latLng.longitude;

            String coord1 = l1.toString();
            String coord2 = l2.toString();

            database = this.openOrCreateDatabase("Places", MODE_PRIVATE, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS places (name VARCHAR, latitude VARCHAR, longitude VARCHAR)");
            String RunCommand_Insert = "INSERT INTO places (name, latitude, longitude) values (?, ?, ?)";


            SQLiteStatement sqLiteStatement = database.compileStatement(RunCommand_Insert);
            sqLiteStatement.bindString(1, Adres);
            sqLiteStatement.bindString(2, coord1);
            sqLiteStatement.bindString(3, coord2);

            sqLiteStatement.execute();

            Toast.makeText(getApplicationContext(), "Konum Kaydedildi.", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {

            e.toString();

        }

    }


}
