package com.example.android.shopifymobile;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private TextView mTotalRevenue;
    private TextView mKeyboardsSold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTotalRevenue = (TextView) findViewById(R.id.total_revenue);
        mKeyboardsSold = (TextView) findViewById(R.id.total_keyboards_sold);

        fetchData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void fetchData() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://shopicruit.myshopify.com/admin/orders.json?" +
                "page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        double totalRevenue = 0.0;
                        int numberOfKeysboardsSold = 0;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray orders = jsonObject.getJSONArray("orders");

                            for (int i = 0; i < orders.length(); i++) {
                                JSONObject overallOrderInfo = orders.getJSONObject(i);

                                double totalPrice = overallOrderInfo.getDouble("total_price");
                                totalRevenue += totalPrice;

                                JSONArray individualOrderInfo = overallOrderInfo.getJSONArray("line_items");
                                for (int j = 0; j < individualOrderInfo.length(); j++) {
                                    JSONObject individualItemInfo = individualOrderInfo.getJSONObject(j);
                                    String nameOfItem = individualItemInfo.getString("title");
                                    if (nameOfItem.equals("Aerodynamic Cotton Keyboard")) {
                                        int quantity = individualItemInfo.getInt("quantity");
                                        numberOfKeysboardsSold += quantity;
                                    }
                                }
                            }

                            mTotalRevenue.setText("$" + String.valueOf(totalRevenue));
                            mKeyboardsSold.setText(String.valueOf(numberOfKeysboardsSold));
                        } catch (Exception e) {
                            Log.e(LOG_TAG, e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        mTotalRevenue.setText("Something went wrong...");
                    }
                });
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
