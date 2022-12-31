package com.sunny.osos_task1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

//-------------------------getting data from Api using volley library-------------------------------------------------------------------
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                "https://jsonplaceholder.typicode.com/users",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response)
                    {
//----------------------Response received from Api-------------------------------------------------
                        ArrayList<User> userArrayList = new ArrayList<>();
                        if(response != null){
                            for(int i=0; i<response.length();i++){
                                try {
                                    JSONObject userJsonObject = response.getJSONObject(i);
                                    JSONObject addressJsonObject = userJsonObject.getJSONObject("address");
                                    String latString = addressJsonObject.getJSONObject("geo").getString("lat");
                                    double lat = Double.parseDouble(latString);
                                    String lonString = addressJsonObject.getJSONObject("geo").getString("lng");
                                    double lon = Double.parseDouble(lonString);
                                    Geo geo = new Geo(lat,lon);
                                    String street = addressJsonObject.getString("street");
                                    String suite = addressJsonObject.getString("suite");
                                    String city = addressJsonObject.getString("city");
                                    String zipcode = addressJsonObject.getString("zipcode");
                                    Address address = new Address(street,suite,city,zipcode,geo);
                                    String name = userJsonObject.getString("name");
                                    String username = userJsonObject.getString("username");
                                    String email = userJsonObject.getString("email");

                                    User user = new User(name,username,email,address);
                                    userArrayList.add(user);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            Log.d("rocky", "onResponse: "+userArrayList);
//--------------------------Marking map with first location-----------------------------------
                            LatLng firstUserLoc = new LatLng(userArrayList.get(0).getAddress().getGeo().getLat(), userArrayList.get(0).getAddress().getGeo().getLon());
                            googleMap.addMarker(new MarkerOptions()
                                    .position(firstUserLoc)
                                    .title("Marker in "+userArrayList.get(0).getAddress().getCity()));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(firstUserLoc));
//--------------------------Initializing Recycler view with adapter----------------------
                            RecyclerView recyclerView = findViewById(R.id.recyclerview);

                            RecyclerAdapter recyclerAdapter = new RecyclerAdapter(userArrayList);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(recyclerAdapter);

                            View visibleChild = recyclerView.getChildAt(0);
                            int firstChild = recyclerView.getChildAdapterPosition(visibleChild);
                            visibleChild = recyclerView.getChildAt(recyclerView.getChildCount()-1);
                            int lastChild = recyclerView.getChildAdapterPosition(visibleChild);

                            Log.d("rocky", "First visible child is: "+firstChild);
                            Log.d("rocky", "last visible child is: "+lastChild);
//--------------------------Listening for scroll change in recycler view------------------------------------
                            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                    super.onScrollStateChanged(recyclerView, newState);
                                    View visibleChild = recyclerView.getChildAt(0);
                                    int firstChild = recyclerView.getChildAdapterPosition(visibleChild);
                                    visibleChild = recyclerView.getChildAt(recyclerView.getChildCount()-1);
                                    int lastChild = recyclerView.getChildAdapterPosition(visibleChild);

                                    Log.d("rocky", "First visible child is: "+firstChild);
                                    Log.d("rocky", "last visible child is: "+lastChild);
//------------------------------Updating location on map according to the item on recycler view------------------
                                    googleMap.clear();
                                    LatLng firstUserLoc = new LatLng(userArrayList.get(lastChild).getAddress().getGeo().getLat(), userArrayList.get(lastChild).getAddress().getGeo().getLon());
                                    Toast.makeText(getApplicationContext(), "lat: "+userArrayList.get(lastChild).getAddress().getGeo().getLat()+" lon: "+userArrayList.get(lastChild).getAddress().getGeo().getLon(),Toast.LENGTH_SHORT).show();
                                    googleMap.addMarker(new MarkerOptions()
                                            .position(firstUserLoc)
                                            .title("Marker in "+userArrayList.get(lastChild).getAddress().getCity()));
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(firstUserLoc));


                                }

                                @Override
                                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                    super.onScrolled(recyclerView, dx, dy);

                                }
                            });


                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                    }
                });
        requestQueue.add(jsonArrayRequest);


    }
}