package com.smartgrocery.smartgrocery;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FoodViewActivity extends AppCompatActivity {

    public final static String FOOD_WEIGHT_EXTRA = "com.smartgrocery.smartgrocery.FOOD_WEIGHT";
    public final static String FOOD_TEMP_EXTRA = "com.smartgrocery.smartgrocery.FOOD_TEMP";
    public final static String FOOD_HUM_EXTRA = "com.smartgrocery.smartgrocery.FOOD_HUM";
    public final static String FOOD_PRICE_EXTRA = "com.smartgrocery.smartgrocery.FOOD_PRICE";
    public final static String FOOD_ID_EXTRA = "com.smartgrocery.smartgrocery.FOOD_ID";

    TextView foodWeightTextView = null;
    Button restockButton = null;
    EditText temperatureEditText = null;
    EditText humidityEditText = null;
    EditText priceEditText = null;
    ProgressBar mProgressBar = null;
    int itemId;

    Button submitTempButton = null;
    Button submitPriceButton = null;
    Button submitHumButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_view);

        Intent intent = getIntent();
        itemId = intent.getIntExtra(FOOD_ID_EXTRA, 0);
        double foodWeight = intent.getDoubleExtra(FOOD_WEIGHT_EXTRA, 0);
        double foodTemp = intent.getDoubleExtra(FOOD_TEMP_EXTRA, 0);
        double foodHum = intent.getDoubleExtra(FOOD_HUM_EXTRA, 0);
        double foodPrice = intent.getDoubleExtra(FOOD_PRICE_EXTRA, 0);
        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar_food_view);

        foodWeightTextView = (TextView)findViewById(R.id.foodWeight);
        foodWeightTextView.setText(String.format("%.2f g", foodWeight));

        temperatureEditText = (EditText)findViewById(R.id.tempEditText);
        temperatureEditText.setText(String.format("%.2f", foodTemp));

        humidityEditText = (EditText)findViewById(R.id.humidityEditText);
        humidityEditText.setText(String.format("%.2f", foodHum));

        priceEditText = (EditText)findViewById(R.id.priceEditText);
        priceEditText.setText(String.format("%.2f", foodPrice));

        restockButton = (Button)findViewById(R.id.restockedButton);
        restockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChangeValueTask("refill/" + itemId).execute();
            }
        });

        submitTempButton = (Button)findViewById(R.id.submitTempButton);
        submitHumButton = (Button)findViewById(R.id.submitHumButton);
        submitPriceButton = (Button)findViewById(R.id.submitPriceButton);

        submitTempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChangeValueTask("adjust/" + itemId + "/temperature?t=" + temperatureEditText.getText().toString()).execute();
            }
        });

        submitHumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChangeValueTask("adjust/" + itemId + "/humidity?h=" + humidityEditText.getText().toString()).execute();
            }
        });

        submitPriceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChangeValueTask("price/" + itemId + "/set?p=" + priceEditText.getText().toString()).execute();
            }
        });


    }


    private class ChangeValueTask extends AsyncTask<Void, Void, Integer> {
        String command;

        ChangeValueTask(String command) {
            this.command = command;
        }

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            URL urlObj = null;

            try {
                urlObj = new URL("http://209.2.210.73/" + command);
            }  catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Malformed url", Toast.LENGTH_LONG).show();
            }

            if (urlObj != null) { //make the connection and return response
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) urlObj.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder res = new StringBuilder();
                    String readLine;
                    while((readLine = br.readLine()) != null) {
                        res.append(readLine);
                    }
//                    if(!res.toString().equals("1")) {
//                        Toast.makeText(getApplicationContext(), "Value could not be changed", Toast.LENGTH_SHORT).show();
//                    }

                    return 1; //if all goes right it should return here

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "IO Exception", Toast.LENGTH_LONG).show();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer results) {
            mProgressBar.setVisibility(View.GONE);

        }

    }
}

