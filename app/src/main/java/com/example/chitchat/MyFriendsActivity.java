package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyFriendsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    ArrayList<String>myfriends;
    ArrayList<user>firebase_list;
    ArrayList<user>actual_list;
    String ownUid,name;
    ArrayList<lastmessage>lastmessages;
    myadapter.userClicked userClicked;
    myadapter myadapter;
    ImageView addfriend;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);


        recyclerView=findViewById(R.id.recycle_view_myfriends);
        progressBar=findViewById(R.id.progress_myfriends);
        addfriend=findViewById(R.id.addfriend);

        recyclerView.setVisibility(View.GONE);
        myfriends=new ArrayList<>();
        firebase_list=new ArrayList<>();
        actual_list=new ArrayList<>();
        lastmessages=new ArrayList<>();

        ownUid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        auth=FirebaseAuth.getInstance();


        getname();

        getFriends();

        userClicked= new myadapter.userClicked() {
            @Override
            public void onUserClicked(int position) {

                                startActivity(new Intent(MyFriendsActivity.this,MessageActivity.class)
                        .putExtra("myname",name)
                        .putExtra("chat_personname",actual_list.get(position).getName())
                        .putExtra("chat_person_image",actual_list.get(position).getImgurl())
                        .putExtra("chat_person_uid",actual_list.get(position).getUid())
                        .putExtra("own_uid",FirebaseAuth.getInstance().getCurrentUser().getUid())
                );

            }
        };

        myadapter=new myadapter(MyFriendsActivity.this,actual_list,lastmessages,userClicked);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyFriendsActivity.this));
        recyclerView.setAdapter(myadapter);

        addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MyFriendsActivity.this,HomeActivity.class);
                startActivityForResult(intent,100);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            if (data!=null){
                String name=data.getStringExtra("Name");
                Toast.makeText(this, name+"added as Friend Successfully", Toast.LENGTH_SHORT).show();
            }
        }
        else if (resultCode==5){
            Toast.makeText(this, "User is already in your friend list", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.pf){
            Intent intent=new Intent(MyFriendsActivity.this,ProfileActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId()==R.id.signout){

            FirebaseDatabase.getInstance().getReference("user/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/online").setValue(false);

            Intent intent=new Intent(MyFriendsActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            FirebaseAuth.getInstance().signOut();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getname() {
        FirebaseDatabase.getInstance().getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name=snapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getFriends() {
        FirebaseDatabase.getInstance().getReference("Friends/"+ownUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myfriends.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    myfriends.add(snapshot1.getValue().toString());
                }
                getUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyFriendsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getUsers() {

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
                firebase_list.clear();
                actual_list.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){

                    firebase_list.add(snapshot1.getValue(user.class));
                }
                for (long i=0;i<firebase_list.size();i++){
                    user user=firebase_list.get((int) i);
                    if (myfriends.contains(user.getUid())){
                        actual_list.add(user);
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

    @Override
    public void onBackPressed() {
        FirebaseDatabase.getInstance().getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/online").setValue(false);
        Log.w("Back pressed","Back pressed");
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        if (auth.getCurrentUser()!=null) {
            FirebaseDatabase.getInstance().getReference("user/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/online").setValue(false);
            Log.w("pause", "pause");
        }
        super.onPause();
    }

    @Override
    protected void onRestart() {
        FirebaseDatabase.getInstance().getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/online").setValue(true);
        Log.w("restart","restart");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        FirebaseDatabase.getInstance().getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/online").setValue(true);
        Log.w("resume","resume");
        super.onResume();
    }

}