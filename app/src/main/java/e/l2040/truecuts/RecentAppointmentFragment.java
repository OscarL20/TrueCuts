/*
package e.l2040.truecuts;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class RecentAppointmentFragment extends Fragment implements View.OnClickListener{

    private MyViewModel model;
    Button back;
    Button messages;
    Button bookAppointment;

    RecyclerView recyclerView;
    PhotoAdapter photoAdapter;
    List<Photo> photoList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recent_appointment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        model = ViewModelProviders.of(getActivity()).get(MyViewModel.class);

        back = view.findViewById(R.id.back);
        back.setOnClickListener(this);

        messages = view.findViewById(R.id.messages);
        messages.setOnClickListener(this);

        bookAppointment = view.findViewById(R.id.bookAppointment);
        bookAppointment.setOnClickListener(this);


        photoList = new ArrayList<>();

        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        photoList.add(
                new Photo(R.drawable.barbershopoutside));
        photoList.add(
                new Photo(R.drawable.barbershopoutside));
        photoList.add(
                new Photo(R.drawable.barbershopoutside));
        photoList.add(
                new Photo(R.drawable.barbershopoutside));
        photoList.add(
                new Photo(R.drawable.barbershopoutside));



        photoAdapter = new PhotoAdapter(getContext(), photoList);
        recyclerView.setAdapter(photoAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView titleBarberShopName = getView().findViewById(R.id.titleBarberShopName);
        titleBarberShopName.setText(model.getBarberShop());

        TextView barberName = getView().findViewById(R.id.theBarber);
        barberName.setText(model.getBarber());

        TextView address = getView().findViewById(R.id.theAddress);
        address.setText(model.getAddress());

        TextView barberShopName = getView().findViewById(R.id.barberShopName);
        barberShopName.setText(model.getBarberShop());

        TextView phone = getView().findViewById(R.id.thePhone);
        phone.setText(model.getPhone());

        Spinner spinner = getView().findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), R.layout.support_simple_spinner_dropdown_item, model.getHours());

        spinner.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new SearchFragment()).commit();
                break;

            case R.id.messages:
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MessagesFragment()).commit();
                break;

            case R.id.bookAppointment:
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new BookAppointmentFragment()).commit();
                break;

        }
    }
}*/
