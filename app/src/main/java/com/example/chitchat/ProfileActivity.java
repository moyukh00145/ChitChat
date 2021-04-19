package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.google.gson.internal.$Gson$Preconditions;

import java.io.IOException;


public class ProfileActivity extends AppCompatActivity {
    Button btn_upload;
    ProgressBar progressBar;
    ImageView imgPf;
    Uri imagepath;
    Bitmap image;
    String loadurl;
    LinearLayout biolay;
    TextView bioabout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btn_upload=findViewById(R.id.button_upload);
        progressBar=findViewById(R.id.progressBar);
        imgPf=findViewById(R.id.imagePf);
        biolay=findViewById(R.id.bio_lay);
        bioabout=findViewById(R.id.bio_about);


        imageload();
        aboutLoad();

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (image!=null){
                    btn_upload.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    FirebaseStorage.getInstance().getReference("Profilepicture/"+FirebaseAuth.getInstance().getCurrentUser().getUid()).putFile(imagepath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                btn_upload.setVisibility(View.VISIBLE);
                                btn_upload.setText("Upload Done");
                                task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()){
                                            FirebaseDatabase.getInstance().getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/imgurl").setValue(task.getResult().toString());

                                        }
                                    }
                                });
                            }
                            else{
                                Toast.makeText(ProfileActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    Toast.makeText(ProfileActivity.this, "Please select an image first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        imgPf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,101);
            }
        });

        biolay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ProfileActivity.this);

                alert.setTitle("Write something about you");

                final EditText input = new EditText(ProfileActivity.this);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        bioabout.setText(value);
                        FirebaseDatabase.getInstance().getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/about").setValue(value);

                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                alert.show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==101&&resultCode==RESULT_OK){
            imagepath=data.getData();
            try {
                image= MediaStore.Images.Media.getBitmap(this.getContentResolver(),imagepath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imgPf.setImageBitmap(image);
        }
    }
    public void imageload(){
        FirebaseDatabase.getInstance().getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/imgurl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.getValue().toString().isEmpty()) {

                    Glide.with(ProfileActivity.this).load(snapshot.getValue().toString()).placeholder(R.drawable.profile_logo).into(imgPf);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void aboutLoad(){
        FirebaseDatabase.getInstance().getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/about").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               bioabout.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}