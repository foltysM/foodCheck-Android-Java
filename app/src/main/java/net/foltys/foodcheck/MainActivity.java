package net.foltys.foodcheck;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RC_SIGN_IN = 2;

    private Button scanButton;
    private static final String TAG = "MainActivity";
    private SQLiteDatabase database;
    private Cursor cursor;
    private RelativeLayout parent;
    private DrawerLayout drawer;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton signInButton;

    // below variables of header
    private TextView nameTextViewHeader;
    private TextView emailTextViewHeader;
    private ImageView personPhotoHeader;
    // end of variables of header

    private boolean userLogged;

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO isOn? internet check

        parent = findViewById(R.id.mainRelLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Logging init
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("391481534194-j4e5bl0pub7t1etg9kav0ibmn0es1otu.apps.googleusercontent.com")
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Sign in button init
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        // variables connected with header
        View header = navigationView.getHeaderView(0);
        nameTextViewHeader = header.findViewById(R.id.nameHeaderTextView);
        emailTextViewHeader = header.findViewById(R.id.emailHeaderTextView);
        personPhotoHeader = header.findViewById(R.id.personPhotoHeader);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        //Microsoft SDK
        AppCenter.start(getApplication(), "bb5ba2c0-ac45-4e09-937b-08d97e6c789c",
                Analytics.class, Crashes.class);

        // creating database
        DatabaseAsyncTask databaseAsyncTask = new DatabaseAsyncTask();
        databaseAsyncTask.execute();

        scanButton = findViewById(R.id.scanButton);


        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO checking permission
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // checks if able to show permission request
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {
                        // shows snackbar
                        Snackbar.make(parent, R.string.need_camera_permission, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.grant_permission, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        intent.setData(Uri.parse("package:" + getPackageName()));
                                        startActivity(intent);
                                    }
                                }).show();
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                    }
                } else {
                    Intent myIntent = new Intent(MainActivity.this, ProductScannerActivity.class);
                    startActivity(myIntent);
                }
            }
        });

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
    protected void onDestroy() {
        super.onDestroy();
        // zamykanie bazy i zamykanie cursora
        cursor.close();
        database.close();


    }


    @Override
    // what will be done after click in permission request
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MainActivity.this, ProductScannerActivity.class);
                    //intent.putExtra("database", database);
                    startActivity(intent);
                } else {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // checks if able to show permission request
                        // TODO wykrzyknik ponizej czy nie?
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {
                            // shows snackbar
                            Snackbar.make(parent, R.string.need_camera_permission, Snackbar.LENGTH_INDEFINITE)
                                    .setAction(R.string.grant_permission, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.parse("package:" + getPackageName()));
                                            startActivity(intent);
                                        }
                                    }).show();
                        } else {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                        }
                    } else {
                        Intent myIntent = new Intent(MainActivity.this, ProductScannerActivity.class);
                        startActivity(myIntent);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                // TODO przekierowanie
                break;
            case R.id.nav_history:
                Intent intentPast = new Intent(MainActivity.this, PastScansActivity.class);
                startActivity(intentPast);
                break;
            case R.id.nav_fav_products:
                // TODO przekierowanie
                Toast.makeText(this, "THIS IS TOAST MESSAGE", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_progress:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProgressFragment()).commit();
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                break;
            case R.id.nav_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                // TODO remove placeholder
                String url = "http://foltys.net/food-check/app/foodCheck1_0_0.apk";
                intent.putExtra(Intent.EXTRA_TEXT, getApplicationContext().getResources().getString(R.string.try_app) + url);
                intent.setType("text/*");
                Intent chooser = new Intent(Intent.createChooser(intent, getApplicationContext().getResources().getString(R.string.choose_app)));
                startActivity(chooser);
                break;
            case R.id.nav_help:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://foltys.net/food-check/help.php"));
                startActivity(browserIntent);
                break;
            case R.id.nav_logout:
                if (userLogged) {
                    //Toast.makeText(this, "You are already logged out", Toast.LENGTH_SHORT).show();
                    logout();
                } else
                    Toast.makeText(this, "You are already logged out", Toast.LENGTH_SHORT).show();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Creating database as a background task
    private class DatabaseAsyncTask extends AsyncTask<Void, Void, String> {
        private SQliteDatabaseHelper databaseHelper;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            databaseHelper = new SQliteDatabaseHelper(MainActivity.this);
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                database = databaseHelper.getWritableDatabase();
                //databaseHelper.insertPastScans(database, 556, "Test product", 1, 1, 2, 2, 2, 2, 2, 2, 2);
                //database.delete("past_scans", )
                cursor = database.query(true, "past_scans", null, null, null, null, null, null, null, null);

                String returnString = "";
                if (cursor.moveToFirst()) {
//                    do{
//                        for(int i = 0; i<cursor.getColumnCount();i++)
//                        {
//                            returnString = returnString + cursor.getColumnName(i)+": " + cursor.getString(i);
//
//                        }
//                        returnString += "**************";
//                        cursor.moveToNext();
//
//                    }while(!cursor.isLast());
                    for (int i = 0; i < cursor.getCount(); i++) {
                        // po każdej kolumnie
                        for (int j = 0; j < cursor.getColumnCount(); j++) {
                            cursor.getColumnName(j);
                            cursor.getDouble(j);
                            cursor.getString(j);

                        }
                        cursor.moveToNext();

                    }


                    //String name = cursor.getString(3);
                    Log.d(TAG, returnString);
                    System.out.println(returnString);
                    return returnString;


                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
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
            nameTextViewHeader.setVisibility(View.GONE);
            emailTextViewHeader.setVisibility(View.GONE);
            //personPhotoHeader.setImageResource(R.mipmap.ic_launcher_round);
            signInButton.setVisibility(View.VISIBLE);
            userLogged = false;

        } else {
            nameTextViewHeader.setVisibility(View.VISIBLE);
            nameTextViewHeader.setText(acc.getDisplayName());
            emailTextViewHeader.setVisibility(View.VISIBLE);
            emailTextViewHeader.setText(acc.getEmail());
            personPhotoHeader.setVisibility(View.VISIBLE);
            // TODO photo
            //Uri urllll =
            String photoUrl = Objects.requireNonNull(acc.getPhotoUrl()).toString();
            Glide.with(getApplicationContext()).load(photoUrl)
                    .thumbnail(0.5f)
                    .transition(new DrawableTransitionOptions()
                            .crossFade())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(personPhotoHeader);
            //Log.d(TAG, "Photo url: "+urllll);
            personPhotoHeader.setImageURI(null);
            Log.d(TAG, "Image set to null");

            //personPhotoHeader.setImageURI(urllll);
            Log.d(TAG, "Image set to url");
            signInButton.setVisibility(View.GONE);
            userLogged = true;
        }
    }

    private void logout() {
        updateUI(null);
        userLogged = false;
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(MainActivity.this, R.string.logout_successful, Toast.LENGTH_SHORT).show();
            }
        });
        signInButton.setVisibility(View.VISIBLE);
    }
}
