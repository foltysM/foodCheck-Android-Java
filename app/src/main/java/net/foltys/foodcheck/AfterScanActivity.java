package net.foltys.foodcheck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class AfterScanActivity extends AppCompatActivity {

    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_scan);

        resultTextView = findViewById(R.id.resultTextView);

        // TODO uzyskanie barcode
        String barcode = "5901588017990"; // dev only

        // TODO polaczenie z baza w necie
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://murmuring-coast-47385.herokuapp.com/api/products/" + barcode;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                resultTextView.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resultTextView.setText("Error");
            }
        });

        queue.add(stringRequest);


        // TODO inserting after scan
        /*ContentValues contentValues = new ContentValues();
        contentValues.put("barcode", 00000);
        contentValues.put("protein", 1.1);
        db.insert("past_scans", null, contentValues);*/


    }


}
