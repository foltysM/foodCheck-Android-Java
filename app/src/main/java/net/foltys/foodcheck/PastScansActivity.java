package net.foltys.foodcheck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PastScansActivity extends AppCompatActivity {

    private FloatingActionButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_scans);

        backButton = findViewById(R.id.backFloatingButton);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO faktycznie back, a nie do main
                // TODO zamienić na taki ładny, nowiutki wygląd
                Intent intent = new Intent(PastScansActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
