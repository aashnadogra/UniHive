package com.example.uniapp.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;

import com.example.uniapp.R;
import com.example.uniapp.adapters.AdapterImagesPicked;
import com.example.uniapp.databinding.ActivityAdCreateBinding;
import com.example.uniapp.utilities.Constants;
import com.example.uniapp.utilities.PreferenceManager;
import com.example.uniapp.utilities.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AdCreateActivity extends AppCompatActivity {

    private ActivityAdCreateBinding binding;
    private static final String TAG = "AD_CREATE_TAG";

    private ProgressDialog progressDialog;

    private Uri imageUri = null;

    private ArrayList<ModelImagePicked> imagePickedArrayList;
    private AdapterImagesPicked adapterImagesPicked;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        preferenceManager = new PreferenceManager(getApplicationContext());

        ArrayAdapter<String> adapterCategories = new ArrayAdapter<>(this, R.layout.row_category_act, Utils.categories );
        binding.categoryAct.setAdapter(adapterCategories);


        ArrayAdapter<String> adapterConditions = new ArrayAdapter<>(this, R.layout.row_condition_act, Utils.conditions);
        binding.conditionAct.setAdapter(adapterConditions);

        imagePickedArrayList = new ArrayList<>();
        loadImages();

        binding.toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        binding.toolbarAdImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickOptions();
            }
        });

        binding.postAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });


    }

    private void loadImages(){
        Log.d(TAG, "loadImages: ");
        adapterImagesPicked = new AdapterImagesPicked(this, imagePickedArrayList);
        binding.imagesRv.setAdapter(adapterImagesPicked);
    }
    private void showImagePickOptions(){
        Log.d(TAG, "showImagePickOptions: ");
        PopupMenu popupMenu = new PopupMenu(this, binding.toolbarAdImageBtn);
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Camera");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "Gallery");

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == 1){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                        String[] cameraPermissions = new String[] {android.Manifest.permission.CAMERA};
                        requestCameraPermissions.launch(cameraPermissions);
                    } else {
                        String[] cameraPermissions = new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestCameraPermissions.launch(cameraPermissions);
                    }
                }
                else if (itemId ==2){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                        pickImageGallery();
                    }else {
                        String storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                        requestStoragePermission.launch(storagePermission);
                    }
                }

                return true;
            }
        });
    }

    private final ActivityResultLauncher<String> requestStoragePermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isGranted) {
                    Log.d(TAG, "onActivityResult: isGranted: "+isGranted);

                    if (isGranted){
                        pickImageGallery();

                    }else{
                        Utils.toast(AdCreateActivity.this, "Storage Permission Denied...");
                    }
                }
            }
    );

    private final ActivityResultLauncher<String[]> requestCameraPermissions = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    Log.d(TAG, "onActivityResult: ");
                    Log.d(TAG, "onActivityResult: "+result.toString());

                    boolean areAllGranted = true;

                    for(Boolean isGranted : result.values()){
                        areAllGranted = areAllGranted && isGranted;
                    }
                    if (areAllGranted){
                        pickImageCamera();

                    }else{
                        Utils.toast(AdCreateActivity.this, "Camera or Storage or both permissions denied...");
                    }
                }
            }
    );

    private void pickImageGallery(){
        Log.d(TAG, "pickImageGallery: ");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }

    private void pickImageCamera(){
        Log.d(TAG, "pickImageCamera: ");

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "TEMPORARY_IMAGE");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "TEMPORARY_IMAGE_DESCRIPTION");

        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);
    }
    private final ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "onActivityResult: ");
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        imageUri = data.getData();

                        Log.d(TAG, "onActivityResult: imageUri: "+imageUri);
                        String timestamp = ""+ Utils.getTimestamp();

                        ModelImagePicked modelImagePicked = new ModelImagePicked(timestamp, imageUri, null, false);
                        imagePickedArrayList.add(modelImagePicked);
                        loadImages();
                    } else {
                        Utils.toast(AdCreateActivity.this, "Cancelled...!");
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "onActivityResult: ");
                    if(result.getResultCode() == Activity.RESULT_OK){

                        Log.d(TAG, "onActivityResult: imageUri: "+imageUri);
                        String timestamp = ""+ Utils.getTimestamp();

                        ModelImagePicked modelImagePicked = new ModelImagePicked(timestamp, imageUri, null, false);
                        imagePickedArrayList.add(modelImagePicked);
                        loadImages();
                    } else {
                        Utils.toast(AdCreateActivity.this, "Cancelled...!");
                    }
                }
            }
    );

    private String brand = "";
    private String category = "";
    private String condition = "";
    private String address = "";
    private String price = "";
    private String title = "";
    private String description = "";

    private void validateData(){
        Log.d(TAG, "validateData: ");

        brand = binding.brandEt.getText().toString().trim();
        category = binding.categoryAct.getText().toString().trim();
        condition = binding.conditionAct.getText().toString().trim();
        address = binding.locationEt.getText().toString().trim();
        price = binding.priceEt.getText().toString().trim();
        title = binding.titleEt.getText().toString().trim();
        description = binding.descriptionEt.getText().toString().trim();

        if (brand.isEmpty()){
            binding.brandEt.setError("Enter Brand");
            binding.brandEt.requestFocus();
        } else if(category.isEmpty()){
            binding.categoryAct.setError("Enter Category");
            binding.categoryAct.requestFocus();
        } else if (condition.isEmpty()) {
            binding.conditionAct.setError("Enter Conditions");
            binding.conditionAct.requestFocus();
        } else if (address.isEmpty()) {
            binding.locationEt.setError("Enter Location");
            binding.locationEt.requestFocus();
        } else if (title.isEmpty()) {
            binding.titleEt.setError("Enter Title");
            binding.titleEt.requestFocus();
        } else if (description.isEmpty()) {
            binding.descriptionEt.setError("Enter Description");
            binding.descriptionEt.requestFocus();
        } else if (imagePickedArrayList.isEmpty()) {
            Utils.toast(this, "Pick at-least one image");
        } else {
            postAd();
        }

    }

    private void postAd(){
        Log.d(TAG, "postAd: ");
        progressDialog.setMessage("Publishing Ad");
        progressDialog.show();

        long timestamp = Utils.getTimestamp();
        DatabaseReference refAds = FirebaseDatabase.getInstance().getReference("Ads");
        String keyId = refAds.push().getKey();

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        //String userid = String.valueOf(database.collection(Constants.KEY_COLLECTION_USERS).document(Constants.KEY_USER_ID));
        String userid = preferenceManager.getString(Constants.KEY_USER_ID);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", ""+keyId);
        hashMap.put("uid", ""+ userid);
        hashMap.put("brand", ""+brand);
        hashMap.put("category", ""+category);
        hashMap.put("condition", ""+condition);
        hashMap.put("address", ""+address);
        hashMap.put("price", ""+price);
        hashMap.put("title", ""+title);
        hashMap.put("description", ""+description);
        hashMap.put("status", ""+Utils.AD_STATUS_AVAILABLE);
        hashMap.put("timestamp", ""+timestamp);

        refAds.child(keyId)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Ad Published");
                        uploadImageStorage(keyId);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure", e);
                        progressDialog.dismiss();
                        Utils.toast(AdCreateActivity.this, "Failed to publish Ad due to "+e.getMessage());
                    }
                });

    }
    private void uploadImageStorage(String adId){
        Log.d(TAG, "uploadImageStorage: ");
        for(int i = 0 ; i < imagePickedArrayList.size() ; i++){
            ModelImagePicked modelImagePicked = imagePickedArrayList.get(i);
            String imageName = modelImagePicked.getId();
            String filePathAndName = "Ads/"+imageName;

            int imageIndexForProgress = i+1;

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);

            storageReference.putFile(modelImagePicked.getImageUri())
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                            String message = "Uploading " + imageIndexForProgress + " of " + imagePickedArrayList.size() + " images...";
                            Log.d(TAG, "onProgress: message: "+message);
                            progressDialog.setMessage(message);
                            progressDialog.show();
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG, "onSuccess: ");
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri uploadedImageUrl = uriTask.getResult();

                            if (uriTask.isSuccessful()){
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("id: ", ""+modelImagePicked.getId());
                                hashMap.put("imageUrl: ", ""+uploadedImageUrl);

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ads");
                                ref.child(adId).child("Images")
                                        .child(imageName)
                                        .updateChildren(hashMap);

                            }
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: ", e);
                            progressDialog.dismiss();

                        }
                    });
        }

    }

}