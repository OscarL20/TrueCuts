package e.l2040.truecuts;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AlertDialogDetails extends DialogFragment{

    private AppointmentWithCustomerViewModel appointmentWithCustomerViewModel;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.alert_dialog_details, null);
        builder.setView(v);

        appointmentWithCustomerViewModel = ViewModelProviders.of(getActivity()).get(AppointmentWithCustomerViewModel.class);

        TextView customerName = v.findViewById(R.id.customerNameduh);
        customerName.setText(appointmentWithCustomerViewModel.getName());
        TextView date = v.findViewById(R.id.date);
        date.setText(appointmentWithCustomerViewModel.getDate());
        TextView time = v.findViewById(R.id.time);
        time.setText(appointmentWithCustomerViewModel.getTime().get(0));

        //*********interferes with create chat logic******
        /*Button message = v.findViewById(R.id.messagenoww);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MessagesFragment()).commit();
            }
        });*/

        Button cancelAppointment = v.findViewById(R.id.cancelAppointment);
        cancelAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

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
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }

}
