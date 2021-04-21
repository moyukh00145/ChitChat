package com.example.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {

    ImageView chat_person_image, send_btn;
    TextView chat_person_name;
    RecyclerView message_view;
    EditText chat_message;
    ProgressBar progressBar;
    ArrayList<Message> messages_list;
    String chat_personname, chat_person_imageurl, chat_person_uid, own_uid, chat_room_id;
    MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        chat_person_image = findViewById(R.id.chat_person_pic);
        send_btn = findViewById(R.id.chat_send_btn);
        chat_person_name = findViewById(R.id.chat_person_name);
        message_view = findViewById(R.id.chat_recycleview);
        chat_message = findViewById(R.id.chat_edittext);
        progressBar = findViewById(R.id.chat_progressbar);


        chat_personname = getIntent().getStringExtra("chat_personname");
        chat_person_imageurl = getIntent().getStringExtra("chat_person_image");
        chat_person_uid = getIntent().getStringExtra("chat_person_uid");
        own_uid = getIntent().getStringExtra("own_uid");

        chat_person_name.setText(chat_personname);
        if (!chat_person_imageurl.isEmpty()) {
            Glide.with(this).load(chat_person_imageurl).placeholder(R.drawable.profile_logo).into(chat_person_image);
        }


        messages_list = new ArrayList<>();

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = chat_message.getText().toString();
                if (msg.isEmpty()) {
                    Toast.makeText(MessageActivity.this, "Please write something before send", Toast.LENGTH_SHORT).show();
                } else {

                    FirebaseDatabase.getInstance().getReference("messages/" + chat_room_id).push().setValue(new Message(own_uid, chat_person_uid, chat_message.getText().toString()));
                    chat_message.setText("");
                }
            }
        });


        adapter = new MessageAdapter(messages_list, MessageActivity.this);
        message_view.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
        message_view.setAdapter(adapter);

        createchatroom(chat_person_uid, own_uid);
    }

    private void createchatroom(String chat_person_uid, String own_uid) {
        if (chat_person_uid.compareTo(own_uid) > 0) {
            chat_room_id = own_uid + chat_person_uid;
        } else if (chat_person_uid.compareTo(own_uid) == 0) {
            chat_room_id = own_uid + chat_person_uid;
        } else {
            chat_room_id = chat_person_uid + own_uid;
        }

        messageListener();
    }

    private void messageListener() {
        FirebaseDatabase.getInstance().getReference("messages/" + chat_room_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messages_list.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    messages_list.add(snapshot1.getValue(Message.class));
                }
                adapter.notifyDataSetChanged();
                message_view.scrollToPosition(messages_list.size() - 1);
                message_view.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                if (messages_list.size() > 0) {

                    FirebaseDatabase.getInstance().getReference("lastmessages/" + chat_room_id).setValue(new lastmessage(chat_room_id, messages_list.get(messages_list.size() - 1).message));
                } else {
                    FirebaseDatabase.getInstance().getReference("lastmessages/" + chat_room_id).setValue(new lastmessage(chat_room_id, ""));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}