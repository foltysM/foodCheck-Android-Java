package net.foltys.foodcheck.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.EditText;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ProgressFragment extends Fragment {


    public static final String TAG = "ProgressActivity";
    private final static int STORAGE_PERMISSION_REQUEST_CODE = 12;
    protected Activity mActivity;
    private TextView noDataTextView;
    private int sortBy = 0;
    private final Calendar myCalendar = Calendar.getInstance();
    protected View mView;
    private int showWhat = 0;
    private TextView generateTextView;
    private Context context;
    private RelativeLayout parent;
    private final String myFormat = "dd/MM/yy";
    private final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
    private ArrayList<PastScan> mPastScans = new ArrayList<>();
    private EditText fromEditText;
    private EditText toEditText;

    /**
     * Constructor of Progress Fragment. Empty by default
     */
    public ProgressFragment() {
    }

    /**
     * Called when a fragment is first attached to its context.
     *
     * @param context Context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    /**
     * Method called on fragment detach. Clearing context field
     */
    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

    /**
     * Called to have the fragment instantiate its user interface view. This is optional, and non-graphical fragments can return null (which is the default implementation). This will be called between onCreate(Bundle) and onActivityCreated(Bundle).
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to. The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);
        this.mView = view;
        return view;
    }

    /**
     * Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned, but before any saved state has been restored in to the view. This gives subclasses a chance to initialize themselves once they know their view hierarchy has been completely created. The fragment's view hierarchy is not however attached to its parent at this point.
     *
     * @param view               The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parent = mView.findViewById(R.id.parent_progress);
        noDataTextView = mView.findViewById(R.id.no_data_textView);
        fromEditText = mView.findViewById(R.id.fromEditText);
        toEditText = mView.findViewById(R.id.toEditText);
        int[] edit = {0};


        DatePickerDialog.OnDateSetListener date = (view1, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(edit[0]);
        };
        fromEditText.setOnClickListener(v -> {
            new DatePickerDialog(context, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            fromEditText.setText(sdf.format(myCalendar.getTime()));
            edit[0] = 1;
        });

        toEditText.setOnClickListener(v ->
        {
            new DatePickerDialog(context, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            toEditText.setText(sdf.format(myCalendar.getTime()));
            edit[0] = 2;
        });
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
                try {
                    String fromStr = fromEditText.getText().toString();
                    if (fromStr.equals(""))
                        fromStr = "00/00/00";

                    Date from = sdf.parse(fromStr);
                    String toStr = toEditText.getText().toString();
                    Date to;
                    if (toStr.equals("")) {
                        to = new Date(1672441200000L);
                        toStr = "31/12/22";
                    } else
                        to = sdf.parse(toStr);

                    assert from != null;
                    if (from.after(to))
                        Toast.makeText(context, getResources().getString(R.string.wrong_dates), Toast.LENGTH_SHORT).show();
                    else {

                        show(fromStr, toStr);
                    }

                } catch (ParseException e) {
                    Toast.makeText(context, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });
        MaterialButton exportButton = mView.findViewById(R.id.export_button);
        exportButton.setOnClickListener(v -> {
            if (checkPermission())
                takeScreenshot();
        });


        PastScanViewModel mPastScanViewModel = new ViewModelProvider(this).get(PastScanViewModel.class);
        mPastScanViewModel.getAllPastScans().observe(getViewLifecycleOwner(), scans -> {
            mPastScans = (ArrayList<PastScan>) scans;
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

    /**
     * Method updates EditText with current date
     *
     * @param edit Indicates which label should be updated
     */
    private void updateLabel(int edit) {
        switch (edit) {
            case 1:
                fromEditText.setText(sdf.format(myCalendar.getTime()));
                break;
            case 2:
                toEditText.setText(sdf.format(myCalendar.getTime()));
                break;
            default:
                Log.d("edit[0]", " error");
                break;
        }
    }

    /**
     * Method checks if user granted permission to write storage. If not, requests it
     *
     * @return True, if permission is granted
     */
    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
            return false;
        }
    }

    /**
     * Method inflates new chart fragment
     *
     * @param from Date from which chart should be generated
     * @param to   Date to which chart should be generated
     */
    private void show(String from, String to) {
        generateTextView.setVisibility(View.GONE);
        getParentFragmentManager().beginTransaction().replace(R.id.frame_for_chart, ChartFragment.newInstance(sortBy, showWhat, mPastScans, from, to)).commit();
    }

    /**
     * Method saves a screenshot of the chart
     */
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

    /**
     * Callback for the result from requesting permissions.
     *
     * @param requestCode  The request code passed
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions which is either PackageManager.PERMISSION_GRANTED or PackageManager.PERMISSION_DENIED. Never null.
     */
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
