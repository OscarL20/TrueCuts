package e.l2040.truecuts;

import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FavoritesFragment extends Fragment implements RecentAppointmentAdapter.OnRecyclerListener {

    private MyViewModel myViewModel;

    android.widget.Toolbar toolbar;
    RecyclerView recyclerView;
    RecentAppointmentAdapter favoritesAdapter;
    List<RecentAppointment> favoritesList;
    ArrayList<Barber> favoritesList1;
    List<List<Uri>> listOfLists;
    List<Uri> listProfileImages;
    List<Uri> listBarberShopImages;

    TextView noFavoriteBarber;

    private int counter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        counter = 0;

        myViewModel = ViewModelProviders.of(getActivity()).get(MyViewModel.class);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Favorite Barbers");
        toolbar.setTitleTextAppearance(getContext(), R.style.ToolbarTextAppearance);
        toolbar.setNavigationIcon(R.drawable.navigation_menu_icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawerLayout);
                drawerLayout.openDrawer(Gravity.LEFT);
            }});

        noFavoriteBarber = (TextView) view.findViewById(R.id.noFavoriteBarber);

        favoritesList = new ArrayList<>();
        favoritesList1 = new ArrayList<>();
        listOfLists = new ArrayList<>();
        listProfileImages = new ArrayList<>();
        listBarberShopImages = new ArrayList<>();

        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        favoritesAdapter = new RecentAppointmentAdapter(getContext(), favoritesList, this);
        recyclerView.setAdapter(favoritesAdapter);



        final String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(currentUserUid).child("favorites").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot barberId : dataSnapshot.getChildren()) {
                        Barber favorites = barberId.getValue(Barber.class);

                            favoritesList1.add(favorites);
                    }
                    getUris();

                    noFavoriteBarber.setAlpha(0);
                }else{
                    noFavoriteBarber.setAlpha(1);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void getUris() {
        if(counter < favoritesList1.size()){

            FirebaseDatabase.getInstance().getReference().child("barbers").child(favoritesList1.get(counter).getUid())
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
                        listOfLists.add(listPreviousWork);


                        //retrieving profile image**********************************
                        String profileImageString = dataSnapshot.child("ProfileImage").getValue(String.class);
                        Uri profileImageUri = Uri.parse(profileImageString);
                        listProfileImages.add(profileImageUri);


                        //retrieving barbershop image*******************************
                        String barberShopImageString = dataSnapshot.child("BarberShopImage").getValue(String.class);
                        Uri barberShopImageUri = Uri.parse(barberShopImageString);
                        listBarberShopImages.add(barberShopImageUri);

                        favoritesList.add(
                                new RecentAppointment(
                                        favoritesList1.get(counter).getBarberShopName(),
                                        favoritesList1.get(counter).getUsername(),
                                        favoritesList1.get(counter).getAddress(),
                                        barberShopImageUri));

                        counter += 1;
                        getUris();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }else{
            favoritesAdapter.notifyDataSetChanged();
            counter = 0;
        }

    }

    @Override
    public void onRecyclerClick(int position) {
        Barber favoriteBarber = favoritesList1.get(position);

        myViewModel.setAddress(favoriteBarber.getAddress());
        myViewModel.setBarber(favoriteBarber.getUsername());
        myViewModel.setBarberShop(favoriteBarber.getBarberShopName());
        myViewModel.setHours(favoriteBarber.getBarberShopHrs());
        myViewModel.setPhone(favoriteBarber.getPhoneNumber());
        myViewModel.setUid(favoriteBarber.getUid());
        //set uri for profile image and uri's for previous work images and barbershop image
        myViewModel.setBarberProfileImage(listProfileImages.get(position));
        myViewModel.setListOfUris(listOfLists.get(position));
        myViewModel.setBarberShopImage(listBarberShopImages.get(position));

        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new BarberDetailsFragment()).addToBackStack(null).commit();
    }
}
