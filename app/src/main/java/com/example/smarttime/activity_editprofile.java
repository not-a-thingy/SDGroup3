package com.example.smarttime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class activity_editprofile extends AppCompatActivity {

    private EditText newUserName;
    private TextView newUserEmail;
    private Button save;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private ImageView updateProfilePic;
    private static int PICK_IMAGE = 123;
    Uri imagePath;
    private StorageReference storageReference;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null){
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                updateProfilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);
        newUserName = findViewById(R.id.et_name);
        newUserEmail = findViewById(R.id.tv_ProfileEmail2);
        save = findViewById(R.id.btn_update);
        updateProfilePic = findViewById(R.id.iv_profilePic2);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("User Info").child(firebaseAuth.getUid());
        storageReference = firebaseStorage.getReference();

        StorageReference mImageRef = FirebaseStorage.getInstance().getReference().child(firebaseAuth.getUid()).child("Images").child("Profile Pic");

        final long ONE_MEGABYTE = 1024 * 1024;

            mImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    DisplayMetrics dm = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dm);

                    updateProfilePic.setMinimumHeight(dm.heightPixels);
                    updateProfilePic.setMinimumWidth(dm.widthPixels);
                    updateProfilePic.setImageBitmap(bm);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    if(mImageRef==null){
                        updateProfilePic.setImageResource(R.drawable.ic_launcher);
                    }
                    //error handler
                }
            });

        updateProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);

            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfile classUserProfile = snapshot.getValue(UserProfile.class);
                newUserName.setText(classUserProfile.getName());
                newUserEmail.setText(classUserProfile.getEmailAddress());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity_editprofile.this, error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = newUserName.getText().toString();
                String email = newUserEmail.getText().toString();

                UserProfile userProfile = new UserProfile(email, name);

                databaseReference.setValue(userProfile);

                StorageReference imageReference = storageReference.child(firebaseAuth.getUid()).child("Images").child("Profile Pic");

                if(imagePath!=null){
                    UploadTask uploadTask = imageReference.putFile(imagePath);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(activity_editprofile.this,"Photo exceeds 1mb", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(activity_editprofile.this,"Upload Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(activity_editprofile.this, activity_userprofile.class));
                        }
                    });
                }else{
                    Toast.makeText(activity_editprofile.this,"Profile Data Upload Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(activity_editprofile.this, activity_userprofile.class));
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}