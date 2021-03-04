package e.l2040.truecuts;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;



public class ChatRoomsFragment extends Fragment implements ChatRoomsAdapter.OnRecyclerListener{

    android.widget.Toolbar toolbar;

    RecyclerView recyclerView;
    ChatRoomsAdapter chatRoomsAdapter;
    List<MetaDataChat> metaDataChatList;

    ChatViewModel chatViewModel;

    MyResultReceiver myResultReceiver;
     Boolean isBarber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_rooms, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myResultReceiver = (MyResultReceiver)context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Messages");
        toolbar.setTitleTextAppearance(getContext(), R.style.ToolbarTextAppearance);
        toolbar.setNavigationIcon(R.drawable.navigation_menu_icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawerLayout);
                drawerLayout.openDrawer(Gravity.LEFT);
            }});

        chatViewModel = ViewModelProviders.of(getActivity()).get(ChatViewModel.class);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        metaDataChatList = new ArrayList<>();
        chatRoomsAdapter = new ChatRoomsAdapter(getContext(), metaDataChatList, this);
        recyclerView.setAdapter(chatRoomsAdapter);


        isBarber = myResultReceiver.getResult();
        final String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (isBarber){

            FirebaseDatabase.getInstance().getReference().child("barbers").child(currentUserUid).child("chats").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    metaDataChatList.clear();
                    MetaDataChat metaDataChat;

                    for (DataSnapshot chatRoomData: dataSnapshot.getChildren()){
                            metaDataChat = chatRoomData.getValue(MetaDataChat.class);

                            metaDataChatList.add(metaDataChat);
                    }

                    chatRoomsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{

            FirebaseDatabase.getInstance().getReference().child("users").child(currentUserUid).child("chats").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    metaDataChatList.clear();
                    MetaDataChat metaDataChat;

                    for (DataSnapshot chatRoomData: dataSnapshot.getChildren()){
                        metaDataChat = chatRoomData.getValue(MetaDataChat.class);

                        metaDataChatList.add(metaDataChat);
                    }

                    chatRoomsAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }



    @Override
    public void onRecyclerClick(int position) {

        MetaDataChat oneMetaDataChat = metaDataChatList.get(position);

        chatViewModel.setOtherPersonsName(oneMetaDataChat.getOtherPersonsName());
        chatViewModel.setOtherPersonsUid(oneMetaDataChat.getOtherPersonsId());
        chatViewModel.setChatRoomId(oneMetaDataChat.getChatRoomId());

        Log.i("testing position", Integer.toString(position));

        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MessagesFragment()).commit();

    }
}
