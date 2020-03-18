package com.example.android2_ereport;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.Tag;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.jar.Attributes;


public class Main2Activity extends AppCompatActivity implements LocationListener {

    public static final int CAMERA_REQUEST_CODE = 102;
    ImageView selectedImage;

    StorageReference storageReference, Imageref;
    private DatabaseReference databaseReference;
    FirebaseStorage storage;
    String currentPhotoPath;

    private LocationManager locationManager;
    TextView Imageurl;
    TextView Latitude;
    TextView Longitude;
    DatabaseReference databaseUsers;
    ProgressDialog progressDialog;






    public static final int CAMERA_PERM_CODE = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final String myinfo = "Information";
    private static final String myName = "Name";
    private static final String myNumber = "Number";
    SharedPreferences sharedPreferences;
    private FusedLocationProviderClient client;



    String ImageUrl;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                startActivity(new Intent(this, about.class));
                return true;

            case R.id.quit:
                final AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
                builder.setMessage("Do you want to continue?");
                builder.setCancelable(true);
                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;

            case R.id.uninstall:
                Toast.makeText(this, "Charot", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        selectedImage = findViewById(R.id.displayImageView);

        sharedPreferences = getSharedPreferences(myinfo, MODE_PRIVATE);

        storageReference = FirebaseStorage.getInstance().getReference();

        Latitude = findViewById(R.id.latitude);

        Longitude = findViewById(R.id.longitude);

        Imageurl=findViewById(R.id.url);

        storage = FirebaseStorage.getInstance();

        databaseUsers = FirebaseDatabase.getInstance().getReference("Users");





        locationManager = (LocationManager) (getSystemService(Context.LOCATION_SERVICE));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);

        onLocationChanged(location);


    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                dispatchTakePictureIntent();

            } else {
                Toast.makeText(this, "Camera is Required in this app", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void cameraBtn(View view) {
        askCameraPermissions();
    }

    private void askCameraPermissions() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {

            dispatchTakePictureIntent();
        }
    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK){

                File f = new File (currentPhotoPath);
                selectedImage.setImageURI(Uri.fromFile(f));


                Log.d("tag","Absolute Url of Image is" + Uri.fromFile(f));
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);



                uploadImageToFirebase(f.getName(),contentUri);

            }
        }



    }

    private void uploadall(){

        String gName=sharedPreferences.getString(myName,"");
        String gNumber=sharedPreferences.getString(myNumber,"");
        String id = databaseUsers.push().getKey();

        String latitude = Latitude.getText().toString();
        String longitude = Longitude.getText().toString();

        Users users = new Users(id, gName, gNumber, ImageUrl, latitude, longitude);

        databaseUsers.child(id).setValue(users);



    }

    public void upload(View view){


        File f = new File (currentPhotoPath);
        selectedImage.setImageURI(Uri.fromFile(f));

        Log.d("tag","Absolute Url of Image is" + Uri.fromFile(f));
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);







    progressDialog = new ProgressDialog(this);
    progressDialog.setMax(100);
    progressDialog.setMessage("Uploading...");
    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    progressDialog.show();
     progressDialog.setCancelable(false);



        uploadImageToFirebase(f.getName(),contentUri);







    }





    private void uploadImageToFirebase(final String name, final Uri contentUri) {



        final StorageReference image = storageReference.child("images/" + name);

        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {





                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {


                    @Override
                    public void onSuccess(Uri uri) {


                        ImageUrl = uri.toString();
                        if(ImageUrl!=""){
                            uploadall();
                        }

                            progressDialog.dismiss();
                        Toast.makeText(Main2Activity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    }



                });
            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override

            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();
                Toast.makeText(Main2Activity.this, "Uploading Failed.", Toast.LENGTH_SHORT).show();
            }
        });



    }






    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        }
        String imageFileName = "JPEG_" + timeStamp + "_";
        //  File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }




    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }




    public void locationBtn(View view) {

        startActivity(new Intent(this, MapsActivity.class));




    }


    @Override
    public void onLocationChanged(Location location) {
        double longitude=location.getLongitude();
        double latitude=location.getLatitude();


        Latitude.setText(""+latitude);
        Longitude.setText(""+longitude);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}



