package net.foltys.foodcheck.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import net.foltys.foodcheck.CaptureAct;
import net.foltys.foodcheck.R;
import net.foltys.foodcheck.ui.fragment.FavoriteFragment;
import net.foltys.foodcheck.ui.fragment.HomeFragment;
import net.foltys.foodcheck.ui.fragment.PastFragment;
import net.foltys.foodcheck.ui.fragment.ProgressFragment;
import net.foltys.foodcheck.ui.fragment.SettingsFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 1;

    private static final int RC_BARCODE_SCAN = 49374;

    //used to attach the fragments
    private static final String TAG_HOME = "home";

    private static final String TAG = "MainActivity";
    private DrawerLayout drawer;
    private static final int RC_SIGN_IN = 2;
    private static final String TAG_HISTORY = "history";
    private GoogleSignInClient mGoogleSignInClient;

    private static final String TAG_FAVORITE = "favorite";

    // below variables of header
    private TextView nameTextViewHeader;
    private TextView emailTextViewHeader;
    private ImageView personPhotoHeader;
    // end of variables of header

    private static final String TAG_PROGRESS = "progress";
    private static final String TAG_SETTINGS = "settings";
    //to identify current nav menu item
    public static int navItemIndex = 0;
    private static String CURRENT_TAG = TAG_HOME;
    ExtendedFloatingActionButton scanButton;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private String name = "";
    private String imageURL = "";


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;

        } else {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }

        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent.getBooleanExtra("notification", false))
            getSupportFragmentManager().beginTransaction().replace(R.id.frame2, new PastFragment()).commit();
        if (intent.getBooleanExtra("notification_full", false)) {
            final Dialog congratsDialog = new Dialog(this);
            congratsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            congratsDialog.setContentView(R.layout.congratulations_dialog);
            Button okButton = congratsDialog.findViewById(R.id.ok_button);
            okButton.setOnClickListener(v -> congratsDialog.cancel());
            congratsDialog.show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Logging init
        String token = "391481534194-j4e5bl0pub7t1etg9kav0ibmn0es1otu.apps.googleusercontent.com";
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(token)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        // variables connected with header
        View header = navigationView.getHeaderView(0);
        nameTextViewHeader = header.findViewById(R.id.nameHeaderTextView);
        emailTextViewHeader = header.findViewById(R.id.emailHeaderTextView);
        personPhotoHeader = header.findViewById(R.id.personPhotoHeader);



        //Microsoft SDK
        AppCenter.start(getApplication(), "bb5ba2c0-ac45-4e09-937b-08d97e6c789c",
                Analytics.class, Crashes.class);

        scanButton = findViewById(R.id.scanButton);

        scanButton.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // checks if able to show permission request
                if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {
                    // shows snackBar
                    Snackbar.make(drawer, R.string.need_camera_permission, Snackbar.LENGTH_LONG)
                            .setAction(R.string.grant_permission, v1 -> requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE)).show();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                }
            } else {
                requestScan();
            }
        });


        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);

    }

    private void loadHomeFragment() {
        selectNavMenu();

        // if user choose the current menu again
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            toggleFab();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.frame2, getHomeFragment()).commit();

        toggleFab();

        drawer.closeDrawers();

        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
            default:
                return HomeFragment.newInstance(name, imageURL);
            case 1:
                return new PastFragment();
            case 2:
                return new FavoriteFragment();
            case 3:
                return new ProgressFragment();
            case 4:
                return new SettingsFragment();
        }
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void requestScan() {
        //check if there is an Internet connection
        if (isOnline()) {
            Log.d(TAG, "online");
            // Permission granted, can scan a barcode
            IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
            integrator.setCaptureActivity(CaptureAct.class);
            integrator.setOrientationLocked(true);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrator.setPrompt(getResources().getString(R.string.scanning));
            integrator.initiateScan();
        } else {
            Log.d(TAG, "offline");
            String[] options = {getResources().getString(R.string.turn_on_wifi), getResources().getString(R.string.turn_on_mobile_data), getResources().getString(R.string.cancel)};

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getResources().getString(R.string.no_internet_connection));
            builder.setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0:
                        //Turn on WiFi
                        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                        wifi.setWifiEnabled(true);
                        break;
                    case 1:
                        //Turn on mobile data
                        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        startActivity(intent);
                        break;
                    case 2:
                    default:
                        break;
                }
            });
            builder.show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "Executed onStart method");
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    @Override
    // what will be done after click in permission request
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestScan();
            } else {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // checks if able to show permission request
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {
                        // shows snackBar
                        Snackbar.make(drawer, R.string.need_camera_permission, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.grant_permission, v -> {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    startActivity(intent);
                                }).show();
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                    }
                } else {
                    requestScan();
                }
            }
        }
    }

    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    navItemIndex = 0;
                    CURRENT_TAG = TAG_HOME;
                    break;
                case R.id.nav_history:
                    navItemIndex = 1;
                    CURRENT_TAG = TAG_HISTORY;
                    break;
                case R.id.nav_fav_products:
                    navItemIndex = 2;
                    CURRENT_TAG = TAG_FAVORITE;
                    break;
                case R.id.nav_progress:
                    navItemIndex = 3;
                    CURRENT_TAG = TAG_PROGRESS;
                    break;
                case R.id.nav_settings:
                    navItemIndex = 4;
                    CURRENT_TAG = TAG_SETTINGS;
                    break;
                case R.id.nav_share:
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    String url = "http://foltys.net/food-check/app/foodCheck.apk";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.try_app) + url);
                    shareIntent.setType("text/*");
                    Intent chooser = new Intent(Intent.createChooser(shareIntent, getApplicationContext().getResources().getString(R.string.choose_app)));
                    startActivity(chooser);
                    break;
                case R.id.nav_help:
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://foltys.net/food-check/help.php"));
                    startActivity(browserIntent);
                    break;
                case R.id.nav_report:
                    Intent reportIntent = new Intent(MainActivity.this, ReportIssueActivity.class);
                    startActivity(reportIntent);
                    break;
                case R.id.nav_logout:
                    logout();
                    break;
                case R.id.nav_login:
                    login();
                    break;
            }
            drawer.closeDrawer(GravityCompat.START);
            item.setChecked(!item.isChecked());
            item.setChecked(true);

            loadHomeFragment();

            return true;

        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        toggle.syncState();

    }

    private void login() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void toggleFab() {
        if (navItemIndex == 0) {
            //home
            scanButton.show();
            scanButton.extend();
        } else if (navItemIndex == 2 || navItemIndex == 1) {
            scanButton.show();
            scanButton.shrink();

        } else {
            scanButton.hide();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        if (requestCode == RC_BARCODE_SCAN) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            Log.d("requestCode", Integer.toString(requestCode));
            if (result != null) {
                if (result.getContents() != null) {
                    Intent afterScanIntent = new Intent(MainActivity.this, AfterScanActivity.class);
                    afterScanIntent.putExtra("barcode", result.getContents());
                    startActivity(afterScanIntent);
                }
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
            Log.d(TAG, "signInResult: Signed-In");
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount acc) {
        if (acc == null) {
            // clearing person data
            nameTextViewHeader.setText(R.string.guest);
            emailTextViewHeader.setVisibility(View.GONE);
            personPhotoHeader.setImageResource(R.mipmap.ic_launcher_round);

            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
            name = getResources().getString(R.string.guest);
            imageURL = "";


        } else {
            nameTextViewHeader.setVisibility(View.VISIBLE);
            nameTextViewHeader.setText(acc.getDisplayName());
            emailTextViewHeader.setVisibility(View.VISIBLE);
            emailTextViewHeader.setText(acc.getEmail());
            personPhotoHeader.setVisibility(View.VISIBLE);

            String photoUrl = Objects.requireNonNull(acc.getPhotoUrl()).toString();
            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(photoUrl)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(personPhotoHeader);
            personPhotoHeader.setImageURI(null);
            name = acc.getDisplayName();
            imageURL = photoUrl;

            Log.d(TAG, "Image set to url");

            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.frame2, HomeFragment.newInstance(name, imageURL)).commit();
    }

    private void logout() {
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.drawer_menu);
        updateUI(null);
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> Toast.makeText(MainActivity.this, R.string.logout_successful, Toast.LENGTH_SHORT).show());
    }

    /**
     * Returns if the device has a connection with the Internet
     *
     * @return true, if it is connected, false if not
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
