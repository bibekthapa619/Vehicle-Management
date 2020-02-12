package com.myapplication;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import dataTypes.DashBoard;
import dataTypes.Vehicle;

import static com.myapplication.R.drawable.textviewshape;

public class DrawerHome extends Fragment {
    ListView listView;
    TextView lastFueled;
    TextView lastServiced;
    ProgressDialog progressDialog;
    ArrayList<DashBoard> dashBoard;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.drawer_home,container,false);
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Home");
        listView = (ListView) view.findViewById(R.id.listView);
        lastFueled= (TextView) view.findViewById(R.id.lastFueled);
        lastServiced = (TextView) view.findViewById(R.id.lastServiced);
        progressDialog =new ProgressDialog(getActivity(),ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.setCanceledOnTouchOutside(false);

        dashBoard=new ArrayList<DashBoard>();


        lastFueled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   fueledHandle();
            }
        });
        lastServiced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceHandle();
            }
        });

    }
    private class CustomAdapter extends BaseAdapter {

        ArrayList<DashBoard> vehicles;
        CustomAdapter(ArrayList<DashBoard> v)
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
            view = getLayoutInflater().inflate(R.layout.home_list,viewGroup,false);
            TextView header = (TextView) view.findViewById(R.id.vehicle);
            TextView desc = (TextView) view.findViewById(R.id.date);
            header.setText(vehicles.get(i).vehicle);
            desc.setText(vehicles.get(i).date);

            return  view;

        }
    }

    public void fueledHandle()
    {
        lastFueled.setTextColor(Color.parseColor("#0093d7"));
        lastServiced.setTextColor(Color.BLACK);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference ref = db.collection("vehicles");
        Query query = ref.whereEqualTo("ownerId", FirebaseAuth.getInstance().getCurrentUser().getUid());

        progressDialog.setMessage("Loading..");
        progressDialog.show();
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                dashBoard.clear();

                progressDialog.dismiss();
                if(task.isSuccessful())
                {
                    for(QueryDocumentSnapshot d : task.getResult())
                    {

                        dashBoard.add(new DashBoard(d.getId(),d.getString("model")+" : "+d.getString("regdNum"),d.getString("lastFueled")));
//                        Toast.makeText(MyVehicles.this,"Su",Toast.LENGTH_SHORT);
                    }

                }
                else
                {
                    Toast.makeText(getActivity(),"Unable to load data",Toast.LENGTH_SHORT).show();
                }
                listView.setAdapter(new CustomAdapter(dashBoard));
            }
        });
    }


    public void serviceHandle()
    {

        lastServiced.setTextColor(Color.parseColor("#0093d7"));
        lastFueled.setTextColor(Color.BLACK);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference ref = db.collection("vehicles");
        Query query = ref.whereEqualTo("ownerId", FirebaseAuth.getInstance().getCurrentUser().getUid());


        progressDialog.setMessage("Loading..");
        progressDialog.show();
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                dashBoard.clear();
                progressDialog.dismiss();
                if(task.isSuccessful())
                {

                    for(QueryDocumentSnapshot d : task.getResult())
                    {

                        dashBoard.add(new DashBoard(d.getId(),d.getString("model")+" : "+d.getString("regdNum"),d.getString("lastServiced")));
//                        Toast.makeText(MyVehicles.this,"Su",Toast.LENGTH_SHORT);
                    }

                }
                else
                {
                    Toast.makeText(getActivity(),"Unable to load data",Toast.LENGTH_SHORT).show();
                }
                listView.setAdapter(new CustomAdapter(dashBoard));
            }
        });
    }




}
