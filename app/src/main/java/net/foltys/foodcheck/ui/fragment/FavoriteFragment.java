package net.foltys.foodcheck.ui.fragment;

import android.app.Dialog;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;

import net.foltys.foodcheck.FavProductsCardViewAdapter;
import net.foltys.foodcheck.R;
import net.foltys.foodcheck.data.FavProd;
import net.foltys.foodcheck.data.FavProdViewModel;
import net.foltys.foodcheck.data.PastScan;
import net.foltys.foodcheck.data.PastScanViewModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class FavoriteFragment extends Fragment {


    public static final String TAG = "FavoriteFragment";
    protected View mView;
    FavProductsCardViewAdapter adapter;
    PastScanViewModel mPastScanViewModel;
    private List<PastScan> pasts = new ArrayList<>();
    private List<FavProd> favs = new ArrayList<>();
    private FrameLayout parent;

    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPastScanViewModel = new ViewModelProvider(this).get(PastScanViewModel.class);
        Observer<Integer> favClickedObserver = new Observer<Integer>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull Integer integer) {
                final double[] percent = {100};

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
                builder.setTitle(getResources().getString(R.string.food_ate_again))
                        .setMessage(favs.get(integer).getName())
                        .setPositiveButton(getResources().getString(R.string.yes), (dialog, which) ->
                        {
                            final Dialog customDialog = new Dialog(getActivity());
                            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            customDialog.setContentView(R.layout.custom_dialog);
                            customDialog.setTitle(R.string.adjust_eaten_product_amount);

                            Slider slider = customDialog.findViewById(R.id.sliderCustomDialog);
                            Button button = customDialog.findViewById(R.id.buttonCustomDialog);
                            Button okWeightButton = customDialog.findViewById(R.id.okWeightButton);
                            Button okPercentageButton = customDialog.findViewById(R.id.okPercentageButton);
                            EditText weightEditTextDialog = customDialog.findViewById(R.id.weightEditTextCustomDialog);
                            EditText percentageEditText = customDialog.findViewById(R.id.percentageEdiTextCustomDialog);
                            percentageEditText.setText(R.string._100percent);

                            weightEditTextDialog.setText(String.format("%s", favs.get(integer).getWeight()));

                            slider.addOnChangeListener((slider1, value, fromUser) -> {

                                double d = shortenDecimal(value);
                                percentageEditText.setText(String.format("%s", d + "%"));
                                double val = ((double) value * favs.get(integer).getWeight()) / 100;

                                weightEditTextDialog.setText(String.format("%s", shortenDecimal(val) + getString(R.string.g)));

                                percent[0] = d;
                            });

                            okPercentageButton.setOnClickListener(v13 -> {
                                String a = percentageEditText.getText().toString();
                                if ((!a.equals("")) && (!a.equals("%"))) {
                                    String b = a.substring(0, a.length() - 1);
                                    if ((!b.equals("")) && (!b.equals("%"))) {
                                        double doubleValue = 0;
                                        try {
                                            doubleValue = Double.parseDouble(b.replace(',', '.'));
                                        } catch (NumberFormatException e) {
                                            //Error
                                            Log.e(TAG, e.getMessage());
                                        }

                                        if (doubleValue <= 0)
                                            doubleValue = 0;

                                        if (doubleValue >= 100)
                                            doubleValue = 100;

                                        slider.setValue((float) doubleValue);

                                        percent[0] = doubleValue;
                                    }
                                }

                            });

                            okWeightButton.setOnClickListener(v12 -> {
                                String a = weightEditTextDialog.getText().toString();
                                if ((!a.equals("")) && (!a.equals(getString(R.string.g)))) {
                                    String b = a.substring(0, a.length() - 1);
                                    Log.d("value b", b);
                                    if ((!b.equals("")) && (!b.equals(getString(R.string.g)))) {
                                        double doubleValue = 0;
                                        try {
                                            doubleValue = Double.parseDouble(b.replace(',', '.'));
                                        } catch (NumberFormatException e) {
                                            //Error
                                            Log.e(TAG, e.getMessage());
                                        }

                                        if (doubleValue <= 0)
                                            doubleValue = 0;
                                        if (doubleValue > favs.get(integer).getWeight())
                                            doubleValue = favs.get(integer).getWeight();
                                        double d = (100 * doubleValue) / favs.get(integer).getWeight();
                                        slider.setValue((float) d);
                                        percent[0] = d;
                                    }
                                }

                            });

                            button.setOnClickListener(v1 ->
                            {
                                //after closing eaten amount dialog
                                final Calendar calendar = Calendar.getInstance();
                                int year = calendar.get(Calendar.YEAR);
                                int month = calendar.get(Calendar.MONTH) + 1;
                                int day = calendar.get(Calendar.DAY_OF_MONTH);

                                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                int minutes = calendar.get(Calendar.MINUTE);
                                PastScan eatenAgain = new PastScan(favs.get(integer).getBarcode(),
                                        favs.get(integer).getName(),
                                        favs.get(integer).getWeight(),
                                        day,
                                        month,
                                        year,
                                        hour,
                                        minutes,
                                        favs.get(integer).getEnergy(),
                                        favs.get(integer).getCarbohydrates(),
                                        favs.get(integer).getProtein(),
                                        favs.get(integer).getFat(),
                                        favs.get(integer).getSaturates(),
                                        favs.get(integer).getSugars(),
                                        favs.get(integer).getFibre(),
                                        favs.get(integer).getSalt(),
                                        favs.get(integer).getUrl(),
                                        percent[0] / 100);

                                mPastScanViewModel.insertPast(eatenAgain);
                                Toast.makeText(getActivity(), R.string.scan_added, Toast.LENGTH_SHORT).show();
                                customDialog.dismiss();

                            });
                            customDialog.show();

                        })
                        .setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss())
                        .show();
            }


            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        adapter = new FavProductsCardViewAdapter(getActivity(), favClickedObserver);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        this.mView = view;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parent = mView.findViewById(R.id.parent);

        RecyclerView favProdCardView = view.findViewById(R.id.cardViewFavProd3);
        FavProdViewModel mFavProdViewModel = new ViewModelProvider(this).get(FavProdViewModel.class);
        favProdCardView.setAdapter(adapter);
        favProdCardView.setLayoutManager(new LinearLayoutManager(getContext()));

        mPastScanViewModel.getAllPastScans().observe(getActivity(), pastScans -> {
            pasts = pastScans;
            adapter.setFavProducts(favs, pastScans);

            Log.d(TAG, "Changed");
        });

        mFavProdViewModel.getAllFav().observe(getActivity(), favProds -> {
            favs = favProds;
            adapter.setFavProducts(favProds, pasts);

            Log.d(TAG, "Changed");
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
                FavProd toDelete = adapter.getFavAt(viewHolder.getAdapterPosition());
                mFavProdViewModel.deleteFav(toDelete);

                Snackbar.make(parent, R.string.favorite_removed, Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, view -> mFavProdViewModel.insertFav(toDelete)).show();

            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                final View foregroundView = ((FavProductsCardViewAdapter.ViewHolder) viewHolder).viewForeground;
                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                final View foregroundView = ((FavProductsCardViewAdapter.ViewHolder) viewHolder).viewForeground;
                getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                if (viewHolder != null) {
                    final View foregroundView = ((FavProductsCardViewAdapter.ViewHolder) viewHolder).viewForeground;
                    getDefaultUIUtil().onSelected(foregroundView);
                }
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                final View foregroundView = ((FavProductsCardViewAdapter.ViewHolder) viewHolder).viewForeground;
                getDefaultUIUtil().clearView(foregroundView);
            }
        }).attachToRecyclerView(favProdCardView);

    }

    private double shortenDecimal(double input) {
        final DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        String a = decimalFormat.format(input);
        double newDouble = 0;
        try {
            newDouble = Double.parseDouble(a.replace(',', '.'));
        } catch (NumberFormatException e) {
            //Error
            Log.e(TAG, e.getMessage());
        }
        return newDouble;
    }
}