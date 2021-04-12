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

import java.util.ArrayList;

public class myadapter extends RecyclerView.Adapter<myadapter.myholder> {

    ArrayList<user>list;
    Context context;
    userClicked userClicked;

    public myadapter(Context context,ArrayList<user> list,userClicked clicked) {
        this.context=context;
        this.list = list;
        this.userClicked=clicked;
    }
    interface userClicked{
        void onUserClicked(int position);
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
        holder.tv2.setText(list.get(position).getLastmsg());
        Glide.with(context).load(list.get(position).getImgurl()).placeholder(R.drawable.profile_logo).into(holder.imv);

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
        }
    }
}
