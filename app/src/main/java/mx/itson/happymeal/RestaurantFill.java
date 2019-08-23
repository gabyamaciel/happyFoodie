package mx.itson.happymeal;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Class RestaurantFill
 *
 * Shows the data of the nearby restaurants on main activity (Restaurants).
 *
 * Author: Gabriela Alvarez Maciel
 */
public class RestaurantFill extends AppCompatActivity implements View.OnClickListener {
    JSONArray restaurants;
    private String[] ids, names, rates, address;
    private Activity activity;
    private TextView resName1, resName2, resName3, resName4, resName5, resName6;
    private TextView resRate1, resRate2, resRate3, resRate4, resRate5, resRate6;
    private TextView resAddress1, resAddress2, resAddress3, resAddress4, resAddress5, resAddress6;
    private TextView id1, id2, id3, id4, id5, id6;

    public RestaurantFill(JSONArray restaurants, Activity activity) {
        this.restaurants = restaurants;
        this.activity = activity;
        ids = new String[6];
        names = new String[6];
        rates = new String[6];
        address = new String[6];

        for(int i = 0; i <= 5; i++) {
            try {
                JSONObject jsonObject = restaurants.getJSONObject(i);
                ids[i] = jsonObject.getString("reference");
                names[i] = jsonObject.getString("name");
                rates[i] = jsonObject.getString("rating");
                address[i] = jsonObject.getString("vicinity");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        id1 = (TextView) activity.findViewById(R.id.id1);
        id1.setText(ids[0]);
        id2 = (TextView) activity.findViewById(R.id.id2);
        id2.setText(ids[1]);
        id3 = (TextView) activity.findViewById(R.id.id3);
        id3.setText(ids[2]);
        id4 = (TextView) activity.findViewById(R.id.id4);
        id4.setText(ids[3]);
        id5 = (TextView) activity.findViewById(R.id.id5);
        id5.setText(ids[4]);
        id6 = (TextView) activity.findViewById(R.id.id6);
        id6.setText(ids[5]);

        resName1 = (TextView) activity.findViewById(R.id.restaurantName1);
        resName1.setText(names[0]);
        resName2 = (TextView) activity.findViewById(R.id.restaurantName2);
        resName2.setText(names[1]);
        resName3 = (TextView) activity.findViewById(R.id.restaurantName3);
        resName3.setText(names[2]);
        resName4 = (TextView) activity.findViewById(R.id.restaurantName4);
        resName4.setText(names[3]);
        resName5 = (TextView) activity.findViewById(R.id.restaurantName5);
        resName5.setText(names[4]);
        resName6 = (TextView) activity.findViewById(R.id.restaurantName6);
        resName6.setText(names[5]);

        for(int i = 0; i < rates.length; i++) {
            if (rates[i] == null) {
                rates[i] = "-";
            }
        }

        resRate1 = (TextView) activity.findViewById(R.id.restaurantRate1);
        resRate1.setText(activity.getResources().getString(R.string.rate) + " " + rates[0]);
        resRate2 = (TextView) activity.findViewById(R.id.restaurantRate2);
        resRate2.setText(activity.getResources().getString(R.string.rate) + " " + rates[1]);
        resRate3 = (TextView) activity.findViewById(R.id.restaurantRate3);
        resRate3.setText(activity.getResources().getString(R.string.rate) + " " + rates[2]);
        resRate4 = (TextView) activity.findViewById(R.id.restaurantRate4);
        resRate4.setText(activity.getResources().getString(R.string.rate) + " " + rates[3]);
        resRate5 = (TextView) activity.findViewById(R.id.restaurantRate5);
        resRate5.setText(activity.getResources().getString(R.string.rate) + " " + rates[4]);
        resRate6 = (TextView) activity.findViewById(R.id.restaurantRate6);
        resRate6.setText(activity.getResources().getString(R.string.rate) + " " + rates[5]);

        resAddress1 = (TextView) activity.findViewById(R.id.restaurantAddress1);
        resAddress1.setText(address[0]);
        resAddress2 = (TextView) activity.findViewById(R.id.restaurantAddress2);
        resAddress2.setText(address[1]);
        resAddress3 = (TextView) activity.findViewById(R.id.restaurantAddress3);
        resAddress3.setText(address[2]);
        resAddress4 = (TextView) activity.findViewById(R.id.restaurantAddress4);
        resAddress4.setText(address[3]);
        resAddress5 = (TextView) activity.findViewById(R.id.restaurantAddress5);
        resAddress5.setText(address[4]);
        resAddress6 = (TextView) activity.findViewById(R.id.restaurantAddress6);
        resAddress6.setText(address[5]);
    }

    @Override
    public void onClick(View v) {

    }
}
