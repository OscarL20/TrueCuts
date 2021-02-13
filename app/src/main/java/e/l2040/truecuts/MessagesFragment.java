package e.l2040.truecuts;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseRegistrar;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MessagesFragment extends Fragment implements MessagesAdapter.OnRecyclerListener{

    MyResultReceiver myResultReceiver;

    ChatViewModel chatViewModel;
    ImageButton send;
    EditText editTextMessage;

    RecyclerView recyclerView;
    MessagesAdapter messagesAdapter;
    List<Message> messageList;

    boolean chatRoomExist;
    Boolean isBarber;



    String currentUserUid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myResultReceiver = (MyResultReceiver)context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        isBarber = myResultReceiver.getResult();

        chatViewModel = ViewModelProviders.of(getActivity()).get(ChatViewModel.class);
        send = view.findViewById(R.id.imgButton);
        editTextMessage = view.findViewById(R.id.editText);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        messageList = new ArrayList<>();

        messagesAdapter = new MessagesAdapter(getContext(), messageList, this);
        recyclerView.setAdapter(messagesAdapter);

        //if chat exists, load messages and assign a boolean variable as true
        //if chat doesnt exist, assign a boolean variable as false
        //when sending message, check if boolean is true or false
        //if true(chat does exist) place message accordingly
        //if false(chat doesnt exist) create chat



        if (!chatViewModel.getChatRoomId().equals("empty")){
            //since room does exist we know it could be either barber or user
            chatRoomExist = true;

            //load messages
            loadMessages(chatViewModel.getChatRoomId());

        }
        else{
            //since room does not exist we know its user
            chatRoomExist = false;

        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chatRoomExist){
                    //place message accordingly by
                    //update children for users/userId/chats/chatId and barbers/userId/chats/chatId (only have to update senderID,date&Time,Message)
                    //push the value for chats/chatId/messages/msgId

                    DatabaseReference userChatRef;
                    DatabaseReference barberChatRef;

                    if (isBarber){
                        userChatRef = FirebaseDatabase.getInstance().getReference()
                                .child("users").child(chatViewModel.getOtherPersonsUid()).child("chats").child(chatViewModel.getChatRoomId());

                        barberChatRef = FirebaseDatabase.getInstance().getReference()
                                .child("barbers").child(currentUserUid).child("chats").child(chatViewModel.getChatRoomId());
                    }else{
                        userChatRef = FirebaseDatabase.getInstance().getReference()
                                .child("users").child(currentUserUid).child("chats").child(chatViewModel.getChatRoomId());

                        barberChatRef = FirebaseDatabase.getInstance().getReference()
                                .child("barbers").child(chatViewModel.getOtherPersonsUid()).child("chats").child(chatViewModel.getChatRoomId());
                    }

                    Map<String, Object> updates = new HashMap<String, Object>();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd 'at' hh:mm");
                    String currentDateAndTime = sdf.format(new Date());

                    updates.put("senderId", currentUserUid);
                    updates.put("dateAndTime", currentDateAndTime);
                    updates.put("message", editTextMessage.getText().toString());

                    userChatRef.updateChildren(updates);
                    barberChatRef.updateChildren(updates);


                    DatabaseReference chats = FirebaseDatabase.getInstance().getReference().child("chats").child(chatViewModel.getChatRoomId());

                    Message messageObj = new Message(currentDateAndTime, editTextMessage.getText().toString(), currentUserUid);

                    chats.child("messages").push().setValue(messageObj);

                }
                else{
                    //create chat by
                    //push the value for both users/userId/chats/chatId and barbers/userId/chats/chatId (metaDataChat class)
                    //push the value for chats/chatId/messages/msgId (message class)
                    //in chats/chatId setValue (uid) to node: barber...to see who is barber

                    FirebaseDatabase.getInstance().getReference().child("barbers").child(chatViewModel.getOtherPersonsUid())
                            .child("images").child("ProfileImage")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot barberSnapShot) {


                            FirebaseDatabase.getInstance().getReference().child("users").child(currentUserUid)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot userSnapShot) {
                                    String userProfileImgUrl = userSnapShot.child("images").child("ProfileImage").getValue().toString();
                                    String userName = userSnapShot.child("username").getValue().toString();

                                    String barberProfileImgUrl = barberSnapShot.getValue().toString();


                                    DatabaseReference userChats = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserUid).child("chats");
                                    DatabaseReference barberChats = FirebaseDatabase.getInstance().getReference().child("barbers").child(chatViewModel.getOtherPersonsUid()).child("chats");

                                    String chatRoomId = userChats.push().getKey();

                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd 'at' hh:mm");
                                    String currentDateAndTime = sdf.format(new Date());

                                    MetaDataChat metaDataChatUser = new MetaDataChat(currentUserUid, currentDateAndTime, barberProfileImgUrl,
                                            editTextMessage.getText().toString(), chatViewModel.getOtherPersonsName(), chatViewModel.getOtherPersonsUid(),
                                            chatRoomId);

                                    userChats.child(chatRoomId).setValue(metaDataChatUser);

                                    MetaDataChat metaDataChatBarber = new MetaDataChat(currentUserUid, currentDateAndTime, userProfileImgUrl,
                                            editTextMessage.getText().toString(), userName, currentUserUid, chatRoomId);

                                    barberChats.child(chatRoomId).setValue(metaDataChatBarber);

                                    DatabaseReference chats = FirebaseDatabase.getInstance().getReference().child("chats").child(chatRoomId);

                                    Message messageObj = new Message(currentDateAndTime, editTextMessage.getText().toString(), currentUserUid);

                                    String msgId = chats.child("messages").push().getKey();
                                    chats.child("messages").child(msgId).setValue(messageObj);

                                    chats.child("barber").setValue(chatViewModel.getOtherPersonsUid());

                                    //change chatRoomExist to true
                                    chatRoomExist = true;
                                    chatViewModel.setChatRoomId(chatRoomId);

                                    loadMessages(chatViewModel.getChatRoomId());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });


        

    }

    public void loadMessages(String chatRoomId) {

        FirebaseDatabase.getInstance().getReference().child("chats").child(chatRoomId).child("messages")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                Message message;

                for (DataSnapshot snapshotMessage: dataSnapshot.getChildren()) {
                        message = snapshotMessage.getValue(Message.class);

                        messageList.add(message);
                }

                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRecyclerClick(int position) {

    }
}