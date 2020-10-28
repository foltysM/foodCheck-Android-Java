package net.foltys.foodcheck;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;

import net.foltys.foodcheck.data.PastScan;
import net.foltys.foodcheck.data.PastScanViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Calendar;

public class NewProductActivity extends AppCompatActivity {

    public static final String TAG = "NewProductActivity";
    final static int RESULT_LOAD_IMG = 88;
    final static int CAMERA_REQUEST_CODE = 512;
    private final PastScan scan = new PastScan();
    private final PastScan org = new PastScan();
    private final double[] percent = {100};
    private final DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    MaterialButton addPhoto;
    MaterialButton saveButton, cancelButton, takePhoto;
    ImageView productImageView;
    EditText nameEditText, energyEditText, weightEditText, fatEditText, saturatesEditText, carbohydratesEditText, sugarsEditText, fibreEditText, proteinEditText, saltEditText;
    Boolean photoSet = false;
    TextView barcodeTextView;
    Uri URI = null;
    String attachmentFile;
    private PastScanViewModel mPastScanViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);
        Intent intent = getIntent();
        scan.setBarcode(intent.getStringExtra("barcode"));
        nameEditText = findViewById(R.id.nameEditText);
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
        addPhoto = findViewById(R.id.addPhotoButton123);
        barcodeTextView = findViewById(R.id.barcode);
        barcodeTextView.setText(scan.getBarcode());
        takePhoto = findViewById(R.id.addPhotoCamera);

        mPastScanViewModel = new ViewModelProvider(this).get(PastScanViewModel.class);

        takePhoto.setOnClickListener(v -> {
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePicture, CAMERA_REQUEST_CODE);
        });

        addPhoto.setOnClickListener(v -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
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
                customDialog.setContentView(R.layout.custom_dialog);
                customDialog.setTitle(R.string.adjust_eaten_product_amount);

                Slider slider = customDialog.findViewById(R.id.sliderCustomDialog);
                Button button = customDialog.findViewById(R.id.buttonCustomDialog);
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
                slider.addOnChangeListener((Slider.OnChangeListener) (slider1, value, fromUser) -> {
                    //TODO miejsca po przecinku w percentage po usunieciu descrete
//                    double abc = (double)value;
//                    @SuppressLint("DefaultLocale") String valuStr = String.format("%.2f", abc);
                    percentageEditText.setText(String.format("%s", value + "%"));
                    double val = (value * finalWeight) / 100;
                    @SuppressLint("DefaultLocale") String valStr = String.format("%.2f", val);
                    weightEditTextDialog.setText(String.format("%s", valStr + getString(R.string.g)));
                    percent[0] = (double) value;
                });

                double finalWeight1 = weight;
                percentageEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String a = s.toString();
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
                                double val = (doubleValue * finalWeight1) / 100;
                                @SuppressLint("DefaultLocale") String valStr = String.format("%.2f", val);
                                weightEditTextDialog.setText(String.format("%s", valStr + getString(R.string.g)));

                                percent[0] = doubleValue;
                            }
                        }
                    }
                });

                double finalWeight2 = weight;
                weightEditTextDialog.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String a = s.toString();
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
                    }
                });


                button.setOnClickListener(v1 ->
                {
                    //after closing eaten amount dialog

                    org.setSalt(Double.parseDouble(saltEditText.getText().toString()));
                    scan.setSalt(convertEditToDouble(saltEditText));
                    scan.setName(nameEditText.getText().toString());
                    org.setEnergy(Double.parseDouble(energyEditText.getText().toString()));
                    scan.setEnergy(convertEditToDouble(energyEditText));
                    org.setWeight(Double.parseDouble(weightEditText.getText().toString()));
                    scan.setWeight(convertEditToDouble(weightEditText));
                    org.setFat(Double.parseDouble(fatEditText.getText().toString()));
                    scan.setFat(convertEditToDouble(fatEditText));
                    org.setSaturates(Double.parseDouble(saturatesEditText.getText().toString()));
                    scan.setSaturates(convertEditToDouble(saturatesEditText));
                    org.setCarbohydrates(Double.parseDouble(carbohydratesEditText.getText().toString()));
                    scan.setCarbohydrates(convertEditToDouble(carbohydratesEditText));
                    org.setSugars(Double.parseDouble(sugarsEditText.getText().toString()));
                    scan.setSugars(convertEditToDouble(sugarsEditText));
                    org.setFibre(Double.parseDouble(fibreEditText.getText().toString()));
                    scan.setFibre(convertEditToDouble(fibreEditText));
                    org.setProtein(Double.parseDouble(proteinEditText.getText().toString()));
                    scan.setProtein(convertEditToDouble(proteinEditText));


                    final Calendar calendar = Calendar.getInstance();
                    scan.setDay(calendar.get(Calendar.DAY_OF_MONTH));
                    scan.setMonth(calendar.get(Calendar.MONTH) + 1);
                    scan.setYear(calendar.get(Calendar.YEAR));
                    scan.setHour(calendar.get(Calendar.HOUR_OF_DAY));
                    scan.setMinutes(calendar.get(Calendar.MINUTE));

                    scan.setUrl("file://" + attachmentFile);


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

            // TODO Coś ze zdjeciem ogarnac

        });

    }

    private double convertEditToDouble(@NotNull EditText text) {
        String a = decimalFormat.format(Double.parseDouble(text.getText().toString()) * percent[0]);
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

                        Bitmap bitmap = (Bitmap) data.getExtras().get("data"); // TODO bitmap save to Uri-> send
                        //URI = getImageUri(this, bitmap, scan.getBarcode());
                        //insert into ImageView
                        productImageView.setImageBitmap(bitmap);
                        photoSet = true;
                        String absolutePath = saveToInternalStorage(bitmap, scan.getBarcode());


                        URI = Uri.parse("file://" + absolutePath);

                        //TODO poprawne załączanie z użyciem ContentProvider


                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(NewProductActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                    Log.d(TAG, "catched");
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
        String body = getString(R.string.send_without_changing) + "\n" +
                "Barcode: " + scan.getBarcode() +
                "\nName: " + scan.getName() +
                "\nEnergy: " + org.getEnergy() +
                "\nWeight:" + org.getWeight() +
                "\nFat:" + org.getFat() +
                "\nSaturates: " + org.getSaturates() +
                "\nCarbohydrates: " + org.getCarbohydrates() +
                "\nSugars: " + org.getSugars() +
                "\nFibre: " + org.getFibre() +
                "\nProtein: " + org.getProtein();
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        if (URI != null) {
            emailIntent.putExtra(Intent.EXTRA_STREAM, URI);
        }

        try {
            startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email_using)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(NewProductActivity.this, getString(R.string.no_email_clients_installed), Toast.LENGTH_SHORT).show();
        }

        //TODO po wysłaniu powrót do past maybe
    }

    public Uri getImageUri(Context inContext, Bitmap inImage, String title) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, title, null);
        return Uri.parse(path);
    }

    private String saveToInternalStorage(Bitmap bitmapImage, String name) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
}