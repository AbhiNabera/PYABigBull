package com.example.abhinabera.pyabigbull.Login;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.abhinabera.pyabigbull.Api.RetrofitClient;
import com.example.abhinabera.pyabigbull.Dialog.ProgressDialog;
import com.example.abhinabera.pyabigbull.Dashboard.MainActivity;
import com.example.abhinabera.pyabigbull.R;
import com.example.abhinabera.pyabigbull.Api.Utility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.R.attr.maxWidth;
import static android.R.attr.maxHeight;

public class UserNameActivity extends AppCompatActivity {

    EditText dependentName;
    EditText userName;
    Button register;
    ImageView profilePhoto;

    RadioGroup radioGroup;
    RadioButton member, spouse, child;

    private String phoneNumber;

    ProgressDialog progressDialog;

    private String prevUsername = "";

    String[] perms = {  "android.permission.WRITE_EXTERNAL_STORAGE" , "android.permission.READ_EXTERNAL_STORAGE","android.permission.CAMERA"};
    private SharedPreferences sharedPreferences;
    public boolean read_permission = true , write_permission = true , cam_permision = true;

    private String type = "member";

    JsonObject playerData ;
    JsonObject data;

    String mCurrentPhotoPath = null;
    Uri currentPhotoUri = null;
    private String CAM_FILE_PATH=null, playerNameStatus = "1";
    Dialog dialog;

