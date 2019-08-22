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

public class NearbyRestaurants extends AsyncTask<Object, String, String> {
    private String url;
    private InputStream is;
    private BufferedReader bufferedReader;
    private StringBuilder stringBuilder;
    private String data;
    private JSONArray resultsArray;
    private Activity activity;
    //private TextView resName1, resName2, resName3, resName4, resName5, resName6;

    public NearbyRestaurants(Activity activity) {
        this.activity = activity;
        /*resName1 = (activity).findViewById(R.id.restaurantName1);
        resName2 = (activity).findViewById(R.id.restaurantName2);
        resName3 = (activity).findViewById(R.id.restaurantName3);
        resName4 = (activity).findViewById(R.id.restaurantName4);
        resName5 = (activity).findViewById(R.id.restaurantName5);
        resName6 = (activity).findViewById(R.id.restaurantName6);*/
    }

    @Override
    protected String doInBackground(Object... params) {
        Log.i("NEARBY RESTS", "PROCESANDO...");
        url = (String)params[1];

        try {
            Log.i("NEARBY RESTS", "COMIENZO..." + url);
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
            Log.i("NEARBY RESTS", "POST EJECUCION...");
            JSONObject parentObject = new JSONObject(s);
            Log.i("NEARBY RESTS", "POST EJECUCION 2...");
            resultsArray = parentObject.getJSONArray("results");
            Log.i("NEARBY RESTS", "POST EJECUCION 3...");

            RestaurantFill restaurant = new RestaurantFill(resultsArray, activity);





/*
            if (resultsArray.length() >= 5) {
                resName1.setText(resultsArray.getJSONObject(0).getString("name"));
                resName2.setText(resultsArray.getJSONObject(1).getString("name"));
                resName3.setText(resultsArray.getJSONObject(2).getString("name"));
                resName4.setText(resultsArray.getJSONObject(3).getString("name"));
                resName5.setText(resultsArray.getJSONObject(4).getString("name"));
                resName6.setText(resultsArray.getJSONObject(5).getString("name"));
            }


            for(int i = 0; i <= 5; i++) {
                Log.i("NEARBY RESTS",  resultsArray.length() + "...");
                JSONObject jsonObject = resultsArray.getJSONObject(i);
                String idRestaurant = jsonObject.getString("id");
                String nameRestaurant = jsonObject.getString("name");

                Log.i("NEARBY RESTS", "EL AI DI: " + idRestaurant + " Y EL NAME: " + nameRestaurant );

            }*/

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
