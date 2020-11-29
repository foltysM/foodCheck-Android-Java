package net.foltys.foodcheck.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;

import net.foltys.foodcheck.ProductDateValue;
import net.foltys.foodcheck.R;
import net.foltys.foodcheck.data.PastScan;

import java.util.ArrayList;
import java.util.List;


public class ChartFragment extends Fragment {

    private final int sortBy;
    private final int showWhat;
    List<PastScan> mPastScans;
    List<DataEntry> data;
    AnyChartView anyChartView;
    ProgressBar pBar;
    private String what;
    private View mView;

    public ChartFragment(int sortBy, int showWhat, List<PastScan> pastScans) {
        this.sortBy = sortBy;
        this.showWhat = showWhat;
        this.mPastScans = pastScans;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = getData();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_chart, container, false);
        return mView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        anyChartView = mView.findViewById(R.id.any_chart_view);
        pBar = mView.findViewById(R.id.chart_p_bar);
        pBar.setVisibility(View.VISIBLE);
        anyChartView.setProgressBar(pBar);
        showChart();
    }

    private void showChart() {
        Cartesian cartesian = AnyChart.column();
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


        column.fill(new String[]{"#4B9460", "#5DB075"});
        column.stroke("#4B9460");


        anyChartView.setChart(cartesian);
    }

    private List<DataEntry> getData() {
        List<ProductDateValue> mProductDateValue = new ArrayList<>();
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
                // TODO date range to choose
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


        List<DataEntry> data = new ArrayList<>();

        for (int i = 0; i < mProductDateValue.size(); i++) {
            data.add(new ValueDataEntry(mProductDateValue.get(i).getDate(), mProductDateValue.get(i).getValue()));
        }
        return data;
    }
}