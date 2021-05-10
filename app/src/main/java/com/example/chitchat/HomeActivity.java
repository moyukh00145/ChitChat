package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.okhttp.internal.DiskLruCache;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class HomeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<user> list;
    ProgressBar progressBar;
    myadapter.userClicked userClicked;
    myadapter.longclicked longclicked;
    myadapter myadapter;
    ArrayList<lastmessage> lastmessages;
    ArrayList<String> friends;
    String name;
    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        progressBar = findViewById(R.id.progress_home);
        recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setVisibility(View.GONE);

        list = new ArrayList<>();
        lastmessages = new ArrayList<>();
        friends = new ArrayList<>();


        FirebaseDatabase.getInstance().getReference("user/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/online").setValue(true);
        getUser();

        FirebaseDatabase.getInstance().getReference("user/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/online").setValue(true);

        userClicked = new myadapter.userClicked() {
            @Override
            public void onUserClicked(int position) {
                pos=position;
                FirebaseDatabase.getInstance().getReference("Friends/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        friends.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            friends.add(snapshot1.getValue().toString());
                        }
                        if (!friends.contains(list.get(pos).getUid())){
                            Intent return_to_friendsActivity = new Intent();
                            FirebaseDatabase.getInstance().getReference("Friends/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).push().setValue(list.get(pos).getUid());
                            return_to_friendsActivity.putExtra("Name", list.get(pos).getName());
                            setResult(RESULT_OK, return_to_friendsActivity);
                            HomeActivity.this.finish();
                        }
                        else{

                            Intent return_to_friendsActivity = new Intent();
                            setResult(5, return_to_friendsActivity);
                            HomeActivity.this.finish();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            }
        };

        longclicked=new myadapter.longclicked() {
            @Override
            public void onLongclick(int position) {

            }
        };

        myadapter = new myadapter(HomeActivity.this, list, lastmessages, userClicked,longclicked);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        recyclerView.setAdapter(myadapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.pf) {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.signout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getUser() {
        FirebaseDatabase.getInstance().getReference("user/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name = snapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        FirebaseDatabase.getInstance().getReference("lastmessages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
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
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (!snapshot1.child("name").getValue().equals(name)) {
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


//    @Override
//    public void onBackPressed() {
//        FirebaseDatabase.getInstance().getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/online").setValue(false);
//        Log.w("Back pressed","Back pressed");
//        super.onBackPressed();
//    }
//
//    @Override
//    protected void onPause() {
//        FirebaseDatabase.getInstance().getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/online").setValue(false);
//        Log.w("pause","pause");
//        super.onPause();
//    }
//
//    @Override
//    protected void onRestart() {
//        FirebaseDatabase.getInstance().getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/online").setValue(true);
//        Log.w("restart","restart");
//        super.onRestart();
//    }
//
//    @Override
//    protected void onResume() {
//        FirebaseDatabase.getInstance().getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/online").setValue(true);
//        Log.w("resume","resume");
//        super.onResume();
//    }
}