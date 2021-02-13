package e.l2040.truecuts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


public class AppointmentWithCustomerFragment extends Fragment implements BarberUpcomingAppointmentAdapter.OnRecyclerListener{

    RecyclerView recyclerView;
    BarberUpcomingAppointmentAdapter barberUpcomingAppointmentAdapter;
    List<UpcomingAppointment> requestedAppointmentList;

    List<CustomerAppointment> customerAppointmentList;

    String currentUserUid;

    android.widget.Toolbar toolbar;
    private int i;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_appointment_with_customer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        i = 0;

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Requested Appointments");
        toolbar.setTitleTextAppearance(getContext(), R.style.ToolbarTextAppearance);
        toolbar.setNavigationIcon(R.drawable.navigation_menu_icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawerLayout);
                drawerLayout.openDrawer(Gravity.LEFT);
            }});

        requestedAppointmentList = new ArrayList<>();
        customerAppointmentList = new ArrayList<>();

        recyclerView = (RecyclerView) getView().findViewById(R.id.requestedAppointmentRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        barberUpcomingAppointmentAdapter = new BarberUpcomingAppointmentAdapter(getContext(), requestedAppointmentList, AppointmentWithCustomerFragment.this);
        recyclerView.setAdapter(barberUpcomingAppointmentAdapter);

        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("barbers").child(currentUserUid).child("appointmentsRequested").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    requestedAppointmentList.clear();
                    customerAppointmentList.clear();

                    //check if a date exists, might crash if there is no child under appointmentsRequested
                    for (DataSnapshot snapshotDate : dataSnapshot.getChildren()) {
                        CustomerAppointment customerAppointment;
                        for (DataSnapshot snapshotTime : snapshotDate.getChildren()) {
                            customerAppointment = snapshotTime.getValue(CustomerAppointment.class);



                            customerAppointmentList.add(customerAppointment);
                        }
                    }
                    getUris();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUris() {
        if (i < customerAppointmentList.size()){

            FirebaseDatabase.getInstance().getReference().child("users").child(customerAppointmentList.get(i)
                    .getUid()).child("images").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        String profileImageString = dataSnapshot.child("ProfileImage").getValue(String.class);
                        Uri profileImageUri = Uri.parse(profileImageString);

                        requestedAppointmentList.add(
                                new UpcomingAppointment(
                                        customerAppointmentList.get(i).getName(),
                                        customerAppointmentList.get(i).getTime().get(0),
                                        customerAppointmentList.get(i).getDate(),
                                        profileImageUri));

                        i += 1;
                        getUris();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            barberUpcomingAppointmentAdapter.notifyDataSetChanged();
            i = 0;
        }
    }


    @Override
    public void onRecyclerClick(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Accept or deny requested appointment.");
        builder.setCancelable(true);

        builder.setPositiveButton(
                        "Accept",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                //requestedAppointmentList.remove(position);
                               // barberUpcomingAppointmentAdapter.notifyDataSetChanged();

                                CustomerAppointment customerAppointment = customerAppointmentList.get(position);

                                //add appointment to barber appointments in firebase
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference databaseReference = database.getReference().child("barbers").
                                        child(currentUserUid).child("appointments").child(customerAppointment.getDate()).
                                        child(customerAppointment.getTime().get(0));
                                databaseReference.setValue(customerAppointment);


                                //add appointment to user appointments in firebase
                                FirebaseDatabase.getInstance().getReference().child("barbers").child(currentUserUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        CustomerAppointment customerAppointment = customerAppointmentList.get(position);

                                        AppointmentWithTheBarber appointmentWithTheBarber = new AppointmentWithTheBarber(
                                                currentUserUid, dataSnapshot.child("username").getValue().toString(), customerAppointment.getDate(),
                                                customerAppointment.getTime().get(0), dataSnapshot.child("phoneNumber").getValue().toString(),
                                                dataSnapshot.child("barberShopName").getValue().toString(), dataSnapshot.child("address").getValue().toString());

                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference databaseReference = database.getReference().child("users")
                                                .child(customerAppointment.getUid()).child("appointments")
                                                .child(customerAppointment.getDate()).child(customerAppointment.getTime().get(0));
                                        databaseReference.setValue(appointmentWithTheBarber);


                                        //remove appointment from appointmentsRequested in firebase
                                        DatabaseReference databaseReference1 = database.getReference().child("barbers").
                                                child(currentUserUid).child("appointmentsRequested").child(customerAppointment.getDate()).
                                                child(customerAppointment.getTime().get(0));
                                        databaseReference1.removeValue();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        });

        builder.setNegativeButton(
                        "Deny",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                requestedAppointmentList.remove(position);
                                barberUpcomingAppointmentAdapter.notifyDataSetChanged();

                                CustomerAppointment customerAppointment = customerAppointmentList.get(position);

                                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference databaseReference1 = database.getReference().child("barbers").
                                        child(currentUserUid).child("appointmentsRequested").child(customerAppointment.getDate()).
                                        child(customerAppointment.getTime().get(0));
                                databaseReference1.removeValue();

                 //************************ dont forget to let user know his requested appointment was denied

                            }
                        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
