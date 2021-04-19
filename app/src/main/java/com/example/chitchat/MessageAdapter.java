package com.example.chitchat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    ArrayList<Message>message_list;
    Context context;

    public MessageAdapter(ArrayList<Message> message_list, Context context) {
        this.message_list = message_list;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v=inflater.inflate(R.layout.message_layout,parent,false);
        return new MessageHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {

        holder.chat.setText(message_list.get(position).message);

        ConstraintLayout ccl=holder.ccl;
        TextView tv=holder.chat;
        if (message_list.get(position).sender.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

            tv.setBackgroundResource(R.drawable.message_design2);

            ConstraintSet constraintSet=new ConstraintSet();
            constraintSet.clone(ccl);
            constraintSet.clear(R.id.chat,ConstraintSet.LEFT);
            constraintSet.connect(R.id.chat,ConstraintSet.RIGHT,R.id.msg_lay,ConstraintSet.RIGHT,0);
            constraintSet.applyTo(ccl);

        }
        else {
            tv.setBackgroundResource(R.drawable.message_design);
            ConstraintSet constraintSet=new ConstraintSet();
            constraintSet.clone(ccl);
            constraintSet.clear(R.id.chat,ConstraintSet.RIGHT);
            constraintSet.connect(R.id.chat,ConstraintSet.LEFT,R.id.msg_lay,ConstraintSet.LEFT,0);
            constraintSet.applyTo(ccl);


        }

    }

    @Override
    public int getItemCount() {
        return message_list.size();
    }

    class MessageHolder extends RecyclerView.ViewHolder{
        ConstraintLayout ccl;
        TextView chat;

        public MessageHolder(@NonNull View itemView) {

            super(itemView);

            ccl=itemView.findViewById(R.id.msg_lay);
            chat=itemView.findViewById(R.id.chat);
        }
    }

}
