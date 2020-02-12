package com.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import dataTypes.Vehicle;

public class Vehicles extends Fragment {
    ListView vehicleList;
    FloatingActionButton addVehicle;
    ArrayList<dataTypes.Vehicle> vehicles;
    ProgressDialog progressDialog;
    SwipeRefreshLayout swipeRefresh;
    TextView nothing;

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("My Vehicles");

        nothing=(TextView) view.findViewById(R.id.nothing);
        nothing.setVisibility(View.INVISIBLE);

        vehicleList = (ListView) view.findViewById(R.id.vehicleList);
        addVehicle=(FloatingActionButton) view.findViewById(R.id.fab);

        vehicles = new ArrayList<Vehicle>();
        progressDialog =new ProgressDialog(getActivity(),ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setCanceledOnTouchOutside(false);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        swipeRefresh.setRefreshing(true);
                        initList();
                        swipeRefresh.setRefreshing(false);
                    }
                }
        );
        progressDialog.setMessage("Loading..");
        progressDialog.show();
        this.initList();
        progressDialog.dismiss();
        addVehicle.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(),AddVehicle.class);
                        startActivity(intent);

                    }
                }
        );

        registerForContextMenu(vehicleList);

        vehicleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(),VehiclePanel.class);
                intent.putExtra("vehicleId",vehicles.get(i).id);
                intent.putExtra("model",vehicles.get(i).model);
                intent.putExtra("regdnum",vehicles.get(i).regd);
                startActivity(intent);

            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_my_vehicles,container,false);
    }
    private void initList()
    {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference ref = db.collection("vehicles");
        Query query = ref.whereEqualTo("ownerId", FirebaseAuth.getInstance().getCurrentUser().getUid());



        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                vehicles.clear();

                if(task.isSuccessful())
                {

                    for(QueryDocumentSnapshot d : task.getResult())
                    {

                        vehicles.add(new Vehicle(d.getId(),d.getString("model"),d.getString("regdNum")));
//                        Toast.makeText(MyVehicles.this,"Su",Toast.LENGTH_SHORT);
                    }
                    vehicleList.setAdapter(new CustomAdapter(vehicles));
                    if(vehicles.isEmpty())
                    {
                        nothing.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        nothing.setVisibility(View.INVISIBLE);
                    }
                }
                else
                {
                    Toast.makeText(getActivity(),"Unable to load data",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        this.initList();
    }

    private class CustomAdapter extends BaseAdapter {

        ArrayList<Vehicle> vehicles;
        CustomAdapter(ArrayList<Vehicle> v)
        {
            vehicles=v;
        }


        public int getCount() {
            return vehicles.size();
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.customlistlayout,viewGroup,false);
            TextView header = (TextView) view.findViewById(R.id.vehicle);
            TextView desc = (TextView) view.findViewById(R.id.descText);
            header.setText(vehicles.get(i).model);
            desc.setText(vehicles.get(i).regd);

            return  view;

        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId()==R.id.vehicleList){
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if(item.getItemId()==R.id.edit)
        {
            Intent intent = new Intent(getActivity(),EditVehicle.class);
            intent.putExtra("documentId",vehicles.get(info.position).id);
            startActivity(intent);
            return  true;
        }
        else if (item.getItemId()==R.id.delete)
        {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            progressDialog.setMessage("Deleting..");
            progressDialog.show();
            CollectionReference ref = db.collection("vehicles");
            ref.document(vehicles.get(info.position).id).delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(),"Deleted Sucessfully",Toast.LENGTH_SHORT).show();
                            initList();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(),"Task failed",Toast.LENGTH_SHORT).show();
                        }
                    });

            return true;
        }
        else{
            return super.onContextItemSelected(item);
        }
    }
}
