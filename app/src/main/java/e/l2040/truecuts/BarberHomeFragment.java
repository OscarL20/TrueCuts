package e.l2040.truecuts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
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
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class BarberHomeFragment extends Fragment implements View.OnClickListener, BarberUpcomingAppointmentAdapter.OnRecyclerListener{

    private AppointmentWithCustomerViewModel appointmentWithCustomerViewModel;

    View constraintLayoutTwo;
    View coordinatorLayout;

    Button search;
    Button toggle;

    String currentUserUid;

    RecyclerView recyclerView;
    BarberUpcomingAppointmentAdapter barberUpcomingAppointmentAdapter;
    List<UpcomingAppointment> upcomingAppointmentList;

    List<CustomerAppointment> customerAppointmentList;

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
        return inflater.inflate(R.layout.fragment_barber_home, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        i = 0;

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        toolbar.setTitleTextAppearance(getContext(), R.style.ToolbarTextAppearance);
        toolbar.setNavigationIcon(R.drawable.navigation_menu_icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawerLayout);
                drawerLayout.openDrawer(Gravity.LEFT);
            }});

        constraintLayoutTwo = getActivity().findViewById(R.id.progressBar);

        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);

        appointmentWithCustomerViewModel = ViewModelProviders.of(getActivity()).get(AppointmentWithCustomerViewModel.class);


        upcomingAppointmentList = new ArrayList<>();
        customerAppointmentList = new ArrayList<>();

        recyclerView = (RecyclerView) getView().findViewById(R.id.upcomingRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        barberUpcomingAppointmentAdapter = new BarberUpcomingAppointmentAdapter(getContext(), upcomingAppointmentList, this);
        recyclerView.setAdapter(barberUpcomingAppointmentAdapter);

        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("barbers").child(currentUserUid).child("appointments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    upcomingAppointmentList.clear();
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

                constraintLayoutTwo.setVisibility(View.INVISIBLE);
                coordinatorLayout.setVisibility(View.VISIBLE);

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

                        upcomingAppointmentList.add(
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
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.message:
                Log.i("message","it works");
        }

    }

    @Override
    public void onRecyclerClick(int position) {

        DialogFragment newFragment = new AlertDialogDetails();
        newFragment.show(getFragmentManager(), "missles");


        CustomerAppointment customerAppointment = customerAppointmentList.get(position);
        appointmentWithCustomerViewModel.setUid(customerAppointment.getUid());
        appointmentWithCustomerViewModel.setName(customerAppointment.getName());
        appointmentWithCustomerViewModel.setDate(customerAppointment.getDate());
        appointmentWithCustomerViewModel.setTime(customerAppointment.getTime());

        //getFragmentManager().beginTransaction().replace(R.id.fragment_container, new AppointmentWithCustomerDetailsFragment()).commit();
    }

}
