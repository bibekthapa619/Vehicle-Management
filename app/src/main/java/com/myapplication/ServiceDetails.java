package com.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import dataTypes.Servicing;

public class ServiceDetails extends AppCompatActivity {

    ListView listView;
    FloatingActionButton add;
    ArrayList<Servicing> services;
    ProgressDialog progressDialog;
    Spinner spinYear;
    TextView nothing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);
        services= new ArrayList<Servicing>();
        listView= (ListView) findViewById(R.id.listView);
        add = (FloatingActionButton) findViewById(R.id.add);
        progressDialog=new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setCanceledOnTouchOutside(false);
        setTitle("Services List");
        registerForContextMenu(listView);
        spinYear= (Spinner) findViewById(R.id.spinYear);
        nothing=(TextView) findViewById(R.id.nothing);
        nothing.setVisibility(View.INVISIBLE);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ServiceDetails.this,AddService.class);
                intent.putExtra("vehicleId",getIntent().getStringExtra("vehicleId"));
                intent.putExtra("odometer",getIntent().getStringExtra("odometer"));
                intent.putExtra("model",getIntent().getStringExtra("model"));
                startActivity(intent);
            }
        });
        initYear();
        spinYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                initList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ServiceDetails.this,ViewService.class);
                intent.putExtra("serviceId",services.get(i).id);
                intent.putExtra("vehicleId",getIntent().getStringExtra("vehicleId"));
                startActivity(intent);

            }
        });
    }
    private void initYear()
    {
        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 2017; i <= thisYear; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, years);


        spinYear.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initList();
    }

    private  void initList()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference ref = db.collection("service");
        Query query = ref.whereEqualTo("vehicleId", getIntent().getStringExtra("vehicleId"));

        progressDialog.setMessage("Loading..");
        progressDialog.show();

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                progressDialog.dismiss();
                services.clear();

                if(task.isSuccessful())
                {

                    for(QueryDocumentSnapshot d : task.getResult())
                    {
                        if(checkYear(d.getString("date")))
                        {
                            services.add( new Servicing(d.getId(),d.getString("date"),d.getString("serviceType")));
                        }
                    }
                    listView.setAdapter(new CustomAdapter(services));
                    if(services.isEmpty())
                    {
                        nothing.setVisibility(View.VISIBLE);
                    }
                    else {
                        nothing.setVisibility(View.INVISIBLE);
                    }
                }
                else
                {
                    Toast.makeText(ServiceDetails.this,"Unable to load data",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private  boolean checkYear(String cd)
    {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        try{

            Date date= sdf.parse(cd);
            Date date2 = sdf.parse("01/01/"+spinYear.getSelectedItem().toString());
            if(date.getYear()==date2.getYear())
            {
                return true;
            }
        }
        catch (Exception e)
        {
//            Toast.makeText(ServiceDetails.this,"Unable to parse",Toast.LENGTH_SHORT).show();

        }
        return false;
    }

    private class CustomAdapter extends BaseAdapter {

        ArrayList<Servicing> services;
        CustomAdapter(ArrayList<Servicing> v)
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
            view = getLayoutInflater().inflate(R.layout.customlistlayout,viewGroup,false);
            TextView header = (TextView) view.findViewById(R.id.vehicle);
            TextView desc = (TextView) view.findViewById(R.id.descText);
            header.setText(services.get(i).date);
            desc.setText(services.get(i).serviceType);

            return  view;

        }
    }
}
