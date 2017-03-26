package com.smartgrocery.smartgrocery;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements RecyclerClickListener {

    private RecyclerView mRecyclerView;
    private FoodRecyclerViewAdapter mAdapter;
    private ProgressBar mProgressBar;
    List<FoodContainer> mFoodContainerList = new ArrayList<FoodContainer>();

    MenuItem loginMenuButton = null;
    MenuItem logoutMenuButton = null;

    private AsyncTask mFetchTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView)findViewById(R.id.foodsRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);

        new FetchItemsTask().execute();

        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        mFetchTask = new FetchItemsTask().execute();
                    }
                });
            }
        };
        timer.schedule(task, 0, 3000); //execute every 3 seconds
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mFetchTask != null) {
           // mFetchTask.cancel(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login, menu);
        loginMenuButton = menu.findItem(R.id.menu_login);
        logoutMenuButton = menu.findItem(R.id.menu_logout);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_login:
                createLoginDialog();
                return true;
            case R.id.menu_logout:
                Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_SHORT).show();
                mAdapter.setManager(false);
                mAdapter.notifyDataSetChanged();
                logoutMenuButton.setVisible(false);
                loginMenuButton.setVisible(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //new FetchItemsTask().execute();
    }

    private void createLoginDialog() {
        final Dialog loginDialog = new Dialog(this);
        loginDialog.setContentView(R.layout.login_dialog);
        loginDialog.setCancelable(false);

        Button loginButton = (Button) loginDialog.findViewById(R.id.btnLogin);
        Button cancelButton = (Button) loginDialog.findViewById(R.id.btnCancel);

        final EditText usernameEditText = (EditText) loginDialog.findViewById(R.id.loginUsername);
        final EditText passwordEditText = (EditText) loginDialog.findViewById(R.id.loginPassword);
        loginDialog.show();

        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(usernameEditText.getText().toString().trim().equals("Manager") && passwordEditText.getText().toString().trim().equals("password")) {
                    mAdapter.setManager(true);
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
                    loginMenuButton.setVisible(false);
                    logoutMenuButton.setVisible(true);
                    loginDialog.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                }

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginDialog.dismiss();
            }
        });

    }

    @Override
    public void itemClicked(View view, int position) {
        //open the food view information for manager to interact with
        if(mAdapter.isManager) {
            Intent intent = new Intent(this, FoodViewActivity.class);
            intent.putExtra(FoodViewActivity.FOOD_WEIGHT_EXTRA, mFoodContainerList.get(position).foodWeight);
            intent.putExtra(FoodViewActivity.FOOD_TEMP_EXTRA, mFoodContainerList.get(position).foodTemperature);
            intent.putExtra(FoodViewActivity.FOOD_HUM_EXTRA, mFoodContainerList.get(position).foodHumidity);
            intent.putExtra(FoodViewActivity.FOOD_PRICE_EXTRA, mFoodContainerList.get(position).foodPrice);
            intent.putExtra(FoodViewActivity.FOOD_ID_EXTRA, mFoodContainerList.get(position).id);
            startActivity(intent);
        }

    }


    private class FetchItemsTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            URL urlObj = null;

            try {
                urlObj = new URL("http://209.2.210.73/info");
            }  catch (MalformedURLException e) {
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Malformed url", Toast.LENGTH_LONG).show();
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

                    try {
                        JSONArray jsonArray = new JSONArray(res.toString());
                        for(int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonFoodItem = jsonArray.getJSONObject(i);
                            if(mFoodContainerList.size() == jsonArray.length()) { //janky way to tell they contain the same items VERY BAD CODE
                                mFoodContainerList.get(i).foodPrice = jsonFoodItem.getDouble("price");
                                mFoodContainerList.get(i).foodWeight = jsonFoodItem.getDouble("weight");
                                mFoodContainerList.get(i).foodTemperature = jsonFoodItem.getDouble("temperature");
                                mFoodContainerList.get(i).foodHumidity = jsonFoodItem.getDouble("humidity");
                            } else {
                                mFoodContainerList.add(new FoodContainer(jsonFoodItem.getString("name"), jsonFoodItem.getDouble("price"), jsonFoodItem.getDouble("weight"), jsonFoodItem.getDouble("temperature"), jsonFoodItem.getDouble("humidity"), jsonFoodItem.getDouble("minWeight"), jsonFoodItem.getInt("id")));
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return 1; //if all goes right it should return here

                } catch (IOException e) {
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "IO Exception", Toast.LENGTH_LONG).show();
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

            if(results == 1) {
                if(mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                } else {
                    mAdapter = new FoodRecyclerViewAdapter(MainActivity.this, mFoodContainerList, MainActivity.this);
                    mAdapter.hasStableIds();
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

        }
    }
}
