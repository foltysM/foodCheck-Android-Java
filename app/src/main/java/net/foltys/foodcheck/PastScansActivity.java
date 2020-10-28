package net.foltys.foodcheck;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import net.foltys.foodcheck.data.FavProd;
import net.foltys.foodcheck.data.FavProdViewModel;
import net.foltys.foodcheck.data.PastScan;
import net.foltys.foodcheck.data.PastScanViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PastScansActivity extends AppCompatActivity {
    public static final String TAG = "PastScanActivity";
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RC_BARCODE_SCAN = 49374;
    PastScansCardViewAdapter adapter;
    private RelativeLayout parent;

    final static String CALORIES_METER_CHANNEL_ID = "calories_meter";
    SharedPreferences sharedPref;
    private List<PastScan> pasts = new ArrayList<>();
    private List<FavProd> favs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_scans);

        parent = findViewById(R.id.parentRelLayoutPast);

        FloatingActionButton newScanFloatingActionBtn = findViewById(R.id.newScanFloatingActionButton);

        RecyclerView pastScansCardView = findViewById(R.id.cardViewPastScans);
        PastScanViewModel mPastScanViewModel = new ViewModelProvider(this).get(PastScanViewModel.class);

        newScanFloatingActionBtn.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(PastScansActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // checks if able to show permission request
                if (!ActivityCompat.shouldShowRequestPermissionRationale(PastScansActivity.this, Manifest.permission.CAMERA)) {
                    // shows snackbar
                    Snackbar.make(parent, R.string.need_camera_permission, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.grant_permission, v1 -> {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }).show();
                } else {
                    ActivityCompat.requestPermissions(PastScansActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                }
            } else {
                // Permission granted, can scan a barcode
                IntentIntegrator integrator = new IntentIntegrator(PastScansActivity.this);
                integrator.setCaptureActivity(CaptureAct.class);
                integrator.setOrientationLocked(true);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt(getResources().getString(R.string.scanning));
                integrator.initiateScan();
            }

        });

        adapter = new PastScansCardViewAdapter(this);
        pastScansCardView.setAdapter(adapter);
        pastScansCardView.setLayoutManager(new LinearLayoutManager(this));

        mPastScanViewModel.getAllPastScans().observe(this, scans -> {
            adapter.setProducts(scans, favs);
            pasts = scans;
            Log.d(TAG, "changed past");
        });
        FavProdViewModel mFavProdViewModel = new ViewModelProvider(this).get(FavProdViewModel.class);
        mFavProdViewModel.getAllFav().observe(this, favProds -> {
            adapter.setProducts(pasts, favProds);
            favs = favProds;
            Log.d(TAG, "changed fav");
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Log.d(TAG, "Deleted:" + viewHolder.getAdapterPosition());
                mPastScanViewModel.deletePast(adapter.getPastAt(viewHolder.getAdapterPosition()));
                Toast.makeText(PastScansActivity.this, R.string.scan_deleted, Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(pastScansCardView);

        // creates an instance of SharedPreferences class to read settings
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        createNotificationChannelExceed();

        checkNotifications();

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNotifications();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkNotifications();
    }

    private void checkNotifications() {
        if (sharedPref.getBoolean("show_calories_progress", false) || sharedPref.getBoolean("calories_exceeded_alert", true)) {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            double sum = 0;
            for (int i = 0; i < pasts.size(); i++) {
                if (pasts.get(i).getDay() == day && pasts.get(i).getMonth() == month && pasts.get(i).getYear() == year)
                    sum = sum + pasts.get(i).getEnergy(); //TODO zero zanim zaciągnie z bazy
            }
            Log.d("energySum", Double.toString(sum));

            if (sharedPref.getBoolean("show_calories_progress", false)) {
                refreshNotificationMeter(sum);
            }
            if (sharedPref.getBoolean("calories_exceeded_alert", true)) {
                if (sum > sharedPref.getInt("calories_limit", 2000))
                    notificationAlert();
            }
        }


    }

    private void refreshNotificationMeter(double sum) {
        //TODO
    }

    private void notificationAlert() {

        Intent intent = new Intent(this, PastScansActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CALORIES_METER_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_warning_24)
                .setContentTitle(getString(R.string.drop_cookies))
                .setContentText(getString(R.string.calories_exceeded))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        int notificationId = 1;
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());
    }

    private void createNotificationChannelExceed() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CALORIES_METER_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent goToMain = new Intent(PastScansActivity.this, MainActivity.class);
        goToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goToMain);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(PastScansActivity.this, ProductScannerActivity.class);
                    startActivity(intent);
                } else {
                    if (ActivityCompat.checkSelfPermission(PastScansActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // checks if able to show permission request
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(PastScansActivity.this, Manifest.permission.CAMERA)) {
                            // shows snackbar
                            Snackbar.make(parent, R.string.need_camera_permission, Snackbar.LENGTH_INDEFINITE)
                                    .setAction(R.string.grant_permission, v -> {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        intent.setData(Uri.parse("package:" + getPackageName()));
                                        startActivity(intent);
                                    }).show();
                        } else {
                            ActivityCompat.requestPermissions(PastScansActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                        }
                    } else {
                        Intent myIntent = new Intent(PastScansActivity.this, ProductScannerActivity.class);
                        startActivity(myIntent);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_BARCODE_SCAN) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            Log.d("requestCode", Integer.toString(requestCode));
            if (result != null) {
                if (result.getContents() != null) {
                    Intent afterScanIntent = new Intent(PastScansActivity.this, AfterScanActivity.class);
                    afterScanIntent.putExtra("barcode", result.getContents());
                    startActivity(afterScanIntent);
                }
            }
        }
    }
}
