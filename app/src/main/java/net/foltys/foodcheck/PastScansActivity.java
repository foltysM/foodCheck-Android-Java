package net.foltys.foodcheck;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import net.foltys.foodcheck.data.PastScan;
import net.foltys.foodcheck.data.PastScanViewModel;

import java.util.ArrayList;
import java.util.List;

public class PastScansActivity extends AppCompatActivity {
    public static final String TAG = "PastScanActivity";
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RC_BARCODE_SCAN = 49374;
    PastScansCardViewAdapter adapter;
    private RelativeLayout parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_scans);

        parent = findViewById(R.id.parentRelLayoutPast);

        FloatingActionButton newScanFloatingActionBtn = findViewById(R.id.newScanFloatingActionButton);

        RecyclerView pastScansCardView = findViewById(R.id.cardViewPastScans);
        PastScanViewModel mPastScanViewModel = new ViewModelProvider(this).get(PastScanViewModel.class);

        newScanFloatingActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(PastScansActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // checks if able to show permission request
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(PastScansActivity.this, Manifest.permission.CAMERA)) {
                        // shows snackbar
                        Snackbar.make(parent, R.string.need_camera_permission, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.grant_permission, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        intent.setData(Uri.parse("package:" + getPackageName()));
                                        startActivity(intent);
                                    }
                                }).show();
                    } else {
                        ActivityCompat.requestPermissions(PastScansActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                    }
                } else {
                    // Permission granted, can scan a barcode
                    IntentIntegrator integrator = new IntentIntegrator(PastScansActivity.this);
                    integrator.setCaptureActivity(CaptureAct.class);
                    integrator.setOrientationLocked(true); // TODO czy na pewno?
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                    integrator.setPrompt(getResources().getString(R.string.scanning));
                    integrator.initiateScan();
                }

            }
        });

        adapter = new PastScansCardViewAdapter(this);
        pastScansCardView.setAdapter(adapter);
        pastScansCardView.setLayoutManager(new LinearLayoutManager(this));

        mPastScanViewModel.getAllPastScans().observe(this, new Observer<List<PastScan>>() {
            @Override
            public void onChanged(@Nullable final List<PastScan> scans) {
                adapter.setProducts(scans);
                Log.d(TAG, "changed");
            }
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
                                    .setAction(R.string.grant_permission, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.parse("package:" + getPackageName()));
                                            startActivity(intent);
                                        }
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
