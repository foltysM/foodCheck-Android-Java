package net.foltys.foodcheck;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ReportIssueActivity extends AppCompatActivity {

    final static int EMAIL_REQUEST_CODE = 987;
    Button sendButton;
    EditText reportContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_issue);

        sendButton = findViewById(R.id.sendButton);
        reportContent = findViewById(R.id.editTextReportContent);

        sendButton.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            String email = "michal@foltys.net";
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            String subject = "[FoodCheck issue report]";
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

            emailIntent.putExtra(Intent.EXTRA_TEXT, reportContent.getText().toString());


            startActivityForResult(emailIntent, EMAIL_REQUEST_CODE);

        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EMAIL_REQUEST_CODE) {
            Intent mainIntent = new Intent(ReportIssueActivity.this, MainActivity.class);
            startActivity(mainIntent);
        }

    }
}