package net.foltys.foodcheck;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChartView;
import com.anychart.data.Set;

import net.foltys.foodcheck.data.PastScan;

import java.util.ArrayList;
import java.util.List;

public class ProgressFragment extends Fragment {

    public static final String TAG = "ProgressFragment";
    AnyChartView anyChartView;
    TextView noDataTextView;
    int sortBy = 0;
    int showWhat = 0;
    String what;
    private List<PastScan> mPastScans = new ArrayList<>();
    private Set set;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_progress, container, false);

    }
}