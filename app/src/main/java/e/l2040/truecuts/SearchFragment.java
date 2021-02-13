package e.l2040.truecuts;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment implements View.OnClickListener, RecentAppointmentAdapter.OnRecyclerListener {

    private MyViewModel model;

    List<String> barberId;
    RecyclerView recyclerView;
    RecentAppointmentAdapter recentAppointmentAdapter;
    List<RecentAppointment> searchResultsList;

    android.widget.Toolbar toolbar;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Search");
        toolbar.setTitleTextAppearance(getContext(), R.style.ToolbarTextAppearance);
        toolbar.setNavigationIcon(R.drawable.navigation_menu_icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawerLayout);
                drawerLayout.openDrawer(Gravity.LEFT);
            }});


        android.widget.SearchView searchView = getView().findViewById(R.id.searchView);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //call search method, pass search string
                search(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });

       /* searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return true;
            }
        });*/


        model = ViewModelProviders.of(getActivity()).get(MyViewModel.class);

        barberId = new ArrayList<>();

        searchResultsList = new ArrayList<>();

        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        recentAppointmentAdapter = new RecentAppointmentAdapter(getContext(), searchResultsList, this);
        recyclerView.setAdapter(recentAppointmentAdapter);

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }



    public void search(final String zipcode) {

            barberId.clear();

            searchResultsList.clear();

        recentAppointmentAdapter.notifyDataSetChanged();

        FirebaseDatabase.getInstance().getReference().child("barbers")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Barber barber = snapshot.getValue(Barber.class);
                            if ((barber.getZipcode() != null) && (barber.getZipcode().equals(zipcode))){

                                String profileImageString = snapshot.child("images").child("ProfileImage").getValue(String.class);
                                Uri profileImageUri = Uri.parse(profileImageString);

                                searchResultsList.add(
                                        new RecentAppointment(
                                                barber.getBarberShopName(),
                                                barber.getUsername(),
                                                barber.getAddress(),
                                                profileImageUri));

                                barberId.add(barber.getUid());

                                recentAppointmentAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


    }


    @Override
    public void onRecyclerClick(int position) {

        FirebaseDatabase.getInstance().getReference().child("barbers").child(barberId.get(position))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Barber barber = dataSnapshot.getValue(Barber.class);

                            String profileImageString = dataSnapshot.child("images").child("ProfileImage").getValue(String.class);
                            Uri profileImageUri = Uri.parse(profileImageString);

                            String barberShopImageString = dataSnapshot.child("images").child("BarberShopImage").getValue(String.class);
                            Uri barberShopImageUri = Uri.parse(barberShopImageString);

                            List<Uri> listPreviousWork = new ArrayList<>();
                            for (DataSnapshot childSnapshot: dataSnapshot.child("images").child("PreviousWorkImages").getChildren()) {
                                String previousWorkImageString = childSnapshot.getValue(String.class);
                                Uri previousWorkImageUri = Uri.parse(previousWorkImageString);
                                listPreviousWork.add(previousWorkImageUri);
                            }

                            model.setAddress(barber.getAddress());
                            model.setBarber(barber.getUsername());
                            model.setBarberShop(barber.getBarberShopName());
                            model.setHours(barber.getBarberShopHrs());
                            model.setPhone(barber.getPhoneNumber());
                            model.setUid(barber.getUid());
                            model.setBarberProfileImage(profileImageUri);
                            model.setListOfUris(listPreviousWork);
                            model.setBarberShopImage(barberShopImageUri);

                            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new BarberDetailsFragment()).commit();

                        }else{

                            Toast.makeText(getContext(), "Something failed", Toast.LENGTH_SHORT).show();

                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.back:
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                break;

        }

    }
}
