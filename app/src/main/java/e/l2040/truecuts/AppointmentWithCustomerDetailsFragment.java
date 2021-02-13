package e.l2040.truecuts;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class AppointmentWithCustomerDetailsFragment extends Fragment implements View.OnClickListener{

    private AppointmentWithCustomerViewModel appointmentWithCustomerViewModel;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_appointment_with_customer_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appointmentWithCustomerViewModel = ViewModelProviders.of(getActivity()).get(AppointmentWithCustomerViewModel.class);

        TextView name = view.findViewById(R.id.name);
        name.setText(appointmentWithCustomerViewModel.getName());
        TextView date = view.findViewById(R.id.date);
        date.setText(appointmentWithCustomerViewModel.getDate());
        TextView time = view.findViewById(R.id.time);
        time.setText(appointmentWithCustomerViewModel.getTime().get(0));


        Button back = view.findViewById(R.id.back);
        back.setOnClickListener(this);

        Button cancelAppointment = view.findViewById(R.id.cancelAppointment);
        cancelAppointment.setOnClickListener(this);

        Button messages = view.findViewById(R.id.messages);
        messages.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new BarberHomeFragment()).commit();
                break;

            case R.id.cancelAppointment:
                String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                //remove appointment from barber firebase
                DatabaseReference databaseReference = database.getReference().child("barbers").
                        child(currentUserUid).child("appointments").child(appointmentWithCustomerViewModel.getDate()).
                        child(appointmentWithCustomerViewModel.getTime().get(0));
                databaseReference.removeValue();
                //remove appointment from user firebase
                DatabaseReference databaseReference1 = database.getReference().child("users").
                        child(appointmentWithCustomerViewModel.getUid()).child("appointments").child(appointmentWithCustomerViewModel.getDate()).
                        child(appointmentWithCustomerViewModel.getTime().get(0));
                databaseReference1.removeValue();

                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new BarberHomeFragment()).commit();
                Toast.makeText(getContext(), "You have cancelled appointment", Toast.LENGTH_SHORT).show();

                //let customer know you cancelled appointment
                break;

            case R.id.messages:
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MessagesFragment()).commit();
                break;
        }
    }
}
