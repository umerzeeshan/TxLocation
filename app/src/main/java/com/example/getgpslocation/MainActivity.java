package com.example.getgpslocation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

	Button btnShowLocation;
	Button btnStartBroadcast;
	
	GPSTracker gps;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        btnShowLocation = (Button) findViewById(R.id.show_location);

		btnStartBroadcast = (Button) findViewById(R.id.start_broadcast);

        btnShowLocation.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				gps = new GPSTracker(MainActivity.this);
				
				if(gps.canGetLocation()) {
					double latitude = gps.getLatitude();
					double longitude = gps.getLongitude();
					
					Toast.makeText(
							getApplicationContext(),
							"Your Location is -\nLat: " + latitude + "\nLong: "
									+ longitude, Toast.LENGTH_LONG).show();
				} else {
					gps.showSettingsAlert();
				}
			}
		});

        btnStartBroadcast.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                StartServer();
            }
        });

		
    }

    public void StartServer() {
        Intent mIntent = new Intent(MainActivity.this, Server.class);
        MainActivity.this.startActivity(mIntent);
    }
}
