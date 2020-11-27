package net.foltys.foodcheck.ui.fragment;

import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;

import net.foltys.foodcheck.MainActivity;
import net.foltys.foodcheck.PastScansCardViewAdapter;
import net.foltys.foodcheck.R;
import net.foltys.foodcheck.RxHelper;
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


public class PastFragment extends Fragment {
    public static final String TAG = "PastFragment";
    final static String CALORIES_METER_CHANNEL_ID = "calories_meter";
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RC_BARCODE_SCAN = 49374;
    protected View mView;
    PastScansCardViewAdapter adapter;
    SharedPreferences sharedPref;
    FavProdViewModel mFavProdViewModel;
    PastScanViewModel mPastScanViewModel;
    private FrameLayout parent;
    private List<PastScan> pasts = new ArrayList<>();
    private List<FavProd> favs = new ArrayList<>();


    public PastFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_past, container, false);
        this.mView = view;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parent = view.findViewById(R.id.parentFrameLayoutPast);
        mPastScanViewModel = new ViewModelProvider(this).get(PastScanViewModel.class);

        //On card click observer
        Observer<Integer> clickObserver = new Observer<Integer>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull Integer integer) {

                final double[] percent = {100};

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
                builder.setTitle(getResources().getString(R.string.food_ate_again))
                        .setMessage(pasts.get(integer).getName())
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

                            weightEditTextDialog.setText(String.format("%s", pasts.get(integer).getWeight()));

                            slider.addOnChangeListener((slider1, value, fromUser) -> {

                                double d = shortenDecimal(value);
                                percentageEditText.setText(String.format("%s", d + "%"));
                                double val = ((double) value * pasts.get(integer).getWeight()) / 100;

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
                                        if (doubleValue > pasts.get(integer).getWeight())
                                            doubleValue = pasts.get(integer).getWeight();
                                        double d = (100 * doubleValue) / pasts.get(integer).getWeight();
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
                                PastScan eatenAgain = new PastScan(pasts.get(integer).getBarcode(),
                                        pasts.get(integer).getName(),
                                        pasts.get(integer).getWeight(),
                                        day,
                                        month,
                                        year,
                                        hour,
                                        minutes,
                                        pasts.get(integer).getEnergy(),
                                        pasts.get(integer).getCarbohydrates(),
                                        pasts.get(integer).getProtein(),
                                        pasts.get(integer).getFat(),
                                        pasts.get(integer).getSaturates(),
                                        pasts.get(integer).getSugars(),
                                        pasts.get(integer).getFibre(),
                                        pasts.get(integer).getSalt(),
                                        pasts.get(integer).getUrl(),
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

        //add to fav
        //remove from fav
        Observer<RxHelper> observer = new Observer<RxHelper>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull RxHelper rxHelper) {
                Log.d("onNext", Integer.toString(rxHelper.getPosition()));
                Log.d("onNext", rxHelper.getValue().toString());

                if (rxHelper.getValue()) {
                    //add to fav
                    FavProd fav = new FavProd(pasts.get(rxHelper.getPosition()).getBarcode(),
                            pasts.get(rxHelper.getPosition()).getName(),
                            pasts.get(rxHelper.getPosition()).getWeight(),
                            pasts.get(rxHelper.getPosition()).getEnergy(),
                            pasts.get(rxHelper.getPosition()).getCarbohydrates(),
                            pasts.get(rxHelper.getPosition()).getProtein(),
                            pasts.get(rxHelper.getPosition()).getFat(),
                            pasts.get(rxHelper.getPosition()).getSaturates(),
                            pasts.get(rxHelper.getPosition()).getSugars(),
                            pasts.get(rxHelper.getPosition()).getFibre(),
                            pasts.get(rxHelper.getPosition()).getSalt(),
                            pasts.get(rxHelper.getPosition()).getUrl());
                    mFavProdViewModel.insertFav(fav);
                } else {
                    //remove from fav
                    mFavProdViewModel.deleteFav(mFavProdViewModel.getOneFav(pasts.get(rxHelper.getPosition()).getBarcode()));
                }
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

        RecyclerView pastScansCardView = view.findViewById(R.id.cardViewPastScans);

        adapter = new PastScansCardViewAdapter(getActivity(), observer, clickObserver);
        pastScansCardView.setAdapter(adapter);
        pastScansCardView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mPastScanViewModel.getAllPastScans().observe(getActivity(), scans -> { //TODO getLifecycleOwner
            pasts = scans;
            adapter.setProducts(scans, favs);

            Log.d(TAG, "changed past");
        });

        mFavProdViewModel = new ViewModelProvider(this).get(FavProdViewModel.class);
        mFavProdViewModel.getAllFav().observe(getActivity(), favProds -> {
            favs = favProds;
            adapter.setProducts(pasts, favProds);

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
                PastScan toDelete = adapter.getPastAt(viewHolder.getAdapterPosition());
                mPastScanViewModel.deletePast(toDelete);
                Snackbar.make(parent, R.string.scan_deleted, Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, view -> mPastScanViewModel.insertPast(toDelete)).show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                //super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                final View foregroundView = ((PastScansCardViewAdapter.ViewHolder) viewHolder).viewForeground;
                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                final View foregroundView = ((PastScansCardViewAdapter.ViewHolder) viewHolder).viewForeground;
                getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);


//                super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                //super.onSelectedChanged(viewHolder, actionState);
                if (viewHolder != null) {
                    final View foregroundView = ((PastScansCardViewAdapter.ViewHolder) viewHolder).viewForeground;
                    getDefaultUIUtil().onSelected(foregroundView);
                }
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                //super.clearView(recyclerView, viewHolder);
                final View foregroundView = ((PastScansCardViewAdapter.ViewHolder) viewHolder).viewForeground;
                getDefaultUIUtil().clearView(foregroundView);
            }
        }).attachToRecyclerView(pastScansCardView);

        // creates an instance of SharedPreferences class to read settings
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        createNotificationChannelExceed();

        checkNotifications();
    }

    @Override
    public void onResume() {
        super.onResume();
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

        Intent intent = new Intent(getActivity(), MainActivity.class); /// TODO

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
        stackBuilder.addNextIntentWithParentStack(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), CALORIES_METER_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_warning_24)
                .setContentTitle(getString(R.string.drop_cookies))
                .setContentText(getString(R.string.calories_exceeded))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());
        int notificationId = 1;
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());
    }

    private void createNotificationChannelExceed() {
        /*// Create the NotificationChannel, but only on API 26+ because
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
        }*/
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent goToMain = new Intent(PastScansActivity.this, MainActivity.class);
//        goToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(goToMain);
//    } todo

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