package com.example.telim2.gmv1.fragments;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.telim2.gmv1.Constants;
import com.example.telim2.gmv1.Utils.DatabaseHelper;
import com.example.telim2.gmv1.Utils.GPSTracker;
import com.example.telim2.gmv1.Utils.Model;
import com.example.telim2.gmv1.Utils.MyAdapter;
import com.example.telim2.gmv1.Utils.MySingleTon;
import com.example.telim2.gmv1.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by galqayeva on 21.08.2017.
 */

public class Fragment2 extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private GPSTracker gpsTracker;
    private Location mLocation;
    double latitude, longitude;
    String url;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<Model> modelList;
    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    DatabaseHelper myDB;
    int load;



    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment2,container,false);


        myDB = new DatabaseHelper(getActivity());

        gpsTracker = new GPSTracker(getActivity());
        mLocation = gpsTracker.getLocation();

        latitude = mLocation.getLatitude();
        longitude = mLocation.getLongitude();

        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swipeRefreshLayout) ;
        recyclerView=(RecyclerView)view.findViewById(R.id.recycleview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        modelList=new ArrayList<>();

        url="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+Double.toString(latitude)+","+Double.toString(longitude)+"&radius=500&type=restaurant&key=AIzaSyC3_ndLS93DsNFqSB-78VuA00A0hrI8B5A";

        swipeRefreshLayout.setOnRefreshListener(this);
        loadListview();


             loadRestaurants();



        return view;

    }



    public void loadRestaurants(){

        Cursor data = myDB.getAlldata();
        if(data.getCount() == 0){

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Getting Restaurants");
            progressDialog.show();
            StringRequest stringRequest=new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jsonObject=new JSONObject(response);
                                JSONArray jsonArray=jsonObject.getJSONArray("results");

                                for (int i=0;i<jsonArray.length();i++) {

                                    JSONObject jsonobject=jsonArray.getJSONObject(i);
                                    String lat = jsonobject.getJSONObject("geometry").getJSONObject("location").getString("lat");
                                    String lan = jsonobject.getJSONObject("geometry").getJSONObject("location").getString("lng");
                                    String location=jsonobject.getString("name");

                                    boolean insertData = myDB.addData(location,lan,lat);
                                    if(!insertData==true)
                                        Log.d("something","getwrong");

                                }
                                modelList.clear();
                                loadListview();
                                progressDialog.dismiss();


                            } catch (JSONException e) {
                                Toast.makeText(getActivity(), "Something wrong with json" ,Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            //Toast.makeText(getActivity(), "Check Your Internet Connection",Toast.LENGTH_LONG).show();
                            Log.d("erroor","k"+ error.getMessage());
                        }
                    }

            );
            MySingleTon.getInstance(getActivity()).addToRequestQueue(stringRequest);



            if(swipeRefreshLayout.isRefreshing()){
                swipeRefreshLayout.setRefreshing(false);
            }
        }

    }


    public void loadListview(){


        Cursor data = myDB.getAlldata();
        if(data.getCount() != 0){

            Log.d("salus","----------"+data.getCount());

            while(data.moveToNext()){

                Model item=new Model(data.getString(3),data.getString(2),data.getString(1));
                modelList.add(item);

            }

            adapter=new MyAdapter(modelList,getActivity());
            recyclerView.setAdapter(adapter);
        }
    }


    @Override
    public void onRefresh() {
        load=0;
        myDB.deleteAll();
        loadRestaurants();

    }
}
