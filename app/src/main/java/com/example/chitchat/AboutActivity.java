package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AboutActivity extends AppCompatActivity {

    ImageView aboitPf_show;
    TextView bio_about_show;
    String imgurl,uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        aboitPf_show=findViewById(R.id.imagePf_about_show);
        bio_about_show=findViewById(R.id.bio_about_show);
        imgurl=getIntent().getStringExtra("imgurl");
        uid=getIntent().getStringExtra("uid");

        FirebaseDatabase .getInstance().getReference("user/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/online").setValue(true);

        if (imgurl!=null){
            Glide.with(this).load(imgurl).placeholder(R.drawable.profile_logo).into(aboitPf_show);
        }
        if (uid!=null){
            FirebaseDatabase.getInstance().getReference("user/"+uid+"/about").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    bio_about_show.setText(snapshot.getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }
}