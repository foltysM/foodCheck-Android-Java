package net.foltys.foodcheck;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;

import net.foltys.foodcheck.data.PastScan;
import net.foltys.foodcheck.data.PastScanViewModel;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    public static final String TAG = "ProgressActivity";
    AnyChartView anyChartView;
    TextView noDataTextView;
    int sortBy = 0;
    int showWhat = 0;
    String what;
    private List<PastScan> mPastScans = new ArrayList<>();
    private Set set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        anyChartView = findViewById(R.id.any_chart_view);
        noDataTextView = findViewById(R.id.no_data_textview);

        set = Set.instantiate();

        PastScanViewModel mPastScanViewModel = new ViewModelProvider(this).get(PastScanViewModel.class);
        mPastScanViewModel.getAllPastScans().observe(this, scans -> {
            mPastScans = scans;
            Log.d(TAG, "pasts");
        });

        Spinner sortSpinner = findViewById(R.id.spinnerSort);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortBy = position;
                refreshData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner showSpinner = findViewById(R.id.spinnerShow);
        showSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showWhat = position;
                refreshData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        List<String> spinStringSort = new ArrayList<>();
        spinStringSort.add(getResources().getString(R.string.sort_by_days));
        spinStringSort.add(getResources().getString(R.string.sort_by_months));
        spinStringSort.add(getResources().getString(R.string.sort_by_years));

        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinStringSort);

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

        ArrayAdapter<String> showAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinStringShow);

        showSpinner.setAdapter(showAdapter);


    }

    private void refreshData() {
        List<ProductDateValue> mProductDateValue = new ArrayList<>();
        if (mPastScans.size() == 0) {
            Log.d(TAG, "no scans");
            noDataTextView.setVisibility(TextView.VISIBLE);
        } else {
            noDataTextView.setVisibility(TextView.GONE);
            for (int i = 0; i < mPastScans.size(); i++) {
                String date;
                switch (sortBy) {
                    case 0:
                        date = mPastScans.get(i).getYear() + "/" + mPastScans.get(i).getMonth() + "/" + mPastScans.get(i).getDay();
                        break;
                    case 1:
                        date = mPastScans.get(i).getYear() + "/" + mPastScans.get(i).getMonth();
                        break;
                    case 2:
                        date = Integer.toString(mPastScans.get(i).getYear());
                        break;
                    default:
                        date = "";
                        break;
                }

                double g = 0;
                switch (showWhat) {
                    case 0:
                        g = mPastScans.get(i).getEnergy();
                        what = getResources().getString(R.string.energy);
                        break;
                    case 1:
                        g = mPastScans.get(i).getCarbohydrates();
                        what = getResources().getString(R.string.carbohydrates);
                        break;
                    case 2:
                        g = mPastScans.get(i).getProtein();
                        what = getResources().getString(R.string.protein);
                        break;
                    case 3:
                        g = mPastScans.get(i).getFat();
                        what = getResources().getString(R.string.fat);
                        break;
                    case 4:
                        g = mPastScans.get(i).getSaturates();
                        what = getResources().getString(R.string.saturates);
                        break;
                    case 5:
                        g = mPastScans.get(i).getSugars();
                        what = getResources().getString(R.string.sugars);
                        break;
                    case 6:
                        g = mPastScans.get(i).getFibre();
                        what = getResources().getString(R.string.fibre);
                        break;
                    case 7:
                        g = mPastScans.get(i).getSalt();
                        what = getResources().getString(R.string.salt);
                        break;
                    default:
                        break;
                }
                boolean found = false;

                if (mProductDateValue.size() == 0)
                    mProductDateValue.add(new ProductDateValue(date, g));

                else {
                    for (int j = 0; j < mProductDateValue.size(); j++) {
                        if (date.equals(mProductDateValue.get(j).getDate())) {
                            found = true;
                            mProductDateValue.get(j).setValue(mProductDateValue.get(j).getValue() + g);
                        }

                    }
                    if (!found)
                        mProductDateValue.add(new ProductDateValue(date, g));

                }
            }
        }

        List<DataEntry> data = new ArrayList<>();
        //data.clear();

        for (int i = 0; i < mProductDateValue.size(); i++) {
            data.add(new ValueDataEntry(mProductDateValue.get(i).getDate(), mProductDateValue.get(i).getValue()));
        }

        Cartesian cartesian;
        cartesian = AnyChart.column();
        Column column = cartesian.column(data);

        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d);

        cartesian.animation(true);
        cartesian.title(what + " " + getResources().getString(R.string.consumption_by_dates)); // f.e. carbohydrates consumption by dates
        cartesian.yScale().minimum(0d);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title(getResources().getString(R.string.dates));
        cartesian.yAxis(0).title(what);

        anyChartView.setChart(cartesian);
        //anyChartView.set

        // TODO chart change somehow

        set.data(data);

//        column.data(data);
    }
}