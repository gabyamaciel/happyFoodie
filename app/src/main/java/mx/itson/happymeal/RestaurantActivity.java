package mx.itson.happymeal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import mx.itson.happymeal.Model.Users;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Class RestaurantActivity
 *
 * Shows the activity with the restaurant detailed data.
 *
 * Author: Gabriela Alvarez Maciel
 */
public class RestaurantActivity extends AppCompatActivity {

    // Defining objects
    private Toolbar toolbarRe;
    private String placeId;
    private PlacesClient placesClient;
    private TextView restaurantName, restaurantCalification, restaurantAddress, restaurantPhone, restaurantCount;
    private ImageView restaurantImage;
    private LinearLayout resCapacity;
    private int count = 0, capacity = 50;

    // Declare firebase objects
    private FirebaseDatabase database;
    private DatabaseReference usuariosDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        database = FirebaseDatabase.getInstance();
        usuariosDB = database.getReference(getString(R.string.users));

        toolbarRe = (Toolbar) findViewById(R.id.toolbarRe);

        this.setSupportActionBar(toolbarRe);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        restaurantName = (TextView) findViewById(R.id.restaurantName);
        restaurantCalification = (TextView) findViewById(R.id.restaurantCalification);
        restaurantAddress = (TextView) findViewById(R.id.restaurantAddress);
        restaurantPhone = (TextView) findViewById(R.id.restaurantPhone);
        restaurantCount = (TextView) findViewById(R.id.restaurantCount);
        restaurantImage = (ImageView) findViewById(R.id.restaurantImage);
        resCapacity = (LinearLayout) findViewById(R.id.restaurantCapacity);
        placesClient = Places.createClient(this);

        Intent intent = getIntent();
        placeId = intent.getStringExtra(getString(R.string.placeid));
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.PHONE_NUMBER, Place.Field.PHOTO_METADATAS, Place.Field.RATING);

        final FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build();
        placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                Place place = fetchPlaceResponse.getPlace();
                Log.i(getString(R.string.placeSuccess), getString(R.string.placeFoundId) + placeId);
                restaurantName.setText(place.getName());
                if (place.getRating() != null)
                    restaurantCalification.setText(place.getRating().toString());
                else
                    restaurantCalification.setText("No calificado");
                restaurantAddress.setText(place.getAddress());
                restaurantPhone.setText(place.getPhoneNumber());

                count();


                if (place.getPhotoMetadatas() != null) {
                    // Get the photo metadata.
                    PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);

                    // Get the attribution text.
                    String attributions = photoMetadata.getAttributions();

                    // Create a FetchPhotoRequest.
                    FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                            .build();
                    placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                        Bitmap bitmap = fetchPhotoResponse.getBitmap();
                        restaurantImage.setImageBitmap(bitmap);
                    }).addOnFailureListener((exception) -> {
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            int statusCode = apiException.getStatusCode();
                            // Handle error with given status code.
                            Log.e(getString(R.string.placeFail), getString(R.string.placeNotFound) + exception.getMessage());
                        }
                    });
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    apiException.printStackTrace();
                    int statusCode = apiException.getStatusCode();
                    Log.i(getString(R.string.placeFail), getString(R.string.placeNotFound) + e.getMessage());
                    Log.i(getString(R.string.placeFail), getString(R.string.statusCode) + statusCode);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(RestaurantActivity.this, Restaurants.class);
            startActivity(intent);
            finish();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void count() {
        usuariosDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot child: children) {
                    Users user = child.getValue(Users.class);
                    if (placeId.equals(user.getCurrentPlace())) {
                        count++;
                    }

                }

                restaurantCount.setText(getString(R.string.statusRestaurant) + " " + Double.toString(calculate(count)) + getString(R.string.capacity));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public double calculate(int count) {
        double per = (100 / capacity) * count;
        if (per < 35) {
            resCapacity.setBackgroundColor(getResources().getColor(R.color.verde));
        } else if (per <= 85) {
            resCapacity.setBackgroundColor(getResources().getColor(R.color.amarillo));
        } else {
            resCapacity.setBackgroundColor(getResources().getColor(R.color.rojo));
        }
        return per;
    }
}
