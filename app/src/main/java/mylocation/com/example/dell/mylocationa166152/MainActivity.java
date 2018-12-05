package mylocation.com.example.dell.mylocationa166152;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    TextView tvLocation;
    FusedLocationProviderClient mFusedLocationClient;
    LocationRequest mLocationRequest;
    LocationCallback mLoactionCallback;

    LocationSettingsRequest.Builder locationSettingsBuilder;
    SettingsClient client;
    Task<LocationSettingsResponse> task;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvLocation = findViewById(R.id.tv_location);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        mLoactionCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                } else ;

                    tvLocation.append("\n"+"Latitude: " +  locationResult.getLastLocation().getLatitude() +"Longitude" + locationResult.getLastLocation().getLongitude());
            }

        };

        setLocationRequestSettings();


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
       // startLocationUpdates();
        requestLocationUpdate();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mFusedLocationClient!=null)
        {
            mFusedLocationClient.removeLocationUpdates(mLoactionCallback);
            //Toast.makeText(MainActivity.this,"Listener is Removed", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestLocationUpdate()
    {
        LocationSettingsRequest.Builder mLocationSettingsBuilder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
    client=LocationServices.getSettingsClient(MainActivity.this);

    task = client.checkLocationSettings(mLocationSettingsBuilder.build());

    task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
        @Override
        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
            startLocationUpdates();
        }
    });

    task.addOnFailureListener(MainActivity.this, new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            if (e instanceof ResolvableApiException)
            {
                try
                {

                    ResolvableApiException resolvable = (ResolvableApiException)e;
                    resolvable.startResolutionForResult(MainActivity.this,REQUEST_CHECK_SETTINGS);

                }catch (IntentSender.SendIntentException sendEx)
                {

                }
            }
        }
    });

    }


    private void setLocationRequestSettings()
    {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private void startLocationUpdates() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showExplanation();

            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        0);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLoactionCallback,null);
            Toast.makeText(MainActivity.this,"Location Permission was granted",Toast.LENGTH_SHORT).show();
        }
    }
    private void showExplanation()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Requires Loaction Permission");
        builder.setMessage("This app needs location permission to get the Loaction information");
        builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        0);
            }

        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this,"Sorry, this function cannot be worked until the permission is granted",Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode)
        {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:

                        Toast.makeText(MainActivity.this,"Location setting has turn on",Toast.LENGTH_LONG).show();
                    break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(MainActivity.this,"Location setting has not turn on",Toast.LENGTH_LONG).show();

                        break;
                }

        }
    }
}
