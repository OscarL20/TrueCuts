package e.l2040.truecuts;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChangeBarberShopFragment extends Fragment implements ChangeBarberShopAdapter.OnRecyclerListener{

    android.widget.Toolbar toolbar;

    Button back;

    String lat;
    String lng;

    String zipcode;

    RecyclerView recyclerView;
    ChangeBarberShopAdapter changeBarberShopAdapter;
    List<BarberShop> searchResultsList;
    List<String> placeId;


    FirebaseUser currentFirebaseUser;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    Map<String, Object> updates;


    @Override
    public void onRecyclerClick(int position) {
        DownloadTask downloadTask2 = new DownloadTask();
        downloadTask2.execute("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeId.get(position) + "&fields=formatted_phone_number,opening_hours,name&key=AIzaSyDfymk8ZpGnSuKTs76t5PAGrhvcsGIDYEU");

       /* currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("barbers").child(currentFirebaseUser.getUid());
        updates = new HashMap<String,Object>();*/

        updates.put("address", searchResultsList.get(position).getAddress());
        Log.i("put", searchResultsList.get(position).getAddress());

        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new SelectImages()).addToBackStack(null).commit();
        //Toast.makeText(this.getContext(), "Barber shop added to profile", Toast.LENGTH_SHORT).show();
    }


    public class DownloadTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            String[] urlAndData = new String[2];

            try{

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int incomingJson = reader.read();

                while (incomingJson != -1) {
                    char current = (char) incomingJson;
                    result += current;
                    incomingJson = reader.read();
                }

                urlAndData[0] = urls[0];
                urlAndData[1] = result;

                return urlAndData;

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }


        @Override
        protected void onPostExecute(String[] s) {
            super.onPostExecute(s);
            //longInfo(s);
            //String lat = "";
            //String lng = "";

            if (s[0].equals("https://maps.googleapis.com/maps/api/geocode/json?address=" + zipcode + "&key=AIzaSyDfymk8ZpGnSuKTs76t5PAGrhvcsGIDYEU")){

                try {
                    JSONObject jsonObject = new JSONObject(s[1]);
                    String results = jsonObject.getString("results");
                    JSONArray jsonArray = new JSONArray(results);


                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject areaInfo = jsonArray.getJSONObject(i);
                        JSONObject jsonObject1 = areaInfo.getJSONObject("geometry");
                        JSONObject jsonObject2 = jsonObject1.getJSONObject("location");
                        lat = jsonObject2.getString("lat");
                        lng = jsonObject2.getString("lng");
                    }

                    passOnLatAndLng(lat, lng);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else if (s[0].equals("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lng + "&radius=5000&type=barber&keyword=barber&key=AIzaSyDfymk8ZpGnSuKTs76t5PAGrhvcsGIDYEU")){

                try {
                    JSONObject jsonObject = new JSONObject(s[1]);
                    String barbersNearBy = jsonObject.getString("results");
                    JSONArray jsonArray = new JSONArray(barbersNearBy);


                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject barber = jsonArray.getJSONObject(i);

                        searchResultsList.add(
                                new BarberShop(
                                        barber.getString("name"),
                                        barber.getString("vicinity")));

                        changeBarberShopAdapter.notifyDataSetChanged();

                        placeId.add(barber.getString("place_id"));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                try {
                    JSONObject jsonObject = new JSONObject(s[1]);
                    String result = jsonObject.getString("result");
                    JSONObject jsonObject1 = new JSONObject(result);
                    String phoneNumber = jsonObject1.getString("formatted_phone_number");
                    String name = jsonObject1.getString("name");
                    try{
                            String openingHours = jsonObject1.getString("opening_hours");
                            JSONObject jsonObject2 = new JSONObject(openingHours);
                            String weekdayText = jsonObject2.getString("weekday_text");
                            JSONArray jsonArray = new JSONArray(weekdayText);
                            List<String> weekDayHrs = new ArrayList<String>();
                            for (int i = 0; i < jsonArray.length(); i++){
                                weekDayHrs.add(jsonArray.getString(i));
                            }
                            updates.put("barberShopHrs", weekDayHrs);
                            updates.put("barberShopName", name);
                            updates.put("phoneNumber", phoneNumber);
                            updates.put("zipcode", zipcode);

                        databaseReference.updateChildren(updates);
                    }catch (JSONException e){
                        e.printStackTrace();
                        //Toast.makeText(getContext(), "Did not upload to profile", Toast.LENGTH_SHORT).show();
                        Log.i("openingHoursWork", e.getLocalizedMessage());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    public void passOnLatAndLng(String lat, String lng){
        DownloadTask downloadTask1 = new DownloadTask();
        downloadTask1.execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lng + "&radius=5000&type=barber&keyword=barber&key=AIzaSyDfymk8ZpGnSuKTs76t5PAGrhvcsGIDYEU");
    }



    public void showList(String zipcode){
        searchResultsList.clear();
        changeBarberShopAdapter.notifyDataSetChanged();
        placeId.clear();

        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute("https://maps.googleapis.com/maps/api/geocode/json?address=" + zipcode + "&key=AIzaSyDfymk8ZpGnSuKTs76t5PAGrhvcsGIDYEU");
        //Intent intent = new Intent (this, Login.class);
        //startActivity(intent);
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_barber_shop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Change Barber Shop");
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
                hideSoftKeyboard(getActivity());

                //call search method, pass search string
                showList(s);
                zipcode = s;
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


        String lat = "";
        String lng = "";



        searchResultsList = new ArrayList<>();
        placeId = new ArrayList<>();

        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        changeBarberShopAdapter = new ChangeBarberShopAdapter(getContext(), searchResultsList, this);
        recyclerView.setAdapter(changeBarberShopAdapter);


        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("barbers").child(currentFirebaseUser.getUid());
        updates = new HashMap<String,Object>();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    public static void hideSoftKeyboard(Activity activity) {
        if (activity.getCurrentFocus() == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }



}
