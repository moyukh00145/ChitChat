package com.example.chitchat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class myadapter extends RecyclerView.Adapter<myadapter.myholder> {

    ArrayList<user>list;
    ArrayList<lastmessage>lastmessages;
    Context context;
    userClicked userClicked;
    longclicked longclicked;
    String ownuid,chat_room_id;

    public myadapter(Context context,ArrayList<user> list,ArrayList<lastmessage>lastmessages,userClicked clicked,longclicked longclicked1) {
        this.context=context;
        this.list = list;
        this.userClicked=clicked;
        this.lastmessages=lastmessages;
        this.longclicked=longclicked1;

        ownuid=FirebaseAuth.getInstance().getCurrentUser().getUid();

    }
    interface userClicked{
        void onUserClicked(int position);
    }
    interface longclicked{
        void onLongclick(int position);
    }


    @NonNull
    @Override
    public myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v=inflater.inflate(R.layout.singlerow,parent,false);
        return new myholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myholder holder, int position) {
        holder.tv1.setText(list.get(position).getName());
        Glide.with(context).load(list.get(position).getImgurl()).placeholder(R.drawable.profile_logo).into(holder.imv);
        String personuid=list.get(position).getUid();
        if (personuid.compareTo(ownuid)>0){
            chat_room_id=ownuid+personuid;
        }
        else if (personuid.compareTo(ownuid)==0){
            chat_room_id=ownuid+personuid;
        }
        else{
            chat_room_id=personuid+ownuid;
        }
        for (int i=0;i<lastmessages.size();i++){
            lastmessage obj=lastmessages.get(i);
            if (chat_room_id.equals(obj.getChatroomid())){
                holder.tv2.setText(obj.getLastmsg());
            }

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class myholder extends RecyclerView.ViewHolder{
        TextView tv1,tv2;
        ImageView imv;

        public myholder(@NonNull final View itemView) {
            super(itemView);
            tv1=itemView.findViewById(R.id.name);
            tv2=itemView.findViewById(R.id.lastmsg);
            imv=itemView.findViewById(R.id.imageprofile);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userClicked.onUserClicked(getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    longclicked.onLongclick(getAdapterPosition());
                    return false;
                }
            });
        }
    }
}
