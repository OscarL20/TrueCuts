package e.l2040.truecuts;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class BarberDetailsFragment extends Fragment implements View.OnClickListener {

    private MyViewModel model;
    ChatViewModel chatViewModel;
    Button bookAppointment;
    Button message;

    RecyclerView recyclerView;
    PhotoUriAdapter photoUriAdapter;
    List<PhotoUri> photoUriList;

    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_barber_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        model = ViewModelProviders.of(getActivity()).get(MyViewModel.class);
        chatViewModel = ViewModelProviders.of(getActivity()).get(ChatViewModel.class);

        CollapsingToolbarLayout collapsingToolbarLayout = getView().findViewById(R.id.collapsingToolBar);
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.black));
        collapsingToolbarLayout.setTitle(model.getBarberShop());
        collapsingToolbarLayout.setExpandedTitleTypeface(Typeface.DEFAULT_BOLD);

        ImageView barberShopImage = view.findViewById(R.id.barberShopImage);
        Picasso.with(getContext()).load(model.getBarberShopImage()).fit().centerCrop().into(barberShopImage);

        ImageView profileImage = view.findViewById(R.id.profileImage);
        Picasso.with(getContext()).load(model.getBarberProfileImage()).fit().centerCrop().into(profileImage);


        bookAppointment = view.findViewById(R.id.bookAppointment);
        bookAppointment.setOnClickListener(this);

        message = view.findViewById(R.id.messageButton);
        message.setOnClickListener(this);

        photoUriList = new ArrayList<>();

        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        for (Uri previousWorkUri : model.getListOfUris()){
            photoUriList.add(new PhotoUri(previousWorkUri));
        }


        photoUriAdapter = new PhotoUriAdapter(getContext(), photoUriList);
        recyclerView.setAdapter(photoUriAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView barberName = getView().findViewById(R.id.barberName);
        barberName.setText(model.getBarber());

        TextView address = getView().findViewById(R.id.actualAddress);
        address.setText(model.getAddress());

        TextView phone = getView().findViewById(R.id.actualPhone);
        phone.setText(model.getPhone());


        Spinner spinner = getView().findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), R.layout.support_simple_spinner_dropdown_item, model.getHours());
        spinner.setAdapter(adapter);


        FloatingActionButton floatingActionButton = getView().findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        final String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        switch (view.getId()){
            case R.id.back:
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new SearchFragment()).commit();
                break;

            case R.id.messageButton:
                //check if there's already chats under current user
                //if so, loop through current user chats and check for UID matching barber
                //*********if UID matches barber, send with viewModel (other persons name & other persons UID & chatRoomId)
                //*********if none of the UID's match barber, send same info, but with chatRoomId as "empty"
                //if no chats, send same info, but with chatRoomId as "empty"


                FirebaseDatabase.getInstance().getReference().child("users").child(currentUserUid).child("chats").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            boolean isEmpty = true;

                            for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {

                                if (model.getUid().equals(childSnapshot.child("otherPersonsId").getValue(String.class))){
                                    chatViewModel.setOtherPersonsName(model.getBarber());
                                    chatViewModel.setOtherPersonsUid(model.getUid());
                                    chatViewModel.setChatRoomId(childSnapshot.child("chatRoomId").getValue(String.class));
                                    isEmpty = false;
                                    Log.i("checking", "chat already exists");
                                    break;
                                }
                            }

                            if (isEmpty){
                                chatViewModel.setOtherPersonsName(model.getBarber());
                                chatViewModel.setOtherPersonsUid(model.getUid());
                                chatViewModel.setChatRoomId("empty");
                                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MessagesFragment()).commit();

                                Log.i("checking", "chat doesnt exist");
                            }
                            else{
                                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MessagesFragment()).commit();
                            }


                        }else{
                            chatViewModel.setOtherPersonsName(model.getBarber());
                            chatViewModel.setOtherPersonsUid(model.getUid());
                            chatViewModel.setChatRoomId("empty");
                            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MessagesFragment()).commit();

                            Log.i("checking", "data doesnt exist");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                break;


            case R.id.bookAppointment:
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new BookAppointmentFragment()).commit();
                break;

            case R.id.floatingActionButton:
                final FirebaseDatabase database = FirebaseDatabase.getInstance();

                FirebaseDatabase.getInstance().getReference().child("barbers").child(model.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Barber favorites = dataSnapshot.getValue(Barber.class);

                                DatabaseReference favoritesRef = database.getReference().child("users").
                                        child(currentUserUid).child("favorites").child(favorites.getUid());

                                favoritesRef.setValue(favorites);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

        }
    }
}
