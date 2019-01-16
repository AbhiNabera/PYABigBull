package nabera.ranjan.abhinabera.pyabigbull.Dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.BitmapCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import nabera.ranjan.abhinabera.pyabigbull.Api.RetrofitClient;
import nabera.ranjan.abhinabera.pyabigbull.Api.Utility;
import nabera.ranjan.abhinabera.pyabigbull.Login.RegistrationActivity;
import nabera.ranjan.abhinabera.pyabigbull.R;
import nabera.ranjan.abhinabera.pyabigbull.UserActivities.About;
import nabera.ranjan.abhinabera.pyabigbull.UserActivities.Disclaimer;
import nabera.ranjan.abhinabera.pyabigbull.UserActivities.TermsAndConditions;
import nabera.ranjan.abhinabera.pyabigbull.UserActivities.TransactionsHistory.TransactionsHistory;
import nabera.ranjan.abhinabera.pyabigbull.UserActivities.Userstocks.UserStocks;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonObject;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class UserDataFragment extends Fragment {

    TextView myStocks, transactionsHistory, termsAndConditions, disclaimer, about, logout, privacypolicy;
    TextView userName, phoneNumber, balance, investmentText, changeText, percentchangeText;

    SwipeRefreshLayout refreshLayout;
    LinearLayout accountLayout;
    ExpandableLayout expandableLayout;

    int PERMISSION_ALL = 1;

    String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    RelativeLayout profileCard;
    RelativeLayout cameraButton;
    CircleImageView profilePhoto;
    ProgressBar progressBar;

    Call<JsonObject> uploadTask;

    JsonObject player;

    File gallery_file;
    String mCurrentPhotoPath = null;
    Uri currentPhotoUri = null;
    private String CAM_FILE_PATH = null;

    private static final int GALLERY_INTENT_CALLED = 4;
    private static final int GALLERY_KITKAT_INTENT_CALLED = 5, CHOICE_CAMERA = 7, PIC_CROP = 9,
            CROP_REQUEST_CODE = 11, PICK_PHOTO_CODE = 13;

    int maxWidth = 100;
    int maxHeight = 100;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_user_data_c, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        progressBar = (ProgressBar) view.findViewById(R.id.ProgressBar);
        profileCard = (RelativeLayout) view.findViewById(R.id.profileCard);
        cameraButton = (RelativeLayout) view.findViewById(R.id.cameraButton);
        profilePhoto = (CircleImageView) view.findViewById(R.id.profilePhoto);

        myStocks = (TextView) view.findViewById(R.id.myStocksText);
        transactionsHistory = (TextView) view.findViewById(R.id.transactionsHistoryText);
        termsAndConditions = (TextView) view.findViewById(R.id.termsAndConditionsText);
        disclaimer = (TextView) view.findViewById(R.id.disclaimerText);
        about = (TextView) view.findViewById(R.id.aboutText);
        logout = (TextView) view.findViewById(R.id.logoutText);
        privacypolicy = (TextView) view.findViewById(R.id.privacypolicy);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        expandableLayout = (ExpandableLayout) view.findViewById(R.id.expandableLayout);
        accountLayout = (LinearLayout) view.findViewById(R.id.accountLayout);

        userName = (TextView) view.findViewById(R.id.userNameText);
        phoneNumber = (TextView) view.findViewById(R.id.phoneNumberText);
        balance = (TextView) view.findViewById(R.id.balanceText);
        investmentText = (TextView) view.findViewById(R.id.investmentText);
        changeText = (TextView) view.findViewById(R.id.changeText);
        percentchangeText = (TextView) view.findViewById(R.id.percentchangeText);

        privacypolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://sites.google.com/view/aadevelopment/home");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        myStocks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), UserStocks.class);
                i.putExtra("name", "MY PORTFOLIO");
                i.putExtra("acc_bal", player.get("data").getAsJsonObject().get("Account").getAsJsonObject()
                        .get("avail_balance").getAsString());
                startActivity(i);
                //getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        transactionsHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), TransactionsHistory.class);
                i.putExtra("name", "HISTORY");
                startActivity(i);
                //getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        termsAndConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), TermsAndConditions.class);
                i.putExtra("name", "PLAYING CONDITIONS");
                startActivity(i);
                //getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        disclaimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Disclaimer.class);
                i.putExtra("name", "DISCLAIMER");
                startActivity(i);
                //getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), About.class);
                i.putExtra("name", "ABOUT");
                startActivity(i);
                //getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                removeDataFromSP();
                startActivity(new Intent(getActivity(), RegistrationActivity.class));
                getActivity().finish();
            }
        });

        accountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandableLayout.toggle();
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (uploadTask != null) {
                    if (uploadTask.isExecuted()) {
                        uploadTask.cancel();
                    }
                }
                getUserInfo();
            }
        });

        profileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (progressBar.getVisibility() != View.VISIBLE) {
                    /*
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/jpeg");
                    startActivityForResult(intent, GALLERY_KITKAT_INTENT_CALLED);
                    */

                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(intent, PICK_PHOTO_CODE);
                    }else {
                        Toast.makeText(getActivity(), "Error occurred", Toast.LENGTH_SHORT).show();
                        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/jpeg");
                        startActivityForResult(intent, GALLERY_KITKAT_INTENT_CALLED);
                    }

                } else {
                    Toast.makeText(getActivity(), "Wait for upload to finish", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (progressBar.getVisibility() != View.VISIBLE) {
                    if (checkCameraHardware(getActivity())) {

                        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {
                                // Error occurred while creating the File
                                ex.printStackTrace();
                            }
                            if (photoFile != null) {
                                currentPhotoUri = FileProvider.getUriForFile(getActivity(),
                                        "nabera.ranjan.android.fileprovider", photoFile);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri);
                                startActivityForResult(intent, CHOICE_CAMERA);
                            }
                        } else {
                            Toast.makeText(getActivity(), "Failed to open camera", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getActivity(), "Failed to open camera", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Wait for upload to finish", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getUserInfo();
    }

    public void setUserCard() {

        if (player.get("data").getAsJsonObject().get("imageUrl") != null) {

            if (!player.get("data").getAsJsonObject().get("imageUrl").toString().equalsIgnoreCase("null")) {
                Picasso.with(getActivity()).
                        load(player.get("data").getAsJsonObject().get("imageUrl").getAsString())
                        //.skipMemoryCache()
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .centerCrop()
                        .fit()
                        .into(profilePhoto);
            }
        }

        phoneNumber.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + "");
        userName.setText(player.get("data").getAsJsonObject().get("userName").getAsString());

        balance.setText(new Utility().getRoundoffData(player.get("data").getAsJsonObject().get("Account").getAsJsonObject()
                .get("avail_balance").getAsString()));
        investmentText.setText(new Utility().getRoundoffData(player.get("data").getAsJsonObject().get("Account").getAsJsonObject()
                .get("investment").getAsString()));

        String change = new Utility().getRoundoffData(player.get("data").getAsJsonObject().get("Account").getAsJsonObject()
                .get("change").getAsString());
        String percentchange = new Utility().getRoundoffData(player.get("data").getAsJsonObject().get("Account").getAsJsonObject()
                .get("percentchange").getAsString());

        changeText.setText(change + "");
        percentchangeText.setText(percentchange + "%");

        if (Double.parseDouble(percentchange) >= 0) {
            changeText.setTextColor(getResources().getColor(R.color.greenText));
            percentchangeText.setTextColor(getResources().getColor(R.color.greenText));
        } else {
            changeText.setTextColor(getResources().getColor(R.color.red));
            percentchangeText.setTextColor(getResources().getColor(R.color.red));
        }

        myStocks.setText("My Portfolio (" + player.get("data").getAsJsonObject().get("Account").getAsJsonObject()
                .get("stocks_count").getAsString() + ")");

        transactionsHistory.setText("Transaction History (" + player.get("data").getAsJsonObject().get("Account").getAsJsonObject()
                .get("txn_history").getAsJsonArray().size() + ")");

        //expandableLayout.toggle();
    }

    public void getUserInfo() {

        refreshLayout.setRefreshing(true);

        new RetrofitClient().getInterface().getPlayerinfo(FirebaseAuth.getInstance().getCurrentUser().
                getPhoneNumber().substring(3)).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                refreshLayout.setRefreshing(false);

                if (response.isSuccessful()) {
                    if (response.body().getAsJsonObject("data") != null) {
                        player = response.body();
                        setUserCard();
                    } else {

                        Toast.makeText(getActivity(), "error occured while fetching account", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        Log.d("error", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    public void removeDataFromSP() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Utility.MyPREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("phoneNumber");
        editor.remove("userName");
        editor.apply();
        editor.commit();
    }

    /*
    camera code starts here
     */
    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = userName.getText().toString().trim() + "";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        try {

            Log.d("onActivity result", "" + requestCode);

            if (resultCode == Activity.RESULT_CANCELED) {
                return;
            }

            if (requestCode == CHOICE_CAMERA) {

                if (mCurrentPhotoPath != null) {
                    CAM_FILE_PATH = mCurrentPhotoPath;
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            crop_from_uri(new File(mCurrentPhotoPath));
                        }
                    });
                }
            }

            if (requestCode == PIC_CROP) {

                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap thePic = extras.getParcelable("data");
                    profilePhoto.setImageBitmap(thePic);
                }
            }

            if (resultCode == RESULT_OK && requestCode == CROP_REQUEST_CODE) {
                Log.d("result:", " success");
                final Uri resultUri = UCrop.getOutput(data);
                if (gallery_file != null) {
                    CAM_FILE_PATH = gallery_file.getPath();
                }
                Bitmap temp = BitmapFactory.decodeFile(resultUri.getPath());
                profilePhoto.setImageBitmap(temp);
                //TODO: upload image here
                if (BitmapCompat.getAllocationByteCount(temp) < 100000) {
                    uploadFile();
                } else {
                    Toast.makeText(getActivity(), "Size limit 100kb", Toast.LENGTH_SHORT).show();
                }
                Log.d("bitmap size", "" + BitmapCompat.getAllocationByteCount(temp));
            } else if (resultCode == UCrop.RESULT_ERROR) {
                Log.d("result:", " failed");
                final Throwable cropError = UCrop.getError(data);

                Toast.makeText(getActivity(), "Unable to crop", Toast.LENGTH_SHORT).show();

            } else if (requestCode == GALLERY_INTENT_CALLED) {

                Log.d("gallery", "");
                Uri originalUri = data.getData();

                int columnIndex = 0;
                String[] projection = {MediaStore.Images.Media.DATA};

                Cursor cursor = null;
                if (originalUri != null) {
                    cursor = getActivity().getContentResolver().query(originalUri, projection, null, null, null);
                }
                if (cursor != null) {
                    cursor.moveToFirst();
                    columnIndex = cursor.getColumnIndex(projection[0]);
                    String picturePath = cursor.getString(columnIndex); // returns null
                    cursor.close();
                    final File gallery_file = new File(picturePath);
                    crop_from_uri(gallery_file);
                }

            } else if (requestCode == GALLERY_KITKAT_INTENT_CALLED) {
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

                    Cursor cursor = getActivity().getContentResolver().
                            query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    column, sel, new String[]{id}, null);

                    String filePath = "";
                    if (cursor != null) {
                        int columnIndex = cursor.getColumnIndex(column[0]);
                        if (cursor.moveToFirst()) {
                            filePath = cursor.getString(columnIndex);
                        }
                        cursor.close();
                        Log.d("filepath", "" + filePath);
                        gallery_file = new File(filePath);
                        crop_from_uri(gallery_file);
                    }
                }

            } else if(requestCode == PICK_PHOTO_CODE) {
                if (data != null) {
                    Uri photoUri = data.getData();
                    Log.d("photoUri", photoUri+"");

                    String[] projection = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getActivity().getContentResolver().query(photoUri, projection, null, null, null);
                    if (cursor != null) {
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToFirst();
                        String filePath = cursor.getString(column_index);
                        cursor.close();

                        gallery_file = new File(filePath);
                        crop_from_uri(gallery_file);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error occured while geting results", Toast.LENGTH_SHORT).show();
        }
    }

    public void crop_from_uri(File pictureFile) {
        try {
            UserDataFragment userDataFragment = (UserDataFragment) getFragmentManager().getFragments()
                    .get(((MainActivity) getActivity()).viewPager.getCurrentItem());
            UCrop.of(Uri.fromFile(pictureFile), Uri.fromFile(pictureFile))
                    .withAspectRatio(5, 5)
                    .withMaxResultSize(maxWidth, maxHeight)
                    .start(getActivity(), userDataFragment, CROP_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    private void setPic() {
        // Get the dimensions of the View
        int targetW = profilePhoto.getWidth();
        int targetH = profilePhoto.getHeight();
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor*2;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        //profilePhoto.setImageBitmap(bitmap);
        Picasso.with(getActivity()).
                load(new File(mCurrentPhotoPath))
                .skipMemoryCache()
                .fit()
                .into(profilePhoto);
        Log.d("bitmap size", ""+ BitmapCompat.getAllocationByteCount(bitmap));
    }
    /*
    private void setPic(Bitmap btmap) {
        // Get the dimensions of the View
        int targetW = profilePhoto.getWidth();
        int targetH = profilePhoto.getHeight();
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        //BitmapFactory.decode
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor*10;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        profilePhoto.setImageBitmap(bitmap);
        Log.d("bitmap size", ""+ BitmapCompat.getAllocationByteCount(bitmap));
    }
    */


    public void uploadFile() {

        progressBar.setVisibility(View.VISIBLE);

        StorageReference mStorageRef;
        mStorageRef = FirebaseStorage.getInstance().getReference();

        Uri file = Uri.fromFile(new File(CAM_FILE_PATH));
        StorageReference profileRef = mStorageRef.child("profile/" + FirebaseAuth.getInstance()
                .getCurrentUser().getPhoneNumber().substring(3) + "/" + userName.getText().toString().trim() + ".jpg");

        profileRef.putFile(file)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d("downloadurl", "" + uri);
                                updateUrl(uri + "", progressBar);
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        progressBar.setVisibility(View.GONE);
                    }
                });

    }

    public void updateUrl(String imageUrl, ProgressBar progressBar) {

        progressBar.setVisibility(View.VISIBLE);

        uploadTask = new RetrofitClient().getInterface().updateUrl(FirebaseAuth.getInstance().getCurrentUser()
                .getPhoneNumber().substring(3), imageUrl);

        uploadTask.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Successful update", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Unable to update image url", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getActivity(), "Unable to update image url", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}