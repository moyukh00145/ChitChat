package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.squareup.okhttp.internal.DiskLruCache;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<user>list;
    ProgressBar progressBar;
    myadapter.userClicked userClicked;
    myadapter myadapter;
    ArrayList<lastmessage>lastmessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        progressBar=findViewById(R.id.progress_home);
        recyclerView=findViewById(R.id.recycle_view);
        recyclerView.setVisibility(View.GONE);

        list=new ArrayList<>();
        lastmessages=new ArrayList<>();

        getUser();

        userClicked=new myadapter.userClicked() {
            @Override
            public void onUserClicked(int position) {
                startActivity(new Intent(HomeActivity.this,MessageActivity.class)
                        .putExtra("chat_personname",list.get(position).getName())
                        .putExtra("chat_person_image",list.get(position).getImgurl())
                        .putExtra("chat_person_uid",list.get(position).getUid())
                        .putExtra("own_uid",FirebaseAuth.getInstance().getCurrentUser().getUid())
                );
            }
        };

        myadapter=new myadapter(HomeActivity.this,list,lastmessages,userClicked);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        recyclerView.setAdapter(myadapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.pf){
            Intent intent=new Intent(HomeActivity.this,ProfileActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId()==R.id.signout){
            FirebaseAuth.getInstance().signOut();
            Intent intent=new Intent(HomeActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public void getUser(){
        final String[] name = new String[1];
        FirebaseDatabase.getInstance().getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name[0] =snapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        FirebaseDatabase.getInstance().getReference("lastmessages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    lastmessages.add(snapshot1.getValue(lastmessage.class));
                }

                myadapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    if (!snapshot1.child("name").getValue().equals(name[0])){
                        list.add(snapshot1.getValue(user.class));
                    }


                }
                myadapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}