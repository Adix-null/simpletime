package com.example.simpletime;


import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class uploadVideo extends AppCompatActivity {

    Button uploadv, save, back;
    TextView videoName;
    EditText videoTitle, videoDescription;
    ProgressDialog progressDialog;
    private FirebaseAuth fAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_control_java);
        videoName = (TextView)findViewById(R.id.upload_videoName);
        videoTitle = findViewById(R.id.upload_editTitle);
        videoDescription = findViewById(R.id.upload_editDescription);
        fAuth = FirebaseAuth.getInstance();

        // initialise layout
        uploadv = findViewById(R.id.uploadv);
        uploadv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code for showing progressDialog while uploading
                progressDialog = new ProgressDialog(uploadVideo.this);
                choosevideo();
            }
        });
        save = findViewById(R.id.upload_btnSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveVideo();
            }
        });

        back = findViewById(R.id.upload_btnBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void saveVideo(){
        if (videoName.getVisibility() == View.INVISIBLE){
            Toast.makeText(this, "Please select a video file.", Toast.LENGTH_SHORT).show();
        }
        else {
            if (videoTitle.getText().toString().equals("")){
                Toast.makeText(this, "Please enter video title.", Toast.LENGTH_SHORT).show();
            }
            else if (videoDescription.getText().toString().equals("")){
                Toast.makeText(this, "Please enter video description.", Toast.LENGTH_SHORT).show();
            }
            else {
                // Update firebase with entered title and description
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();
                    uploadvideo();
            }

        }
    }



    // choose a video from phone storage
    private void choosevideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 5);
    }

    Uri videouri;

    // startActivityForResult is used to receive the result, which is the selected video.
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            videouri = data.getData();
            String vName = getFileName(videouri);

            videoName.setText(vName);
            if (videoName.getVisibility() == View.INVISIBLE)
                videoName.setVisibility(View.VISIBLE);

            Toast.makeText(this, "Selected video: \n" + vName, Toast.LENGTH_LONG).show();

//            progressDialog.setTitle("Uploading...");
//            progressDialog.show();
//            uploadvideo();
        }
    }

    private String getfiletype(Uri videouri) {
        ContentResolver r = getContentResolver();
        // get the file type ,in this case its mp4
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(r.getType(videouri));
    }

    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)); // cia veikia, nekreipt demesio
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    // save the selected video in Firebase storage
//    final StorageReference reference = FirebaseStorage.getInstance().getReference("Videos/" +
//            fAuth.getCurrentUser().getUid() +"/" + System.currentTimeMillis() +"."  +  getfiletype(videouri));
//            reference.putFile(videouri);


    private void uploadvideo() {
        if (videouri != null) {
            String docName = "" + System.currentTimeMillis();
            // save the selected video in Firebase storage
            final StorageReference reference = FirebaseStorage.getInstance().getReference("Videos/" +
                     docName +"."  +  getfiletype(videouri));
            Map<String, Object> video = new HashMap<>();
            video.put("title", videoTitle.getText().toString());
            video.put("description", videoDescription.getText().toString());
            video.put("views", 0);
            video.put("timesRated", 0);
            video.put("rating", 0);
            video.put("uploaderID", fAuth.getCurrentUser().getUid());
            video.put("reports", Arrays.asList(0, 0, 0, 0, 0, 0));
            video.put("reportsOther", Arrays.asList());
            reference.putFile(videouri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "WORKS");
                    progressDialog.dismiss();

                    Log.d(TAG, "WORKS");
                    db.collection("videos").document(docName)
                            .set(video)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Added document with ID: "+ docName);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document to collection", e);
                                }
                            });
//                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
//                    while (!uriTask.isSuccessful()) ;
//                    // get the link of video
//                    String downloadUri = uriTask.getResult().toString();
//                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Video");
//                    HashMap<String, String> map = new HashMap<>();
//                    map.put("videolink", downloadUri);
//                    reference1.child("" + System.currentTimeMillis()).setValue(map);
                    // Video uploaded successfully
                    // Dismiss dialog
                    Toast.makeText(uploadVideo.this, "Video successfully uploaded!", Toast.LENGTH_LONG).show();
                    finish();



//                    Toast.makeText(uploadVideo.this, "Video Uploaded!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Error, Image not uploaded
                    progressDialog.dismiss();
                    Toast.makeText(uploadVideo.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                // Progress Listener for loading
                // percentage on the dialog box
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    // show the progress bar
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                }
            });
        }
    }
}
