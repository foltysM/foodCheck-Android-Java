package net.foltys.foodcheck.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import net.foltys.foodcheck.R;
import net.foltys.foodcheck.data.PastScan;
import net.foltys.foodcheck.data.PastScanViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ProgressFragment extends Fragment {


    public static final String TAG = "ProgressActivity";
    private final static int STORAGE_PERMISSION_REQUEST_CODE = 12;
    protected Activity mActivity;
    private TextView noDataTextView;
    private int sortBy = 0;
    private List<PastScan> mPastScans = new ArrayList<>();
    protected View mView;
    private int showWhat = 0;
    private TextView generateTextView;
    private Context context;
    private RelativeLayout parent;

    public ProgressFragment() {


    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);
        this.mView = view;
        return view;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parent = mView.findViewById(R.id.parent_progress);
        noDataTextView = mView.findViewById(R.id.no_data_textView);
        if (mPastScans == null || mPastScans.size() == 0)
            noDataTextView.setVisibility(View.VISIBLE);
        else
            noDataTextView.setVisibility(View.GONE);
        generateTextView = mView.findViewById(R.id.click_generate_text_view);

        MaterialButton generateButton = mView.findViewById(R.id.generate_button);

        generateButton.setOnClickListener(v -> {

            if (mPastScans == null || mPastScans.size() == 0) {
                noDataTextView.setVisibility(View.VISIBLE);
                generateTextView.setVisibility(View.GONE);
            } else {
                noDataTextView.setVisibility(View.GONE);
                show();
            }
        });
        MaterialButton exportButton = mView.findViewById(R.id.export_button);
        exportButton.setOnClickListener(v -> {
            if (checkPermission())
                takeScreenshot();
        });


        PastScanViewModel mPastScanViewModel = new ViewModelProvider(this).get(PastScanViewModel.class);
        mPastScanViewModel.getAllPastScans().observe(getViewLifecycleOwner(), scans -> {
            mPastScans = scans;
            if (mPastScans == null || mPastScans.size() == 0) {
                noDataTextView.setVisibility(View.VISIBLE);
                generateTextView.setVisibility(View.GONE);
            } else {
                noDataTextView.setVisibility(View.GONE);
                generateTextView.setVisibility(View.VISIBLE);
            }
            Log.d(TAG, "pasts");
        });

        Spinner sortSpinner = mView.findViewById(R.id.spinnerSort);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortBy = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner showSpinner = mView.findViewById(R.id.spinnerShow);
        showSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showWhat = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        List<String> spinStringSort = new ArrayList<>();
        spinStringSort.add(getResources().getString(R.string.sort_by_days));
        spinStringSort.add(getResources().getString(R.string.sort_by_months));
        spinStringSort.add(getResources().getString(R.string.sort_by_years));

        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, spinStringSort);

        sortSpinner.setAdapter(sortAdapter);

        List<String> spinStringShow = new ArrayList<>();
        spinStringShow.add(getResources().getString(R.string.energy));
        spinStringShow.add(getResources().getString(R.string.carbohydrates));
        spinStringShow.add(getResources().getString(R.string.protein));
        spinStringShow.add(getResources().getString(R.string.fat));
        spinStringShow.add(getResources().getString(R.string.saturates_only));
        spinStringShow.add(getResources().getString(R.string.sugars_only));
        spinStringShow.add(getResources().getString(R.string.fibre));
        spinStringShow.add(getResources().getString(R.string.salt));

        ArrayAdapter<String> showAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, spinStringShow);

        showSpinner.setAdapter(showAdapter);

    }

    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
            return false;
        }
    }

    private void show() {
        generateTextView.setVisibility(View.GONE);
        getParentFragmentManager().beginTransaction().replace(R.id.frame_for_chart, new ChartFragment(sortBy, showWhat, mPastScans)).commit();
    }

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = mActivity.getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            //openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            Log.d("Screenshot", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, R.string.permission_granted, Toast.LENGTH_SHORT).show();
            } else {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // checks if able to show permission request
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // shows snackBar
                        Snackbar.make(parent, R.string.need_storage_permission, Snackbar.LENGTH_LONG)
                                .setAction(R.string.grant_permission, v -> {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
                                    startActivity(intent);
                                }).show();
                    } else {
                        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.permission_granted, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
