package com.example.welisten.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.welisten.ChatDetailActivity;
import com.example.welisten.Models.Users;
import com.example.welisten.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.viewHolder>{

    ArrayList<Users> list;
    Context context;

    public UsersAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.sample_chat_bar , parent, false);
        return new viewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        // in this method we set the data to the layout
        Users user= list.get(position);
        Picasso.get().load(user.getProfilepic()).placeholder(R.drawable.profile_photo).into(holder.image);
        holder.name.setText(user.getUserName());

        FirebaseDatabase.getInstance().getReference().child("chats")
                        .child(FirebaseAuth.getInstance().getUid() + user.getUserId())
                                .orderByChild("timestamp")
                                        .limitToLast(1)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if(snapshot.hasChildren()){
                                                            for(DataSnapshot snapshot1 : snapshot.getChildren()){
                                                                holder.msg.setText(snapshot1.child("message").getValue().toString());
                                                            }
                                                        }
                                                        else{
                                                            holder.msg.setText("");
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context , ChatDetailActivity.class);
                intent.putExtra("userId", user.getUserId());
                intent.putExtra("profilePic", user.getProfilepic());
                intent.putExtra("userName", user.getUserName());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name, msg;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_profile);
            name = itemView.findViewById(R.id.text_name);
            msg = itemView.findViewById(R.id.text_msg);

        }
    }
}