    private static final int GALLERY_INTENT_CALLED = 4;
    private static final int GALLERY_KITKAT_INTENT_CALLED = 5, CHOICE_CAMERA = 7;
    File gallery_file;
    Drawable img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_username);
        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requesreadtpermission();
                }
            }
        },500);

        playerData = new JsonObject();
        data = new JsonObject();

        userName = (EditText) findViewById(R.id.userName);
        register = (Button) findViewById(R.id.register);
        dependentName = (EditText) findViewById(R.id.dependentName);
        profilePhoto = (ImageView) findViewById(R.id.profilePhoto);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        member = (RadioButton) findViewById(R.id.member);
        spouse = (RadioButton) findViewById(R.id.spouse);
        child = (RadioButton) findViewById(R.id.child);

        phoneNumber = getIntent().getStringExtra("phoneNumber");

        sharedPreferences = getSharedPreferences(Utility.MyPREF, MODE_PRIVATE);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                //Log.d("position", ""+i);
                if(radioGroup.getCheckedRadioButtonId() == R.id.member) {
                    type = "member";
                    playerData.addProperty("type", "member");
                    dependentName.setVisibility(View.GONE);

                } else if(radioGroup.getCheckedRadioButtonId() == R.id.spouse) {
                    type = "spouse";
                    playerData.addProperty("type", "spouse");
                    dependentName.setVisibility(View.VISIBLE);
                    dependentName.setHint("Enter husband's name");
                    dependentName.setText("");

                } else if(radioGroup.getCheckedRadioButtonId() == R.id.child) {
                    type = "child";
                    playerData.addProperty("type", "child");
                    dependentName.setVisibility(View.VISIBLE);
                    dependentName.setHint("Enter father's name");
                    dependentName.setText("");

                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Utility.isOnline(UserNameActivity.this)) {
                    if (check()) {

                        playerData.addProperty("type", type);
                        playerData.addProperty("phoneNumber", phoneNumber);
                        playerData.addProperty("userName", userName.getText().toString().toUpperCase().trim());

                        data.addProperty("type", type);
                        data.addProperty("userName", userName.getText().toString().toUpperCase().trim());

                        if(!type.equalsIgnoreCase("member")) {
                            data.addProperty("dependentName", dependentName.getText().toString().trim());
                        } else {
                            data.remove("dependentName");
                        }
                        playerData.add("userData", data);
                        Log.d("player data", playerData+"");
                        addPlayer(playerData);
                    }
                } else {

                    new Utility().showDialog("NO INTERNET",
                            "Please check your internet connection.", UserNameActivity.this);
                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getUserName();
            }
        },100);

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    public void showDialog(){

        dialog = new Dialog(UserNameActivity.this);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.photo_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        ImageView camera = (ImageView) dialog.findViewById(R.id.camera_option);
        ImageView gallery = (ImageView) dialog.findViewById(R.id.gallery_option);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkCameraHardware(UserNameActivity.this)) {
                    if (cam_permision) {

                        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        if(intent.resolveActivity(getPackageManager()) != null) {
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {
                                // Error occurred while creating the File
                            }
                            if(photoFile != null) {
                                currentPhotoUri = FileProvider.getUriForFile(getApplicationContext(),
                                "com.example.android.fileprovider", photoFile);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri);
                                startActivityForResult(intent, CHOICE_CAMERA);
                            }
                        }
                    }else{
                        requesreadtpermission();
                    }
                }
                dialog.dismiss();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/jpeg");
                startActivityForResult(intent, GALLERY_KITKAT_INTENT_CALLED);
                dialog.dismiss();
            }
        });
    }

    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }

        if(requestCode == CHOICE_CAMERA) {

            if(mCurrentPhotoPath != null) {
                CAM_FILE_PATH = mCurrentPhotoPath;
                crop_from_uri(new File(mCurrentPhotoPath));
                profilePhoto.setImageURI(currentPhotoUri);
            }
        }

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Log.d("result:"," success");
            final Uri resultUri = UCrop.getOutput(data);
            if(gallery_file!=null){
                CAM_FILE_PATH = gallery_file.getPath();
            }
            Bitmap temp = BitmapFactory.decodeFile(resultUri.getPath());
            profilePhoto.setImageBitmap(temp);
            //finish();
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Log.d("result:"," failed");
            final Throwable cropError = UCrop.getError(data);
        }
        else   if (requestCode == GALLERY_INTENT_CALLED) {

            Log.d("gallery","");
            Uri originalUri = data.getData();

            int columnIndex = 0;
            String[] projection = { MediaStore.Images.Media.DATA };

            Cursor cursor = null;
            if (originalUri != null) {
                cursor = getContentResolver().query(originalUri, projection, null, null, null);
            }
            if (cursor != null) {
                cursor.moveToFirst();
                columnIndex = cursor.getColumnIndex(projection[0]);
                String picturePath = cursor.getString(columnIndex); // returns null
                cursor.close();
                final File gallery_file = new File(picturePath);
                crop_from_uri(gallery_file);
            }
        }

        else if (requestCode == GALLERY_KITKAT_INTENT_CALLED) {
            // Will return "image:x*"
            Log.d("kitkat", "");

            if (data != null) {
                Uri originalUri = data.getData();
                Log.d("Original Uri : ", "------------" + originalUri);
                String wholeID = DocumentsContract.getDocumentId(originalUri);

                // Split at colon, use second item in the array
                String id = wholeID.split(":")[1];

                String[] column = {MediaStore.Images.Media.DATA};

                // where id is equal to
                String sel = MediaStore.Images.Media._ID + "=?";

                Cursor cursor = getContentResolver().
                        query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                column, sel, new String[]{id}, null);

                String filePath = "";
                if(cursor != null) {
                    int columnIndex = cursor.getColumnIndex(column[0]);
                    if (cursor.moveToFirst()) {
                        filePath = cursor.getString(columnIndex);
                    }
                    cursor.close();
                    gallery_file = new File(filePath);
                    crop_from_uri(gallery_file);
                }
            }
        }
    }

    public void crop_from_uri( File pictureFile){
        UCrop.of(Uri.fromFile(pictureFile), Uri.fromFile(pictureFile))
                .withAspectRatio(5, 5)
                .withMaxResultSize(maxWidth, maxHeight)
                .start(UserNameActivity.this);
    }

    @TargetApi(Build.VERSION_CODES.M)
    void requesreadtpermission(){
        requestPermissions(perms,100);
    }

    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "BigBull_JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public boolean check() {

        if(userName.getText().toString().trim().length()<3) {
            userName.setError("Minimum length of 3 characters required");
            return false;
        }

        if(type.equalsIgnoreCase("spouse") || type.equalsIgnoreCase("child")) {
            if(dependentName.getText().toString().trim().isEmpty()) {
                dependentName.setError("This field can't be empty");
                return false;
            }
        }
        return true;
    }

    public void getUserName() {

        progressDialog = new Utility().showDialog("Please wait while we are getting your info", UserNameActivity.this);
        progressDialog.setCancelable(false);

        new RetrofitClient().getInterface().getUserNameData(phoneNumber).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                Log.d("response", response.body()+"");

                if(response.isSuccessful()) {
                    if(response.body().get("userData")!=null) {
                        prevUsername = response.body().getAsJsonObject("userData").get("userName").getAsString().trim();
                        userName.setText(prevUsername);

                        if(response.body().getAsJsonObject("userData").get("type").getAsString().trim().equalsIgnoreCase("member")) {
                            member.setChecked(true);
                            dependentName.setVisibility(View.GONE);
                        }else {
                            if(response.body().getAsJsonObject("userData").get("type").getAsString().trim().equalsIgnoreCase("spouse")) {
                                spouse.setChecked(true);
                            }else if(response.body().getAsJsonObject("userData").get("type").getAsString().trim().equalsIgnoreCase("child")) {
                                child.setChecked(true);
                            }

                            dependentName.setVisibility(View.VISIBLE);
                            dependentName.setText(response.body().getAsJsonObject("userData").get("dependentName").getAsString()+"");
                        }

                        pushDataInSP(phoneNumber, userName.getText().toString().trim());

                        //Intent intent = new Intent(UserNameActivity.this, MainActivity.class);
                        //startActivity(intent);
                        //finish();

                    }
                }

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();
            }
        });
    }

    public void addPlayer(JsonObject data) {

        progressDialog = new Utility().showDialog("Please wait for updation to complete.", UserNameActivity.this);
        progressDialog.setCancelable(false);

        new RetrofitClient().getInterface().addPlayer(playerData).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(response.isSuccessful()) {

                    //Log.d("response", response.body()+"");

                    switch (response.body().get("flag").getAsString().trim()) {

                        case "USER_ADDED" :
                            //Intent intent = new Intent(UserNameActivity.this, MainActivity.class);
                            //intent.putExtra("phoneNumber", phoneNumber);
                            //intent.putExtra("userName", userName.getText().toString().trim());

                            pushDataInSP(phoneNumber, userName.getText().toString().trim());

                            //startActivity(intent);
                            //finish();
                            break;

                        case "USERNAME_EXIST" :
                            userName.setError("Username already exists");
                            //Toast.makeText(UserNameActivity.this, "Username already exist", Toast.LENGTH_SHORT).show();
                            break;

                        case "USER_DISABLED" : new Utility().showDialog("ACCOUNT DISABLED",
                                "Your account has been disabled and you can't login until it is enabled again. " +
                                        "Please contact your admin.", UserNameActivity.this);
                        break;

                        case "REGISTRATION_CLOSED" :
                            new Utility().showDialog("REGISTRATION CLOSED",
                                    "Registration has been closed by the admin. " +
                                            "Please contact your admin.", UserNameActivity.this);
                            break;

                        case "INTERNAL SERVER ERROR" :
                            Toast.makeText(UserNameActivity.this, "internal server error", Toast.LENGTH_SHORT).show();
                            break;
                    }

                }else {
                    try {
                        Log.d("error", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                progressDialog.dismiss();
            }
        });
    }

    public void pushDataInSP(String phoneNumber, String userName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phoneNumber", phoneNumber);
        editor.putString("userName", userName);
        editor.apply();
        editor.commit();
    }
}

