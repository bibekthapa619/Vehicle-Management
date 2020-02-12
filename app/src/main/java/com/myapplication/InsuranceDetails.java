package com.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import dataTypes.Insurance;

public class InsuranceDetails extends AppCompatActivity {
    ListView listView;
    FloatingActionButton add;
    ArrayList<Insurance> insurance;
    ProgressDialog progressDialog;
    TextView nothing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance_details);
        progressDialog=new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setCanceledOnTouchOutside(false);
        nothing=(TextView) findViewById(R.id.nothing);
        nothing.setVisibility(View.INVISIBLE);

        insurance = new ArrayList<Insurance>();

        listView= (ListView) findViewById(R.id.listView);
        add = (FloatingActionButton) findViewById(R.id.add);
        setTitle("Insurance List");


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InsuranceDetails.this,AddInsurance.class);
                intent.putExtra("vehicleId",getIntent().getStringExtra("vehicleId"));
                intent.putExtra("model",getIntent().getStringExtra("model"));
                startActivity(intent);
            }
        });
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        this.initList();
        progressDialog.dismiss();

        final SwipeRefreshLayout swipeRefresh;
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        initList();
    }

    private  void initList()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference ref = db.collection("insurance");
        Query query = ref.whereEqualTo("vehicleId", getIntent().getStringExtra("vehicleId"));


        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                insurance.clear();

                if(task.isSuccessful())
                {

                    for(QueryDocumentSnapshot d : task.getResult())
                    {

                        insurance.add(new Insurance(d.getId(),d.getString("issuedDate"),d.getString("expiryDate"),d.getString("insuranceType")));
//                        Toast.makeText(MyVehicles.this,"Su",Toast.LENGTH_SHORT);
                    }
                    listView.setAdapter(new CustomAdapter(insurance));
                    if(insurance.isEmpty())
                    {
                        nothing.setVisibility(View.VISIBLE);
                    }
                    else{
                        nothing.setVisibility(View.INVISIBLE);
                    }
                }
                else
                {
                    Toast.makeText(InsuranceDetails.this,"Unable to load data",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private class CustomAdapter extends BaseAdapter {

        ArrayList<Insurance> services;
        CustomAdapter(ArrayList<Insurance> v)
        {
            services=v;
        }


        public int getCount() {
            return services.size();
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
            view = getLayoutInflater().inflate(R.layout.servicing_list,viewGroup,false);
            TextView header1 = (TextView) view.findViewById(R.id.vehicle);
            TextView header2 = (TextView) view.findViewById(R.id.headerText2);

            TextView desc = (TextView) view.findViewById(R.id.descText);
            header1.setText(services.get(i).issuedDate);
            header2.setText(services.get(i).expiryDate);
            desc.setText(services.get(i).insuranceType);

            return  view;

        }

    }
}
