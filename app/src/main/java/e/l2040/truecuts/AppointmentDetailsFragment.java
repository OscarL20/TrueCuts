package e.l2040.truecuts;

import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class AppointmentDetailsFragment extends Fragment implements View.OnClickListener {

    AppointmentViewModel appointmentViewModel;
    ChatViewModel chatViewModel;

    Button message;
    Button cancel;

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
        return inflater.inflate(R.layout.fragment_appointment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appointmentViewModel = ViewModelProviders.of(getActivity()).get(AppointmentViewModel.class);
        chatViewModel = ViewModelProviders.of(getActivity()).get(ChatViewModel.class);

        CollapsingToolbarLayout collapsingToolbarLayout = getView().findViewById(R.id.collapsingToolBar);
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.black));
        collapsingToolbarLayout.setTitle(appointmentViewModel.getBarberShopName());
        collapsingToolbarLayout.setExpandedTitleTypeface(Typeface.DEFAULT_BOLD);


        TextView address = view.findViewById(R.id.actualAddress);
        address.setText(appointmentViewModel.getAddress());

        TextView barberName = view.findViewById(R.id.barberName);
        barberName.setText(appointmentViewModel.getBarberName());

        TextView date = view.findViewById(R.id.actualDate);
        date.setText(appointmentViewModel.getDate());

        TextView time = view.findViewById(R.id.actualTime);
        time.setText(appointmentViewModel.getTime());

        TextView phone = view.findViewById(R.id.actualPhone);
        phone.setText(appointmentViewModel.getPhone());

        ImageView barberShopImage = view.findViewById(R.id.imageView);
        Picasso.with(getContext()).load(appointmentViewModel.getBarberShopImage()).fit().centerCrop().into(barberShopImage);

        ImageView profileImage = view.findViewById(R.id.profileImageView);
        Picasso.with(getContext()).load(appointmentViewModel.getBarberProfileImage()).fit().centerCrop().into(profileImage);


        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingAction);
        floatingActionButton.setOnClickListener(this);

        message = view.findViewById(R.id.messageButton);
        message.setOnClickListener(this);

        cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);

        photoUriList = new ArrayList<>();

        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        for (Uri previousWorkUri : appointmentViewModel.getListOfUris()){
            photoUriList.add(new PhotoUri(previousWorkUri));
        }


        photoUriAdapter = new PhotoUriAdapter(getContext(), photoUriList);
        recyclerView.setAdapter(photoUriAdapter);


    }

    @Override
    public void onClick(View view) {
        final String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        switch (view.getId()){


            /*case R.id.directions:
                //open google maps
                break;*/

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

                                if (appointmentViewModel.getUid().equals(childSnapshot.child("otherPersonUid").getValue(String.class))){
                                    chatViewModel.setOtherPersonsName(appointmentViewModel.getBarberName());
                                    chatViewModel.setOtherPersonsUid(appointmentViewModel.getUid());
                                    chatViewModel.setChatRoomId(childSnapshot.child("chatRoomId").getValue(String.class));
                                    isEmpty = false;
                                    break;
                                }
                            }

                            if (isEmpty){
                                chatViewModel.setOtherPersonsName(appointmentViewModel.getBarberName());
                                chatViewModel.setOtherPersonsUid(appointmentViewModel.getUid());
                                chatViewModel.setChatRoomId("empty");
                                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MessagesFragment()).commit();
                            }
                            else{
                                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MessagesFragment()).commit();
                            }


                        }else{
                            chatViewModel.setOtherPersonsName(appointmentViewModel.getBarberName());
                            chatViewModel.setOtherPersonsUid(appointmentViewModel.getUid());
                            chatViewModel.setChatRoomId("empty");
                            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MessagesFragment()).commit();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                break;

            case R.id.cancel:
                //remove appointment from user firebase
                DatabaseReference databaseReference = database.getReference().child("users").
                        child(currentUserUid).child("appointments").child(appointmentViewModel.getDate()).
                        child(appointmentViewModel.getTime());
                databaseReference.removeValue();
                //remove appointment from barber firebase
                DatabaseReference databaseReference1 = database.getReference().child("barbers").
                        child(appointmentViewModel.getUid()).child("appointments").child(appointmentViewModel.getDate()).
                        child(appointmentViewModel.getTime());
                databaseReference1.removeValue();

                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                Toast.makeText(getContext(), "You have cancelled appointment", Toast.LENGTH_SHORT).show();

                //let barber know you cancelled appointment

            case R.id.floatingAction:
                /*upload data from appointmentViewModel into favorites node on firebase
                with same structure as recentAppointments, then in favorites fragment
                load just as you would recent appointment recycler view onto home fragment
                 */
                Log.i("FAB", "it runs");

                FirebaseDatabase.getInstance().getReference().child("barbers").child(appointmentViewModel.getUid())
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
