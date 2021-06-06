package com.example.location;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //inicijalizacija varijabli
    Button btLocation;
    TextView textView1, textView2, textView3, textView4, textView5;
    FusedLocationProviderClient fusedLocationProviderClient;

    //određuje potrebnu razinu preciznosti / potrošnje energije i željeni interval ažuriranja,
    //a uređaj automatski vrši odgovarajuće promjene u postavkama sustava
    protected void createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); //brzina u milisekundama u kojoj aplikacija prima ažuriranja lokacije
        locationRequest.setFastestInterval(5000); // najbržu brzinu u milisekundama kojom aplikacija obraduje ažuriranja lokacije
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //najpreciznije moguće mjesto, vjerojatnije je da će lokacije usluge koristiti GPS za određivanje lokacije
    }

    private LocationRequest locationRequest;
    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //dodijelivanje varijabli
        btLocation = findViewById(R.id.bt_location);
        textView1 = findViewById(R.id.text_view1); //sirina
        textView2 = findViewById(R.id.text_view2); //duzina
        textView3 = findViewById(R.id.text_view3); //drzava
        textView4 = findViewById(R.id.text_view4); //mjesto
        textView5 = findViewById(R.id.text_view5); //adresa

        //inicijalizacija fusedLocationProviderClient, glavna ulazna točka za interakciju s osiguračem lokacije
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        btLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //provjera dopustenja, vezano za gps
                //klasa ActivityCompat- helper za rad i provjeru dopuštenja
   //PackageManager-općenito klasa za dohvaćanje raznih informacija vezanih za pakete koji su trenutno instalirani
                // na uređaju-u našem slučaju se preko PackageManagera vrši provjera jesu li dana prava
                if (ActivityCompat.checkSelfPermission(MainActivity.this
                        , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //kad se odobrenje odobri
                    getLocation();
                } else {
                    //odbijeno odobrenje
                    ActivityCompat.requestPermissions(MainActivity.this
                            , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
            }
        });

    }

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //inicijalizacija lokacije
                Location location = task.getResult();
                if (location != null) { //Lokacija je isključena, uredaj nije nikada zabiljezio svoje mjesto

                    try {
                        //inicijalizacija geocodera
                        //proces transformacije adrese lokacije u koordinate (latituda i longituda)
                        Geocoder geocoder = new Geocoder(MainActivity.this,
                                Locale.getDefault());

                        //inicijalizacija adresne liste
        //Vraća niz adresa za koje je poznato da opisuju područje koje neposredno okružuje datu geografsku širinu i dužinu
        //Vraćene vrijednosti mogu se dobiti mrežnim pretraživanjem, nije zajamčeno da će rezultati biti točni
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );
                        //zemljopisna sirina na textview
                        textView1.setText(Html.fromHtml(
                                "<font color='#6200EE'><b><Sirina :</b><br></font>"
                                        + addresses.get(0).getLatitude()
                        ));
                        //zemljopisna duzina na textview
                        textView2.setText(Html.fromHtml(
                                "<font color='#6200EE'><b><Duzina :</b><br></font>"
                                        + addresses.get(0).getLongitude()
                        ));
                        //drzava
                        textView3.setText(Html.fromHtml(
                                "<font color='#6200EE'><b><Drzava :</b><br></font>"
                                        + addresses.get(0).getCountryName()
                        ));
                        //mjesto
                        textView4.setText(Html.fromHtml(
                                "<font color='#6200EE'><b><Mjesto :</b><br></font>"
                                        + addresses.get(0).getLocality()
                        ));
                        //adresa
                        textView5.setText(Html.fromHtml(
                                "<font color='#6200EE'><b><Adresa :</b><br></font>"
                                        + addresses.get(0).getAddressLine(0)
                        ));

                    } catch (IOException e) { //ako je mreža nedostupna ili se dogodi bilo koji drugi I/O problem
                        e.printStackTrace();
                    }
                }


            }
        });
    }

}
