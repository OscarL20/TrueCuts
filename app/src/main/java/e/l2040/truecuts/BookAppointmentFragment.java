package e.l2040.truecuts;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;
import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class BookAppointmentFragment extends Fragment implements View.OnClickListener, TimeSlotAdapter.OnHorizontalRecyclerListener{

    private MyViewModel model;

    Button back;
    Button favorites;
    Button bookNow;

    CalendarView calendarView;

    RecyclerView recyclerView;
    TimeSlotAdapter timeSlotAdapter;

    List<String> timeSlots;

    List<String> unavailableTimes;

    List<TimeSlot> timeSlotList;

    ArrayList<String> time;

    DateTimeFormatter dtf;

    String date;

    String currentUserUid;
    String username;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_appointment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(currentUserUid).child("username")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            username = dataSnapshot.getValue().toString();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        timeSlots = new ArrayList<>();

        timeSlotList = new ArrayList<>();

        unavailableTimes = new ArrayList<>();

        time = new ArrayList<>();

        dtf = new DateTimeFormatterBuilder().appendPattern("h:mm a").toFormatter();

        time.clear();

        model = ViewModelProviders.of(getActivity()).get(MyViewModel.class);

        bookNow = view.findViewById(R.id.bookNow);
        bookNow.setOnClickListener(this);

        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        calendarView = view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, final int i1, final int i2) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(i, i1, i2);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                List<String> shopHours = new ArrayList<>();
                shopHours = model.getHours();
                int dayToCheck = 0;

                time.clear();

                switch (dayOfWeek){
                    case 1:
                        dayToCheck = 6;
                        break;
                    case 2:
                        dayToCheck = 0;
                        break;
                    case 3:
                        dayToCheck = 1;
                        break;
                    case 4:
                        dayToCheck = 2;
                        break;
                    case 5:
                        dayToCheck = 3;
                        break;
                    case 6:
                        dayToCheck = 4;
                        break;
                    case 7:
                        dayToCheck = 5;
                        break;
                }

                if (shopHours.get(dayToCheck).contains("Closed")){
                    Log.i("is barber closed", "barber is closed");
                }
                else{
                    Log.i("is barber closed", "barber is open");

                    date = (i1 + 1) + "-" + i2 + "-" + i;

                    final List<String> finalShopHours = shopHours;
                    final int finalDayToCheck = dayToCheck;

                    //check start and end hrs for that day and make a time slot picker with 15min. intervals

                    int startOfString = finalShopHours.get(finalDayToCheck).indexOf(" ") + 1;
                    String startAndEndTime = finalShopHours.get(finalDayToCheck).substring(startOfString);
                    String[] arrayStartandEndTime = startAndEndTime.split(" – ");

                    String startTime = arrayStartandEndTime[0];
                    String endTime = arrayStartandEndTime[1];

                    LocalTime startLocalTime = LocalTime.parse(startTime, dtf);
                    LocalTime endLocalTime = LocalTime.parse(endTime, dtf);
                    // Log.i("testingtesting", dtf.format(startLocalTime));

                    timeSlots.clear();
                    timeSlotList.clear();

                    while (startLocalTime.isBefore(endLocalTime.plusMinutes(15))){
                        timeSlots.add(dtf.format(startLocalTime));
                        startLocalTime = startLocalTime.plusMinutes(15);
                    }


                    FirebaseDatabase.getInstance().getReference().child("barbers").child(model.getUid()).child("appointments")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        unavailableTimes.clear();

                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            if (snapshot.getKey().equals(date)){
                                                CustomerAppointment customerAppointment;
                                                for (DataSnapshot snapshot1: snapshot.getChildren()){
                                                    customerAppointment = snapshot1.getValue(CustomerAppointment.class);
                                                    unavailableTimes.addAll(customerAppointment.getTime());
                                                    //Log.i("checkingunavailabletimes", unavailableTimes.toString());
                                                }
                                                timeSlots.removeAll(unavailableTimes);
                                                unavailableTimes.clear();
//**********************************************check entire array for times to remove********************************************************************
                                            if (timeSlots.size() >= 3){

                                                for (int counter = 0; counter < timeSlots.size() - 2; counter++){
                                                    LocalTime aTime = LocalTime.parse(timeSlots.get(counter), dtf);
                                                    String aTimePlusFifteen = dtf.format(aTime.plusMinutes(15));
                                                    String aTimePlusThirty = dtf.format(aTime.plusMinutes(30));

                                                    if (!(timeSlots.get(counter + 1).equals(aTimePlusFifteen)) || !(timeSlots.get(counter + 2).equals(aTimePlusThirty))){
                                                        unavailableTimes.add(timeSlots.get(counter));
                                                    }

                                                }
                                                unavailableTimes.add(timeSlots.get(timeSlots.size() - 1));
                                                unavailableTimes.add(timeSlots.get(timeSlots.size() - 2));
                                                timeSlots.removeAll(unavailableTimes);
                                            }
                                            }
                                        }
                                    }

                                    //inner onDataChange goes here
                                    FirebaseDatabase.getInstance().getReference().child("barbers").child(model.getUid()).child("appointmentsRequested")
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            unavailableTimes.clear();

                                            if (dataSnapshot.exists()) {
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    if (snapshot.getKey().equals(date)) {
                                                        CustomerAppointment customerAppointment;
                                                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                            customerAppointment = snapshot1.getValue(CustomerAppointment.class);
                                                            unavailableTimes.addAll(customerAppointment.getTime());
                                                            //Log.i("checkingunavailabletimes", unavailableTimes.toString());
                                                        }
                                                        timeSlots.removeAll(unavailableTimes);
                                                        unavailableTimes.clear();
//**********************************************check entire array for times to remove********************************************************************
                                                        if (timeSlots.size() >= 3) {

                                                            for (int counter = 0; counter < timeSlots.size() - 2; counter++) {
                                                                LocalTime aTime = LocalTime.parse(timeSlots.get(counter), dtf);
                                                                String aTimePlusFifteen = dtf.format(aTime.plusMinutes(15));
                                                                String aTimePlusThirty = dtf.format(aTime.plusMinutes(30));

                                                                if (!(timeSlots.get(counter + 1).equals(aTimePlusFifteen)) || !(timeSlots.get(counter + 2).equals(aTimePlusThirty))) {
                                                                    unavailableTimes.add(timeSlots.get(counter));
                                                                }

                                                            }
                                                            unavailableTimes.add(timeSlots.get(timeSlots.size() - 1));
                                                            unavailableTimes.add(timeSlots.get(timeSlots.size() - 2));
                                                            timeSlots.removeAll(unavailableTimes);
                                                        }
                                                    }
                                                }
                                            }
                                            for (String time: timeSlots){
                                                timeSlotList.add(
                                                        new TimeSlot(
                                                                time));
                                            }

                                            timeSlotAdapter = new TimeSlotAdapter(getContext(), timeSlotList, BookAppointmentFragment.this);
                                            recyclerView.setAdapter(timeSlotAdapter);

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                }
            }
        });
    }


    String getMonthForInt(int num) {
        String month = "";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new BarberDetailsFragment()).commit();
                break;

            case R.id.bookNow:
                if (!time.isEmpty()){
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

                    CustomerAppointment customerAppointment = new CustomerAppointment(currentUserUid, username, date, time);
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = database.getReference().child("barbers")
                            .child(model.getUid()).child("appointmentsRequested").child(date).child(time.get(0));
                    databaseReference.setValue(customerAppointment);

                    Toast.makeText(getContext(), "You've requested an appointment, waiting for barber to accept", Toast.LENGTH_LONG).show();
                    break;
                }
                else {
                    Toast.makeText(getContext(), "Select a time", Toast.LENGTH_SHORT).show();
                    break;
                }
        }

    }

    @Override
    public void onHorizontalRecyclerClick(int position) {



        time.clear();
        time.add(timeSlots.get(position));
        LocalTime selectedTime = LocalTime.parse(timeSlots.get(position), dtf);
        selectedTime = selectedTime.plusMinutes(15);
        time.add(dtf.format(selectedTime));
        selectedTime = selectedTime.plusMinutes(15);
        time.add(dtf.format(selectedTime));
    }
}
