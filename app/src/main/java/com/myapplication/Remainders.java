package com.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;

import dataTypes.DashBoard;
import dataTypes.MyNotification;
import dataTypes.Remainder;
import dataTypes.Servicing;
import dataTypes.Vehicle;

public class Remainders extends Fragment {


    ListView listView;
    FloatingActionButton add;
    ArrayList<Remainder> remainders;
    ProgressDialog progressDialog;
    TextView nothing;

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Reminders");

        remainders = new ArrayList<Remainder>();
        listView = (ListView) view.findViewById(R.id.listView);
        add = (FloatingActionButton) view.findViewById(R.id.add);
        progressDialog = new ProgressDialog(getContext(), AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setCanceledOnTouchOutside(false);
        nothing=(TextView) view.findViewById(R.id.nothing);
        nothing.setVisibility(View.INVISIBLE);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddRemainder.class);

                startActivity(intent);
            }
        });
        initList();

    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.remainder, container, false);
    }

    private void initList() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final CollectionReference ref = db.collection("remainders");
        Query query = ref;


        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                remainders.clear();

                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot d : task.getResult()) {

                        remainders.add(new Remainder(d.getString("date"), d.getString("title")));
//                        Toast.makeText(MyVehicles.this,"Su",Toast.LENGTH_SHORT);
                    }
                    listView.setAdapter(new CustomAdapter(remainders));
                    if (remainders.isEmpty())
                    {
                        nothing.setVisibility(View.VISIBLE);
                    }
                    else {
                        nothing.setVisibility(View.INVISIBLE);
                    }
                } else {
                    Toast.makeText(getActivity(), "Unable to load data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class CustomAdapter extends BaseAdapter {

        ArrayList<Remainder> vehicles;

        CustomAdapter(ArrayList<Remainder> v) {
            vehicles = v;
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
            view = getLayoutInflater().inflate(R.layout.customlistlayout, viewGroup, false);
            TextView header = (TextView) view.findViewById(R.id.vehicle);
            TextView desc = (TextView) view.findViewById(R.id.descText);
            header.setText(vehicles.get(i).title);
            desc.setText(vehicles.get(i).date);

            return view;

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        initList();
    }
}
