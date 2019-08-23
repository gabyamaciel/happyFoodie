package mx.itson.happymeal;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Class NearbyRestaurants
 *
 * Gets JSON object with the data of the nearby restaurants from the current location.
 *
 * Author: Gabriela Alvarez Maciel
 */
public class NearbyRestaurants extends AsyncTask<Object, String, String> {

    // Defining objects
    private String url;
    private InputStream is;
    private BufferedReader bufferedReader;
    private StringBuilder stringBuilder;
    private String data;
    private JSONArray resultsArray;
    private Activity activity;

    // Class constructor
    public NearbyRestaurants(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(Object... params) {
        // Gets the url parameter
        url = (String)params[1];

        // Returns the data obtained by calling the nearby places url
        try {

            URL myUrl = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) myUrl.openConnection();
            httpURLConnection.connect();
            is = httpURLConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(is));

            String line = "";
            stringBuilder = new StringBuilder();

            while((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            data = stringBuilder.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    @Override
    protected void onPostExecute (String s) {
        try {
            JSONObject parentObject = new JSONObject(s);
            resultsArray = parentObject.getJSONArray("results");

            RestaurantFill restaurant = new RestaurantFill(resultsArray, activity);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
