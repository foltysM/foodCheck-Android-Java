package net.foltys.foodcheck.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import net.foltys.foodcheck.R;
import net.foltys.foodcheck.data.PastScan;
import net.foltys.foodcheck.data.PastScanViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Calendar;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class NewProductActivity extends AppCompatActivity {

    public static final String TAG = "NewProductActivity";
    final static int RESULT_LOAD_IMG = 88;
    final static int CAMERA_REQUEST_CODE = 512;
    final static int SEND_MAIL_REQUEST_CODE = 144;
    private final PastScan scan = new PastScan();
    private final static int STORAGE_PERMISSION_REQUEST_CODE = 12;

    private final double[] percent = {100};
    private final DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    MaterialButton saveButton, cancelButton, takePhoto;
    ImageView productImageView;
    EditText nameEditText, energyEditText, weightEditText, fatEditText, saturatesEditText, carbohydratesEditText, sugarsEditText, fibreEditText, proteinEditText, saltEditText;
    Boolean photoSet = false;
    TextView barcodeTextView, energyTextView, fatTextView, saturatesTextView, carbohydratesTextView, sugarsTextView, fibreTextView, proteinTextView, saltTextView;
    Uri URI = null;
    String attachmentFile;
    ScrollView parent;
    private PastScanViewModel mPastScanViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);
        Intent intent = getIntent();
        scan.setBarcode(intent.getStringExtra("barcode"));

        MaterialButton addPhoto = findViewById(R.id.addPhotoButton123);
        initView();

        barcodeTextView.setText(scan.getBarcode());


        mPastScanViewModel = new ViewModelProvider(this).get(PastScanViewModel.class);


        Log.d("permission", Integer.toString(ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)));
        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);

        takePhoto.setOnClickListener(v -> {
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePicture, CAMERA_REQUEST_CODE);
        });

        addPhoto.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(NewProductActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            } else {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
            }
        });

        cancelButton.setOnClickListener(v -> {
            Intent goMain = new Intent(NewProductActivity.this, MainActivity.class);
            startActivity(goMain);
        });

        saveButton.setOnClickListener(v -> {
            if (saltEditText.getText().toString().length() == 0 || nameEditText.getText().toString().length() == 0 || energyEditText.getText().toString().length() == 0 || weightEditText.getText().toString().length() == 0 ||
                    fatEditText.getText().toString().length() == 0 || saturatesEditText.getText().toString().length() == 0 || carbohydratesEditText.getText().toString().length() == 0 ||
                    sugarsEditText.getText().toString().length() == 0 || fibreEditText.getText().toString().length() == 0 || proteinEditText.getText().toString().length() == 0 || !photoSet)
                Toast.makeText(this, getString(R.string.all_fields_must_be_filled), Toast.LENGTH_SHORT).show();
            else {

                final Dialog customDialog = new Dialog(NewProductActivity.this);
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
                String weightString = weightEditText.getText().toString();
                double weight = 0;
                try {
                    weight = Double.parseDouble(weightString.replace(',', '.'));
                } catch (NumberFormatException e) {
                    //Error
                    Log.e(TAG, e.getMessage());
                }

                weightEditTextDialog.setText(weightString);

                double finalWeight = weight;
                slider.addOnChangeListener((slider1, value, fromUser) -> {

                    double d = shortenDecimal(value);
                    percentageEditText.setText(String.format("%s", d + "%"));
                    double val = ((double) value * finalWeight) / 100;

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

                double finalWeight2 = weight;


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
                            if (doubleValue > finalWeight2)
                                doubleValue = finalWeight2;
                            double d = (100 * doubleValue) / finalWeight2;
                            slider.setValue((float) d);
                            percent[0] = d;
                        }
                    }

                });


                button.setOnClickListener(v1 ->
                {
                    //after closing eaten amount dialog
                    scan.setSalt(convertEditToDouble(saltEditText));
                    scan.setName(nameEditText.getText().toString());
                    scan.setEnergy(convertEditToDouble(energyEditText));
                    scan.setWeight(convertEditToDouble(weightEditText));
                    scan.setFat(convertEditToDouble(fatEditText));
                    scan.setSaturates(convertEditToDouble(saturatesEditText));
                    scan.setCarbohydrates(convertEditToDouble(carbohydratesEditText));
                    scan.setSugars(convertEditToDouble(sugarsEditText));
                    scan.setFibre(convertEditToDouble(fibreEditText));
                    scan.setProtein(convertEditToDouble(proteinEditText));

                    scan.setPercentEaten(percent[0] / 100);

                    final Calendar calendar = Calendar.getInstance();
                    scan.setDay(calendar.get(Calendar.DAY_OF_MONTH));
                    scan.setMonth(calendar.get(Calendar.MONTH) + 1);
                    scan.setYear(calendar.get(Calendar.YEAR));
                    scan.setHour(calendar.get(Calendar.HOUR_OF_DAY));
                    scan.setMinutes(calendar.get(Calendar.MINUTE));

                    scan.setUrl(URI.toString());


                    mPastScanViewModel.insertPast(scan);

                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(NewProductActivity.this);
                    builder.setTitle(getResources().getString(R.string.scan_added))
                            .setMessage(getResources().getString(R.string.add_to_official_database))
                            .setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> sendMail())
                            .setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> {
                                Intent goToMain = new Intent(NewProductActivity.this, MainActivity.class);
                                startActivity(goToMain);
                            })
                            .show();


                });
                customDialog.show();
            }

        });

    }

    private void initView() {
        nameEditText = findViewById(R.id.nameEditText);
        energyTextView = findViewById(R.id.energyTextView);
        fatTextView = findViewById(R.id.fatTextView);
        saturatesTextView = findViewById(R.id.saturatesTextView);
        carbohydratesTextView = findViewById(R.id.carbohydratesTextView);
        sugarsTextView = findViewById(R.id.sugarsTextView);
        fibreTextView = findViewById(R.id.fibreTextView);
        proteinTextView = findViewById(R.id.proteinTextView);
        saltTextView = findViewById(R.id.saltTextView);
        energyTextView.setText(String.format("%s", getResources().getString(R.string.energy) + " (" + getResources().getString(R.string.kcal) + ")"));
        fatTextView.setText(String.format("%s", getResources().getString(R.string.fat) + " (" + getResources().getString(R.string.g) + ")"));
        saturatesTextView.setText(String.format("%s", getResources().getString(R.string.saturates) + " (" + getResources().getString(R.string.g) + ")"));
        carbohydratesTextView.setText(String.format("%s", getResources().getString(R.string.carbohydrates) + " (" + getResources().getString(R.string.g) + ")"));
        sugarsTextView.setText(String.format("%s", getResources().getString(R.string.sugars) + " (" + getResources().getString(R.string.g) + ")"));
        fibreTextView.setText(String.format("%s", getResources().getString(R.string.fibre) + " (" + getResources().getString(R.string.g) + ")"));
        proteinTextView.setText(String.format("%s", getResources().getString(R.string.protein) + " (" + getResources().getString(R.string.g) + ")"));
        saltTextView.setText(String.format("%s", getResources().getString(R.string.salt) + " (" + getResources().getString(R.string.g) + ")"));
        energyEditText = findViewById(R.id.energyEditText);
        weightEditText = findViewById(R.id.weightEditText);
        fatEditText = findViewById(R.id.fatEditText);
        saturatesEditText = findViewById(R.id.saturatesEditText);
        carbohydratesEditText = findViewById(R.id.carbohydratesEditText);
        sugarsEditText = findViewById(R.id.sugarsEditText);
        fibreEditText = findViewById(R.id.fibreEditText);
        proteinEditText = findViewById(R.id.proteinEditText);
        saltEditText = findViewById(R.id.saltEditText);
        productImageView = findViewById(R.id.productImage);
        saveButton = findViewById(R.id.save);
        cancelButton = findViewById(R.id.cancel);
        barcodeTextView = findViewById(R.id.barcode);
        takePhoto = findViewById(R.id.addPhotoCamera);
        parent = findViewById(R.id.topScrLayout);
    }

    private double convertEditToDouble(@NotNull EditText text) {
        String a = decimalFormat.format(Double.parseDouble(text.getText().toString()));
        double ans = 0;
        try {
            ans = Double.parseDouble(a.replace(',', '.'));
        } catch (NumberFormatException e) {
            //Error
            Log.e(TAG, e.getMessage());
        }

        return ans;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    try {

                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                        //URI = getImageUri(this, bitmap, scan.getBarcode());
                        //insert into ImageView
                        productImageView.setImageBitmap(bitmap);
                        photoSet = true;
                        //attachmentFile = saveToInternalStorage(bitmap, scan.getBarcode());
                        String att = createDirectoryAndSaveFile(bitmap, scan.getBarcode());
                        attachmentFile = att + "/" + scan.getBarcode() + ".jpg";


                        URI = Uri.parse("file://" + attachmentFile);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(NewProductActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                    Log.d(TAG, "Catched");
                } else {
                    Toast.makeText(this, R.string.image_not_picked, Toast.LENGTH_SHORT).show();
                }
                break;
            case RESULT_LOAD_IMG:
                if (resultCode == RESULT_OK && data != null) {
                    try {
                        final Uri imageUri = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        attachmentFile = cursor.getString(columnIndex);
                        Log.d("Attachment Path:", attachmentFile);
                        URI = Uri.parse("file://" + attachmentFile);
                        cursor.close();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        productImageView.setImageBitmap(selectedImage);
                        photoSet = true;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(NewProductActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, R.string.image_not_picked, Toast.LENGTH_SHORT).show();
                }
                break;
            case SEND_MAIL_REQUEST_CODE:
                Log.d("Send mail result code", String.valueOf(resultCode));
                Intent goMain = new Intent(NewProductActivity.this, MainActivity.class);
                startActivity(goMain);
                break;
            default:
                break;
        }
    }

    private void sendMail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        String email = "michal@foltys.net";
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        String subject = "[New product request " + scan.getBarcode() + " ]";
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        Gson gson = new Gson();
        Log.d("JSON", gson.toJson(scan));
        String body = getString(R.string.send_without_changing) + "\n" +
                gson.toJson(scan);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        if (URI != null) {
            //emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://" + CachedFileProvider.AUTHORITY + "/" + fileName));

            emailIntent.putExtra(Intent.EXTRA_STREAM, URI);
        }

        try {

            startActivityForResult(Intent.createChooser(emailIntent, getString(R.string.send_email_using)), SEND_MAIL_REQUEST_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(NewProductActivity.this, getString(R.string.no_email_clients_installed), Toast.LENGTH_SHORT).show();
        }
    }

   /* public Uri getImageUri(Context inContext, Bitmap inImage, String title) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, title, null);
        return Uri.parse(path);
    }

    private String saveToInternalStorage(Bitmap bitmapImage, String name) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/net.foltys.foodcheck/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File myPath = new File(directory, name + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 70, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }*/

    private String createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

        File direct = new File(Environment.getExternalStorageDirectory() + "/FoodCheck");

        if (!direct.exists()) {
            File photoDirectory = new File(Environment.getExternalStorageDirectory().getPath() + "/FoodCheck/");


            if (photoDirectory.mkdirs())
                Log.d(TAG, "dir created");
            else
                Log.d(TAG, "dir not created");
        }


        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/FoodCheck/", fileName + ".jpg");
            if (file.exists()) {
                if (!file.delete())
                    Log.d("Photo already exists", "Deletion not successful!");
            }
            try {
                FileOutputStream out = new FileOutputStream(file);
                imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        return direct.getAbsolutePath();
    }

    private double shortenDecimal(double input) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show();
            } else {
                if (ActivityCompat.checkSelfPermission(NewProductActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // checks if able to show permission request
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(NewProductActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // shows snackBar
                        Snackbar.make(parent, R.string.need_storage_permission, Snackbar.LENGTH_LONG)
                                .setAction(R.string.grant_permission, v -> {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    startActivity(intent);
                                }).show();
                    } else {
                        ActivityCompat.requestPermissions(NewProductActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
                    }
                } else {
                    Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
