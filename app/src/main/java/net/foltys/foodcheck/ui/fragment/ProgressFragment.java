package net.foltys.foodcheck.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.anychart.AnyChartView;
import com.google.android.material.button.MaterialButton;

import net.foltys.foodcheck.R;
import net.foltys.foodcheck.data.PastScan;
import net.foltys.foodcheck.data.PastScanViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProgressFragment extends Fragment {


    public static final String TAG = "ProgressActivity";
    AnyChartView anyChartView;
    TextView noDataTextView;
    int sortBy = 0;
    int showWhat = 0;
    private List<PastScan> mPastScans = new ArrayList<>();
    protected View mView;
    MaterialButton generateButton;

    public ProgressFragment() {


    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        anyChartView = mView.findViewById(R.id.any_chart_view);
        noDataTextView = mView.findViewById(R.id.no_data_textView);
        if (mPastScans == null || mPastScans.size() == 0)
            noDataTextView.setVisibility(View.VISIBLE);
        else
            noDataTextView.setVisibility(View.GONE);

        generateButton = mView.findViewById(R.id.generate_button);

        generateButton.setOnClickListener(v ->
                show());


        PastScanViewModel mPastScanViewModel = new ViewModelProvider(this).get(PastScanViewModel.class);
        mPastScanViewModel.getAllPastScans().observe(getViewLifecycleOwner(), scans -> {
            mPastScans = scans;
            if (mPastScans == null || mPastScans.size() == 0)
                noDataTextView.setVisibility(View.VISIBLE);
            else
                noDataTextView.setVisibility(View.GONE);
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

    private void show() {
        TextView textView = mView.findViewById(R.id.click_generate_text_view);
        textView.setVisibility(View.GONE);
        getParentFragmentManager().beginTransaction().replace(R.id.frame_for_chart, new ChartFragment(sortBy, showWhat, mPastScans)).commit();
    }
}
