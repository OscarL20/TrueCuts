package e.l2040.truecuts;

import android.app.Activity;
import android.app.MediaRouteButton;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class HomeFragment extends Fragment implements RecentAppointmentAdapter.OnRecyclerListener, UpcomingAppointmentAdapter.OnHorizontalRecyclerListener {

    private AppointmentViewModel appointmentViewModel;
    private MyViewModel myViewModel;

    View coordinatorLayout;
    View constraintLayoutTwo;

    Toolbar toolbar;

    TextView noUpcomingAppointments;
    TextView noRecentAppointments;

    RecyclerView recyclerView;
    RecentAppointmentAdapter recentAppointmentAdapter;
    List<RecentAppointment> recentAppointmentList;

    RecyclerView recyclerView1;
    UpcomingAppointmentAdapter upcomingAppointmentAdapter;
    List<UpcomingAppointment> upcomingAppointmentList;

    List<AppointmentWithTheBarber> appointmentWithTheBarberList;
    List<AppointmentWithTheBarber> appointmentWithTheBarberList1;

    List<List<Uri>> listOfLists;
    List<List<Uri>> recentListOfLists;

    List<Uri> listProfileImages;
    List<Uri> recentListProfileImages;

    List<Uri> listBarberShopImages;
    List<Uri> recentListBarberShopImages;

    private int i;
    private int counter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        i = 0;
        counter = 0;

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        toolbar.setTitleTextAppearance(getContext(), R.style.ToolbarTextAppearance);
        toolbar.setNavigationIcon(R.drawable.navigation_menu_icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawerLayout);
                drawerLayout.openDrawer(Gravity.LEFT);
            }});

        noUpcomingAppointments = (TextView) view.findViewById(R.id.noUpcomingAppointment);
        noRecentAppointments = (TextView) view.findViewById(R.id.noRecentAppointments);


        constraintLayoutTwo = getActivity().findViewById(R.id.constraintLayoutTwo);
        coordinatorLayout = getActivity().findViewById(R.id.coordinatorLayout);

        appointmentViewModel = ViewModelProviders.of(getActivity()).get(AppointmentViewModel.class);
        myViewModel = ViewModelProviders.of(getActivity()).get(MyViewModel.class);

        listBarberShopImages = new ArrayList<>();
        recentListBarberShopImages = new ArrayList<>();

        listProfileImages = new ArrayList<>();
        recentListProfileImages = new ArrayList<>();

        listOfLists = new ArrayList<>();
        recentListOfLists = new ArrayList<>();

        recentAppointmentList = new ArrayList<>();

        recyclerView = (RecyclerView) getView().findViewById(R.id.recentRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        upcomingAppointmentList = new ArrayList<>();

        recyclerView1 = (RecyclerView) getView().findViewById(R.id.upcomingRecyclerView);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        appointmentWithTheBarberList = new ArrayList<>();
        appointmentWithTheBarberList1 = new ArrayList<>();

        upcomingAppointmentAdapter = new UpcomingAppointmentAdapter(getContext(), upcomingAppointmentList, this);
        recyclerView1.setAdapter(upcomingAppointmentAdapter);

        recentAppointmentAdapter = new RecentAppointmentAdapter(getContext(), recentAppointmentList, this);
        recyclerView.setAdapter(recentAppointmentAdapter);


        final String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(currentUserUid).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    String userName = dataSnapshot.getValue(String.class);
                    ((Home) getActivity()).navUsername.setText(userName);
                    ((Home) getActivity()).navShopName.setText("");
                }




                FirebaseDatabase.getInstance().getReference().child("users").child(currentUserUid).child("appointments").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            noUpcomingAppointments.setAlpha(0);

                            upcomingAppointmentList.clear();
                            appointmentWithTheBarberList.clear();

                            listOfLists.clear();
                            recentListOfLists.clear();

                            listProfileImages.clear();
                            recentListProfileImages.clear();

                            listBarberShopImages.clear();
                            recentListBarberShopImages.clear();

                            //check if a date exists, might crash if there is no child under appointmentsRequested
                            for (DataSnapshot snapshotDate : dataSnapshot.getChildren()) {
                                AppointmentWithTheBarber appointmentWithTheBarber;

                                for (DataSnapshot snapshotTime : snapshotDate.getChildren()) {
                                    appointmentWithTheBarber = snapshotTime.getValue(AppointmentWithTheBarber.class);

                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M-d-yyyy h:mm a");
                                    LocalDateTime time = LocalDateTime.parse(snapshotDate.getKey() + " " + snapshotTime.getKey(), formatter);
                                    LocalDateTime cTime = LocalDateTime.now();
                                    Log.i("testingDateTime", snapshotDate.getKey() + " " + snapshotTime.getKey());

                                    if (cTime.isAfter(time)){
                                        //add time to recent appointment firebase
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference databaseReference = database.getReference().child("users")
                                                .child(currentUserUid).child("recentAppointments").child(appointmentWithTheBarber.getDate()).child(appointmentWithTheBarber.getTime());
                                        databaseReference.setValue(appointmentWithTheBarber);

                                        //remove that time from appointments in firebase
                                        snapshotTime.getRef().removeValue();

                                    }
                                    else{
                                        appointmentWithTheBarberList.add(appointmentWithTheBarber);
                                    }
                                }
                            }
                            getUris();
                        }else{
                            noUpcomingAppointments.setAlpha(1);
                        }

                        //Populate recent appointments recycler view

                        FirebaseDatabase.getInstance().getReference().child("users").child(currentUserUid).child("recentAppointments").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    noRecentAppointments.setAlpha(0);
                                    recentAppointmentList.clear();
                                    appointmentWithTheBarberList1.clear();
                                    for (DataSnapshot snapshotDate : dataSnapshot.getChildren()) {
                                        AppointmentWithTheBarber recentAppointment;

                                        for (DataSnapshot snapshotTime : snapshotDate.getChildren()) {
                                            recentAppointment = snapshotTime.getValue(AppointmentWithTheBarber.class);


                                            appointmentWithTheBarberList1.add(recentAppointment);
                                        }
                                    }
                                    getUrisRecent();
                                }else{
                                    constraintLayoutTwo.setVisibility(View.INVISIBLE);
                                    coordinatorLayout.setVisibility(View.VISIBLE);

                                    noRecentAppointments.setAlpha(1);
                                }

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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }



    private void getUrisRecent() {
        if(counter < appointmentWithTheBarberList1.size()){

            FirebaseDatabase.getInstance().getReference().child("barbers").child(appointmentWithTheBarberList1.get(counter).getBarberId())
                    .child("images").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){

                        //retrieving previous work images**************************
                        List<Uri> listPreviousWork = new ArrayList<>();

                        for (DataSnapshot childSnapshot: dataSnapshot.child("PreviousWorkImages").getChildren()) {
                            String previousWorkImageString = childSnapshot.getValue(String.class);
                            Uri previousWorkImageUri = Uri.parse(previousWorkImageString);
                            listPreviousWork.add(previousWorkImageUri);
                        }
                        recentListOfLists.add(listPreviousWork);


                        //retrieving profile image**********************************
                        String profileImageString = dataSnapshot.child("ProfileImage").getValue(String.class);
                        Uri profileImageUri = Uri.parse(profileImageString);
                        recentListProfileImages.add(profileImageUri);


                        //retrieving barbershop image*******************************
                        String barberShopImageString = dataSnapshot.child("BarberShopImage").getValue(String.class);
                        Uri barberShopImageUri = Uri.parse(barberShopImageString);
                        recentListBarberShopImages.add(barberShopImageUri);

                        recentAppointmentList.add(
                                new RecentAppointment(
                                        appointmentWithTheBarberList1.get(counter).getBarberShopName(),
                                        appointmentWithTheBarberList1.get(counter).getBarberName(),
                                        appointmentWithTheBarberList1.get(counter).getAddress(),
                                        barberShopImageUri));

                        counter += 1;
                        getUrisRecent();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }else{
            recentAppointmentAdapter.notifyDataSetChanged();
            counter = 0;


            constraintLayoutTwo.setVisibility(View.INVISIBLE);
            coordinatorLayout.setVisibility(View.VISIBLE);
        }

    }


    private void getUris() {
        if (i < appointmentWithTheBarberList.size()){

            FirebaseDatabase.getInstance().getReference().child("barbers").child(appointmentWithTheBarberList.get(i)
                    .getBarberId()).child("images").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){

                        //retrieving previous work images**************************
                        List<Uri> listPreviousWork = new ArrayList<>();

                        for (DataSnapshot childSnapshot: dataSnapshot.child("PreviousWorkImages").getChildren()) {
                            String previousWorkImageString = childSnapshot.getValue(String.class);
                            Uri previousWorkImageUri = Uri.parse(previousWorkImageString);
                            listPreviousWork.add(previousWorkImageUri);
                        }
                        listOfLists.add(listPreviousWork);

                        //retrieving profile image**********************************
                        String profileImageString = dataSnapshot.child("ProfileImage").getValue(String.class);
                        Uri profileImageUri = Uri.parse(profileImageString);
                        listProfileImages.add(profileImageUri);

                        //retrieving barbershop image*******************************
                        String barberShopImageString = dataSnapshot.child("BarberShopImage").getValue(String.class);
                        Uri barberShopImageUri = Uri.parse(barberShopImageString);
                        listBarberShopImages.add(barberShopImageUri);

                        upcomingAppointmentList.add(
                                new UpcomingAppointment(
                                        appointmentWithTheBarberList.get(i).getBarberShopName(),
                                        appointmentWithTheBarberList.get(i).getTime(),
                                        appointmentWithTheBarberList.get(i).getDate(),
                                        barberShopImageUri));

                        i += 1;
                        getUris();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            upcomingAppointmentAdapter.notifyDataSetChanged();
            i = 0;
        }
    }







    @Override
    public void onRecyclerClick(final int position) {
        AppointmentWithTheBarber recentAppointment = appointmentWithTheBarberList1.get(position);

        FirebaseDatabase.getInstance().getReference().child("barbers").child(recentAppointment.getBarberId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Barber barber = dataSnapshot.getValue(Barber.class);
                        myViewModel.setAddress(barber.getAddress());
                        myViewModel.setBarber(barber.getUsername());
                        myViewModel.setBarberShop(barber.getBarberShopName());
                        myViewModel.setHours(barber.getBarberShopHrs());
                        myViewModel.setPhone(barber.getPhoneNumber());
                        myViewModel.setUid(barber.getUid());
                        //set uri for profile image and uri's for previous work images and barbershop image
                        myViewModel.setBarberProfileImage(recentListProfileImages.get(position));
                        myViewModel.setListOfUris(recentListOfLists.get(position));
                        myViewModel.setBarberShopImage(recentListBarberShopImages.get(position));


                        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new BarberDetailsFragment()).addToBackStack(null).commit();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }


    @Override
    public void onHorizontalRecyclerClick(int position) {

        AppointmentWithTheBarber appointmentWithTheBarber = appointmentWithTheBarberList.get(position);
        appointmentViewModel.setBarberShopName(appointmentWithTheBarber.getBarberShopName());
        appointmentViewModel.setBarberName(appointmentWithTheBarber.getBarberName());
        appointmentViewModel.setAddress(appointmentWithTheBarber.getAddress());
        appointmentViewModel.setDate(appointmentWithTheBarber.getDate());
        appointmentViewModel.setTime(appointmentWithTheBarber.getTime());
        appointmentViewModel.setPhone(appointmentWithTheBarber.getPhone());
        appointmentViewModel.setUid(appointmentWithTheBarber.getBarberId());
        //set uri for profile image and uri's for previous work images and barbershop image
        appointmentViewModel.setBarberProfileImage(listProfileImages.get(position));
        appointmentViewModel.setListOfUris(listOfLists.get(position));
        appointmentViewModel.setBarberShopImage(listBarberShopImages.get(position));

        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new AppointmentDetailsFragment()).addToBackStack(null).commit();
    }

}
