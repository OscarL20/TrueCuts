package e.l2040.truecuts;

import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.Size;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MyResultReceiver{

    private MyViewModel model;
    private AppointmentWithCustomerViewModel appointmentWithCustomerViewModel;
    private ChatViewModel chatViewModel;
    User user;
    BottomNavigationView bottomNavigationView;

    TextView navUsername;
    TextView navShopName;

    boolean isBarber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        NavigationView navigationView = (NavigationView) findViewById(R.id.drawerNavigation);
        navigationView.setNavigationItemSelectedListener(this);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottomNavigationView.getMenu().clear();

        model = ViewModelProviders.of(this).get(MyViewModel.class);
        appointmentWithCustomerViewModel = ViewModelProviders.of(this).get(AppointmentWithCustomerViewModel.class);
        chatViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);

        Intent intent = getIntent();
        isBarber = intent.getBooleanExtra("isBarber", false);

        if (isBarber){
            DatabaseReference profileImageRefBarber = FirebaseDatabase.getInstance().getReference().child("barbers")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("images").child("ProfileImage");


            View navHeaderBarber = navigationView.inflateHeaderView(R.layout.nav_header);
            navigationView.inflateMenu(R.menu.drawermenu);
            navUsername = (TextView) navHeaderBarber.findViewById(R.id.navName);
            navShopName = (TextView) navHeaderBarber.findViewById(R.id.navShopName);
            navUsername.setText("wasup");
            navShopName.setText("wasup");

            final ImageView headerViewImage = navHeaderBarber.findViewById(R.id.headerViewImage);

            profileImageRefBarber.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        Picasso.with(getApplicationContext()).load(dataSnapshot.getValue(String.class)).resize(100,100).centerCrop().into(headerViewImage);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            bottomNavigationView.inflateMenu(R.menu.barber_bottom_navigation);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BarberHomeFragment()).commit();
        }
        else{
            DatabaseReference profileImageRefCustomer = FirebaseDatabase.getInstance().getReference().child("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("images").child("ProfileImage");

            View navHeaderCustomer = navigationView.inflateHeaderView(R.layout.nav_header);
            navigationView.inflateMenu(R.menu.drawermenu_customer);
            navUsername = (TextView) navHeaderCustomer.findViewById(R.id.navName);
            navShopName = (TextView) navHeaderCustomer.findViewById(R.id.navShopName);
            navUsername.setText("wasup");
            navShopName.setText("wasup");


            final ImageView headerViewImage = navHeaderCustomer.findViewById(R.id.headerViewImage);

            profileImageRefCustomer.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        Picasso.with(getApplicationContext()).load(dataSnapshot.getValue(String.class)).resize(100,100).centerCrop().into(headerViewImage);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });


            bottomNavigationView.inflateMenu(R.menu.bottom_navigation);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;

            if (!isBarber) {

                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        selectedFragment = new HomeFragment();
                        break;

                    case R.id.nav_search:
                        selectedFragment = new SearchFragment();

                        break;

                    case R.id.nav_message:
                        selectedFragment = new ChatRoomsFragment();

                        break;

                    case R.id.nav_heart:
                        selectedFragment = new FavoritesFragment();
                        break;
                }
            }else{
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        selectedFragment = new BarberHomeFragment();
                        break;

                    case R.id.nav_appointment:
                        selectedFragment = new AppointmentWithCustomerFragment();
                        break;

                    case R.id.nav_message:
                        selectedFragment = new ChatRoomsFragment();
                        break;
                }

            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

            return true;
        }
    };


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.changeShop: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChangeBarberShopFragment()).commit();
                break;
            }

            case R.id.profile: {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BarberEditProfile()).commit();
                break;
            }

            case R.id.changeImage:{
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SelectingImageCustomer()).commit();
                break;
            }

            case R.id.inviteFriends:{
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    String shareMessage= "\nLet me recommend you this application\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
                break;
            }

            case R.id.signOut:{
                Intent myIntent = new Intent(Home.this, Login.class);
                startActivity(myIntent);
                FirebaseAuth.getInstance().signOut();
            }

        }
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public Boolean getResult() {
        return isBarber;
    }
}
