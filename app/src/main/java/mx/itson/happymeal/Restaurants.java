package mx.itson.happymeal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import mx.itson.happymeal.Model.Users;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Restaurants extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private FusedLocationProviderClient locationClient;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;
    private TextView id1, id2, id3, id4, id5, id6;
    private LinearLayout res1, res2, res3, res4, res5, res6;

    private Location lastKnownLocation;
    private LatLng currentLocation;
    private LocationCallback locationCallback;

    private MaterialSearchBar materialSearchBar;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference usuariosDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);

        materialSearchBar = findViewById(R.id.searchBar);
        toolbar = findViewById(R.id.toolbarRes);

        id1 = (TextView) findViewById(R.id.id1);
        id2 = (TextView) findViewById(R.id.id2);
        id3 = (TextView) findViewById(R.id.id3);
        id4 = (TextView) findViewById(R.id.id4);
        id5 = (TextView) findViewById(R.id.id5);
        id6 = (TextView) findViewById(R.id.id6);

        res1 = (LinearLayout) findViewById(R.id.restaurant1);
        res2 = (LinearLayout) findViewById(R.id.restaurant2);
        res3 = (LinearLayout) findViewById(R.id.restaurant3);
        res4 = (LinearLayout) findViewById(R.id.restaurant4);
        res5 = (LinearLayout) findViewById(R.id.restaurant5);
        res6 = (LinearLayout) findViewById(R.id.restaurant6);

        res1.setOnClickListener(this);
        res2.setOnClickListener(this);
        res3.setOnClickListener(this);
        res4.setOnClickListener(this);
        res5.setOnClickListener(this);
        res6.setOnClickListener(this);

        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usuariosDB = database.getReference("users");

        locationClient = LocationServices.getFusedLocationProviderClient(Restaurants.this);
        Places.initialize(Restaurants.this, getResources().getString(R.string.key));
        placesClient = Places.createClient(this);
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString(), true, null, true);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION) {
                    // Opening or closing a navigation drawer
                } else if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    materialSearchBar.disableSearch();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            materialSearchBar.clearSuggestions();
                        }
                    }, 1000);

                    materialSearchBar.clearSuggestions();
                }
            }
        });

        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                        .setCountry("mx")
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)
                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build();
                placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onComplete(Task<FindAutocompletePredictionsResponse> task) {
                        if (task.isSuccessful()) {
                            FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                            if (predictionsResponse != null) {
                                predictionList = predictionsResponse.getAutocompletePredictions();
                                List<String> suggestionList = new ArrayList<>();
                                for (int i = 0; i < predictionList.size(); i++) {
                                    AutocompletePrediction prediction = predictionList.get(i);
                                    suggestionList.add(prediction.getFullText(null).toString());
                                }
                                materialSearchBar.updateLastSuggestions(suggestionList);
                                if (!materialSearchBar.isSuggestionsVisible()) {
                                    materialSearchBar.showSuggestionsList();
                                }
                            }
                        } else {
                            Log.i("myTag", "Prediction  fetching task unsuccesful");
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        materialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            public void OnItemClickListener(int position, View v) {
                if (position >= predictionList.size()) {
                    return;
                }
                AutocompletePrediction selectedPrediction = predictionList.get(position);
                String suggestion = materialSearchBar.getLastSuggestions().get(position).toString();
                materialSearchBar.setText(suggestion);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        materialSearchBar.clearSuggestions();
                    }
                }, 1000);

                materialSearchBar.clearSuggestions();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(materialSearchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                String placeId = selectedPrediction.getPlaceId();
                List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.PHONE_NUMBER, Place.Field.PHOTO_METADATAS, Place.Field.RATING);

                final FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build();
                placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        Place place = fetchPlaceResponse.getPlace();

                        Log.i("Place Success", "Place found: " + place.getId());
                        Intent intent = new Intent(Restaurants.this, RestaurantActivity.class);
                        intent.putExtra("placeId", place.getId());
                        startActivity(intent);
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            apiException.printStackTrace();
                            int statusCode = apiException.getStatusCode();
                            Log.i("Place Failure", "Place not found: " + e.getMessage());
                            Log.i("Place Failure", "status code: " + statusCode);
                        }
                    }
                });
            }

            public void OnItemDeleteListener(int position, View v) {

            }
        });

        //LatLng currentLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

        if (ContextCompat.checkSelfPermission(Restaurants.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            Dexter.withActivity(Restaurants.this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse response) {

                }
                @Override
                public void onPermissionDenied(PermissionDeniedResponse response) {
                    if (response.isPermanentlyDenied()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Restaurants.this);
                        builder.setTitle("Permission Denied")
                                .setMessage("Permission to access device location is permanently denied, you need to go to settings to allow the permission")
                                .setNegativeButton("Cancel", null)
                                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        intent.setData(Uri.fromParts("package", getPackageName(), null));
                                    }
                                }).show();
                    } else {
                        Toast.makeText(Restaurants.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                    token.continuePermissionRequest();
                }
            }).check();
        }


        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(Restaurants.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(Restaurants.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });

        task.addOnFailureListener(Restaurants.this, new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if(e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(Restaurants.this, 51);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 51) {
            if (resultCode == RESULT_OK) {
                getDeviceLocation();
            }
        }
    }

    private void getDeviceLocation() {
        locationClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(Task<Location> task) {
                        if (task.isSuccessful()) {
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                currentLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                                findRestaurants();
                            } else {
                                LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult == null) {
                                            return;
                                        }
                                        lastKnownLocation = locationResult.getLastLocation();
                                        locationClient.removeLocationUpdates(locationCallback);
                                    }
                                };
                                locationClient.requestLocationUpdates(locationRequest, locationCallback, null);

                            }
                        } else {
                            Toast.makeText(Restaurants.this, "Unable to get last location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.restaurant1) {
            Intent intent = new Intent(Restaurants.this, RestaurantActivity.class);
            intent.putExtra("placeId", id1.getText());
            startActivity(intent);
            finish();
        } else if(v.getId() == R.id.restaurant2) {
            Intent intent = new Intent(Restaurants.this, RestaurantActivity.class);
            intent.putExtra("placeId", id2.getText());
            startActivity(intent);
            finish();
        } else if(v.getId() == R.id.restaurant3) {
            Intent intent = new Intent(Restaurants.this, RestaurantActivity.class);
            intent.putExtra("placeId", id3.getText());
            startActivity(intent);
            finish();
        } else if(v.getId() == R.id.restaurant4) {
            Intent intent = new Intent(Restaurants.this, RestaurantActivity.class);
            intent.putExtra("placeId", id4.getText());
            startActivity(intent);
            finish();
        } else if(v.getId() == R.id.restaurant5) {
            Intent intent = new Intent(Restaurants.this, RestaurantActivity.class);
            intent.putExtra("placeId", id5.getText());
            startActivity(intent);
            finish();
        } else if(v.getId() == R.id.restaurant6) {
            Intent intent = new Intent(Restaurants.this, RestaurantActivity.class);
            intent.putExtra("placeId", id6.getText());
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemLogout:
                auth.signOut();
                Intent intent = new Intent(Restaurants.this, Login.class);
                startActivity(intent);
                finish();

        }
        return super.onOptionsItemSelected(item);
    }

    public void findRestaurants() {
        StringBuilder stringBuilder = new StringBuilder(getString(R.string.nearbyUrlBase));
        stringBuilder.append("&location=" + currentLocation.latitude + "," + currentLocation.longitude);
        stringBuilder.append("&radius=" + 1000);
        stringBuilder.append("&types=restaurant");
        stringBuilder.append("&key=" + getResources().getString(R.string.key));

        String url = stringBuilder.toString();

        Object dataTransfer[] = new Object[2];
        dataTransfer[0] = new Object();
        dataTransfer[1] = url;

        NearbyRestaurants nearbyRestaurants = new NearbyRestaurants(this);
        nearbyRestaurants.execute(dataTransfer);
    }
}