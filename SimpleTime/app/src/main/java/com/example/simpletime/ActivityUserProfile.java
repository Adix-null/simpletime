package com.example.simpletime;

import static android.content.ContentValues.TAG;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firestore.v1.WriteResult;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActivityUserProfile extends AppCompatActivity {

    private ImageView profileImage;

    private DatePickerDialog datePickerDialog;
    private Button dateButton;

    private Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth fAuth;
    private FirebaseFirestore firebaseFirestore;
    EditText profileUsername, profileName, profileSurname, profileEmail, profileGender;
    Button btnBack;
    String firestoreUsername, firestoreName, firestoreSurname, uid, firestoreEmail, date2;
    Date firestoreBirthdate;
    Timestamp profileBirthdate;
    Boolean firestoreGender;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    int firebaseYear, firebaseMonth, firebaseDay;

//    Date varBirthdate;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        profileImage = findViewById(R.id.userProfile_profilePicture);
        profileUsername = findViewById(R.id.userProfile_editUsername);
        profileEmail = findViewById(R.id.userProfile_editEmail);
        profileName = findViewById(R.id.userProfile_editName);
        profileSurname = findViewById(R.id.userProfile_editSurname);
//      profileBirthdate = findViewById(R.id.userProfile_editAge);
        btnBack = findViewById(R.id.userProfile_btnBack);
        firebaseFirestore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        uid = fAuth.getCurrentUser().getUid();


        //getFirestoreVariables();
        /*initDatePicker();
        dateButton = findViewById(R.id.userProfile_editAge);

//        loadImagePlaceholder();
        StorageReference profileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //choosePicture();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //finish();
            }
        });*/

//        showAllUserData();
    }

    private String getTodaysDate()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    Date varBirthdate;
    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day, 00, 00, 00);
                varBirthdate = calendar.getTime();
                date = makeDateString(day, month + 1, year);
                Log.d(TAG, "JONAS" + date);
                Log.d(TAG, "MARGOLIS" + date2);
                dateButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }

    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + ", " + year;
    }

    private String getMonthFormat(int month)
    {
        String[] months= {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        try {
            return months[month - 1];
        }
        catch (Exception e)
        {
            //default should never happen
            return "Jan";
        }
    }

    public void openDatePicker(View view)
    {
        datePickerDialog.show();
    }


    private void showAllUserData(){
        profileUsername.setText(firestoreUsername);
        profileEmail.setText(firestoreEmail);
        profileName.setText(firestoreName);
        profileSurname.setText(firestoreSurname);


        Date date = firestoreBirthdate;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        firebaseYear = cal.get(Calendar.YEAR);
        firebaseMonth = cal.get(Calendar.MONTH);
        firebaseDay = cal.get(Calendar.DAY_OF_MONTH);
//        getMonthFormat(firebaseMonth) + " " + firebaseDay + ", " + firebaseYear;
//        date2 = makeDateString(firebaseDay,firebaseMonth+1,firebaseYear);
        dateButton.setText(getMonthFormat(firebaseMonth+1) + " " + firebaseDay + ", " + firebaseYear);

//        profileAge.setText(firestoreAge.toString());

//        if (firestoreGender.toString() == "true")
//        {
//            gender = "Male";
//        }
//        else{
//            gender = "Female";
//        }
//        profileGender.setText(gender);
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }



    public void update_profile(View view){
        getFirestoreVariables();


        if(isSurnameChanged() || isEmailChanged() || isNameChanged() || isUsernameChanged() || isAgeChanged()){

            if (isSurnameChanged()){
                changeSurname();
            }
            if (isNameChanged()){
                changeName();
            }
            if (isUsernameChanged()){
                changeUsername();
            }
            if (isAgeChanged()){
                changeAge();
            }

            if (isEmailChanged()){
                changeEmail();
            }
            Toast.makeText(this,"Data saved",Toast.LENGTH_SHORT).show();


            finish();
        }
        else{
            Toast.makeText(this,"Saved data. Nothing to update.",Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isEmailChanged() {

        if (!firestoreEmail.equals(profileEmail.getText().toString()))
//                &&
//                isEmailValid(profileEmail.getText().toString()))
        {
//            user.updateEmail(profileEmail.getText().toString())
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//                                Log.d(TAG, "User email address updated.");
//                            }
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.d(TAG, "User email was not updated.");
//                        }
//                    });
            return true;
        }
//        else if (!user.getEmail().equals(profileEmail.getText().toString()) &&
//                !isEmailValid(profileEmail.getText().toString()))
//        {
//            Toast.makeText(this,"Failed to update info, email is invalid.",Toast.LENGTH_SHORT).show();
//            return false;
//        }
        else {
            return false;
        }
    }

    private void changeEmail(){
        DocumentReference docRef = firebaseFirestore.collection("users").document(uid);
        if (isEmailValid(profileEmail.getText().toString())){
            docRef.update("email", profileEmail.getText().toString());
        }
        else {
            Toast.makeText(this,"Failed to update email, enter a valid email",Toast.LENGTH_SHORT).show();
        }

    }


    private void changeUsername(){
        DocumentReference docRef = firebaseFirestore.collection("users").document(uid);
        docRef.update("username", profileUsername.getText().toString());
    }
    private boolean isUsernameChanged() {

        if (!firestoreUsername.equals(profileUsername.getText().toString())){
            return true;
        }
        else{
            return false;
        }
//        if (!user.getDisplayName().equals(profileUsername.getText().toString())){
//
//            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                    .setDisplayName(profileUsername.getText().toString())
//                    .build();
//
//            user.updateProfile(profileUpdates)
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//                                Log.d(TAG, "User profile updated.");
//                            }
//                        }
//                    });
//            return true;
//        }
//        else{
//            return false;
//        }

    }

    private void getFirestoreVariables() {

        DocumentReference docRef = firebaseFirestore.collection("users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        firestoreEmail = document.getString("email");
                        firestoreUsername = document.getString("username");
                        firestoreName = document.getString("name");
                        firestoreSurname = document.getString("surname");
                        firestoreBirthdate = document.getDate("birthdate");   // NEEDS TO BE TIMESTAMP
                        firestoreGender = document.getBoolean("gender");
//                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        showAllUserData();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to get firestore variables");
            }
        });
    }

    private boolean isAgeChanged(){
        if (date != date2){
            return true;
        }
        else{
            return false;
        }
    }

    private void changeAge(){
        DocumentReference docRef = firebaseFirestore.collection("users").document(uid);
        docRef.update("birthdate", varBirthdate);
    }


    private void changeName(){
        DocumentReference docRef = firebaseFirestore.collection("users").document(uid);
        docRef.update("name", profileName.getText().toString());
    }

    private boolean isNameChanged(){
        if (!firestoreName.equals(profileName.getText().toString())){
            return true;
        }
        else{
            return false;
        }
    }

    private void changeSurname(){
        DocumentReference docRef = firebaseFirestore.collection("users").document(uid);
        docRef.update("surname", profileSurname.getText().toString());
    }
    private boolean isSurnameChanged(){
        if (!firestoreSurname.equals(profileSurname.getText().toString())){
            return true;
        }
        else{
            return false;
        }
    }


    private void choosePicture(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();

            //profileImage.setImageURI(imageUri);
            uploadPicture();

        }
    }








//    private void loadImagePlaceholder(){
//        Picasso.get()
//                .load( R.drawable.loading_image )
//                .error( R.drawable.error_image )
//                .placeholder( R.drawable.progress_animation )
//                .into( profileImage );
//    }

    private void uploadPicture() {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();

        final String randomKey = UUID.randomUUID().toString();
//        StorageReference fileRef = storageReference.child("images/" + randomKey);
        StorageReference fileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
        fileRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
//                        Snackbar.make(findViewById(android.R.id.content),"Image uploaded.", Snackbar.LENGTH_LONG).show();
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).into(profileImage);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(),"Failed To Upload", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progressPercent = (100.00 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        pd.setMessage("Percentage: " + (int) progressPercent + "%");
                    }
                });

    }
}