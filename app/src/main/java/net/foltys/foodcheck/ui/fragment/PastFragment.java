package net.foltys.foodcheck.ui.fragment;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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

import net.foltys.foodcheck.PastScansCardViewAdapter;
import net.foltys.foodcheck.R;
import net.foltys.foodcheck.RxHelper;
import net.foltys.foodcheck.data.FavProd;
import net.foltys.foodcheck.data.FavProdViewModel;
import net.foltys.foodcheck.data.PastScan;
import net.foltys.foodcheck.data.PastScanViewModel;
import net.foltys.foodcheck.ui.activity.MainActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class PastFragment extends Fragment {
    public static final String TAG = "PastFragment";
    private final static String CALORIES_METER_CHANNEL_ID = "calories_meter";
    protected View mView;
    private PastScansCardViewAdapter adapter;
    private SharedPreferences sharedPref;
    private FavProdViewModel mFavProdViewModel;
    private PastScanViewModel mPastScanViewModel;
    private RelativeLayout parent;
    private List<PastScan> pasts = new ArrayList<>();
    private List<FavProd> favs = new ArrayList<>();
    private Context context;
    NotificationManagerCompat notificationManagerProgress;
    NotificationCompat.Builder progressBuilder;

    public PastFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
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
        // creates an instance of SharedPreferences class to read settings
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        //On card click observer
        Observer<Integer> clickObserver = new Observer<Integer>() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull Integer integer) {

                final double[] percent = {100};

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                builder.setTitle(getResources().getString(R.string.food_ate_again))
                        .setMessage(pasts.get(integer).getName())
                        .setPositiveButton(getResources().getString(R.string.yes), (dialog, which) ->
                        {
                            final Dialog customDialog = new Dialog(getActivity());
                            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            customDialog.setContentView(R.layout.adjust_food_dialog);
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

        mPastScanViewModel.getAllPastScans().observe(getViewLifecycleOwner(), scans -> {
            Log.d(TAG, "changed past");
            pasts = scans;
            //sorting
            String howToSort = sharedPref.getString(getResources().getString(R.string.pref_sort_key), getResources().getString(R.string.first_key));
            pasts.sort((o1, o2) -> {
                if (o1.getYear() > o2.getYear()) {
                    if (howToSort.equals(getResources().getString(R.string.first_key)))
                        return 1;
                    else
                        return -1;
                } else if (o1.getYear() < o2.getYear()) {
                    if (howToSort.equals(getResources().getString(R.string.first_key)))
                        return -1;
                    else
                        return 1;
                } else {
                    //comparing months
                    if (o1.getMonth() > o2.getMonth()) {
                        if (howToSort.equals(getResources().getString(R.string.first_key)))
                            return 1;
                        else
                            return -1;
                    } else if (o1.getMonth() < o2.getMonth()) {
                        if (howToSort.equals(getResources().getString(R.string.first_key)))
                            return -1;
                        else
                            return 1;
                    } else {
                        //comparing days
                        if (o1.getDay() > o2.getDay()) {
                            if (howToSort.equals(getResources().getString(R.string.first_key)))
                                return 1;
                            else
                                return -1;
                        } else if (o1.getDay() < o2.getDay()) {
                            if (howToSort.equals(getResources().getString(R.string.first_key)))
                                return -1;
                            else
                                return 1;
                        } else {
                            //comparing hours
                            if (o1.getHour() > o2.getHour()) {
                                if (howToSort.equals(getResources().getString(R.string.first_key)))
                                    return 1;
                                else
                                    return -1;
                            } else if (o1.getHour() < o2.getHour()) {
                                if (howToSort.equals(getResources().getString(R.string.first_key)))
                                    return -1;
                                else
                                    return 1;
                            } else {
                                //comparing minutes
                                if (o1.getMinutes() > o2.getMinutes()) {
                                    if (howToSort.equals(getResources().getString(R.string.first_key)))
                                        return 1;
                                    else
                                        return -1;
                                } else if (o1.getMinutes() < o2.getMinutes()) {
                                    if (howToSort.equals(getResources().getString(R.string.first_key)))
                                        return -1;
                                    else
                                        return 1;
                                } else
                                    return 0;
                            }
                        }
                    }
                }
            });
            adapter.setProducts(scans, favs);
            checkNotifications();
        });

        mFavProdViewModel = new ViewModelProvider(this).get(FavProdViewModel.class);
        mFavProdViewModel.getAllFav().observe(getViewLifecycleOwner(), favProds -> {
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
            }

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                if (viewHolder != null) {
                    final View foregroundView = ((PastScansCardViewAdapter.ViewHolder) viewHolder).viewForeground;
                    getDefaultUIUtil().onSelected(foregroundView);
                }
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                final View foregroundView = ((PastScansCardViewAdapter.ViewHolder) viewHolder).viewForeground;
                getDefaultUIUtil().clearView(foregroundView);
            }
        }).attachToRecyclerView(pastScansCardView);


        createNotificationChannelExceed();

        if (sharedPref.getBoolean("show_calories_progress", false)) {
            initProgressNotification();
        }
        checkNotifications();
    }

    private void initProgressNotification() {
        notificationManagerProgress = NotificationManagerCompat.from(context);
        progressBuilder = new NotificationCompat.Builder(context, CALORIES_METER_CHANNEL_ID);
        progressBuilder.setContentTitle(getResources().getString(R.string.calories_progress))
                .setSmallIcon(R.drawable.ic_baseline_show_chart_24)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        // Issue the initial notification with zero progress
        int PROGRESS_MAX = sharedPref.getInt("calories_limit", 2000);
        int PROGRESS_CURRENT = 0;
        progressBuilder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
        int notificationId = 2;
        notificationManagerProgress.notify(notificationId, progressBuilder.build());
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
                    sum = sum + pasts.get(i).getEnergy();
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
        progressBuilder.setContentText(String.format("%s", getResources().getString(R.string.calories_eaten) + (int) sum));
        progressBuilder.setProgress(sharedPref.getInt("calories_limit", 2000), (int) sum, false);
        notificationManagerProgress.notify(2, progressBuilder.build());
        Log.d(TAG, "refresh meter");
    }

    private void notificationAlert() {

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("notification_full", true);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
        stackBuilder.addNextIntentWithParentStack(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CALORIES_METER_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_warning_24)
                .setContentTitle(getString(R.string.drop_cookies))
                .setContentText(getString(R.string.calories_exceeded))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
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
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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