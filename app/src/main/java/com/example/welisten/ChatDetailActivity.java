package com.example.welisten;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.welisten.Adapter.ChatAdapter;
import com.example.welisten.Models.MessageModel;
import com.example.welisten.databinding.ActivityChatDetailBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class ChatDetailActivity extends AppCompatActivity {

    ActivityChatDetailBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        final String senderId = auth.getUid();
        String receiverId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");

        binding.userName.setText(userName);

        Picasso.get().load(profilePic).placeholder(R.drawable.profile_photo).into(binding.imageProfile);

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatDetailActivity.this , HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        final ArrayList<MessageModel> messageModels = new ArrayList<>();
        final ChatAdapter chatAdapter = new ChatAdapter(messageModels , this);
        binding.chatDetailRecyclerView.setAdapter(chatAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatDetailRecyclerView.setLayoutManager(layoutManager);

        final String senderRoom = senderId+receiverId;
        final String receiverRoom = receiverId+senderId;

        database.getReference().child("chats").child(senderRoom)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                messageModels.clear(); //to clear the list so that previous message do not repeat
                                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                                    MessageModel model = snapshot1.getValue(MessageModel.class);
                                    messageModels.add(model);
                                }
                                chatAdapter.notifyDataSetChanged(); //to update recycler view instantly
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = binding.sendingMessage.getText().toString();
                final MessageModel model = new MessageModel(senderId , message);
                model.setTimeStamp(new Date().getTime());
                binding.sendingMessage.setText("");

                database.getReference().child("chats").child(senderRoom)
                        .push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                database.getReference().child("chats").child(receiverRoom)
                                        .push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        });
                            }
                        });
            }
        });

        setVoiceCall(receiverId, userName);
        setVideoCall(receiverId, userName);

//        binding.call.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setVoiceCall(receiverId, userName);
//            }
//        });
//
//        binding.vedioCall.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setVideoCall(receiverId, userName);
//            }
//        });

    }

    void startService(){
        Application application = getApplication();
        long appID = 904089373;
        String appSign = "157aefbbf942f202c5a20fa27971bb79e541d46ac42f261a2dcd7a3f512c171b";
        String userName = auth.getCurrentUser().toString();
        String userID = auth.getUid().toString();


        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();
        callInvitationConfig.notifyWhenAppRunningInBackgroundOrQuit = true;
        ZegoNotificationConfig notificationConfig = new ZegoNotificationConfig();
        notificationConfig.sound = "zego_uikit_sound_call";
        notificationConfig.channelID = "CallInvitation";
        notificationConfig.channelName = "CallInvitation";
        ZegoUIKitPrebuiltCallInvitationService.init(getApplication(), appID, appSign, userID, userName,callInvitationConfig);
    }

    void setVoiceCall(String targetUserID, String targetUserName){

        binding.call.setIsVideoCall(false);
        binding.call.setResourceID("zego_uikit_call");
        binding.call.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserID,targetUserName)));
    }

    void setVideoCall(String targetUserID, String targetUserName){

        startService();
        binding.videoCall.setIsVideoCall(true);
        binding.videoCall.setResourceID("zego_uikit_call");
        binding.videoCall.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserID,targetUserName)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZegoUIKitPrebuiltCallInvitationService.unInit();
    }
}