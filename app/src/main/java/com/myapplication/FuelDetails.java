package com.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import dataTypes.Refuel;

public class FuelDetails extends AppCompatActivity {
    ListView listView;
    FloatingActionButton add;
    ArrayList<Refuel> refuel;
    ProgressDialog progressDialog;
    EditText startDate;
    EditText endDate;
    TextView nothing;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel_details);
        progressDialog=new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setCanceledOnTouchOutside(false);

        refuel = new ArrayList<Refuel>();
        listView= (ListView) findViewById(R.id.listView);
        add = (FloatingActionButton) findViewById(R.id.add);
        startDate=(EditText)findViewById(R.id.startDate);
        endDate=(EditText) findViewById(R.id.endDate);
        nothing=(TextView) findViewById(R.id.nothing);
        nothing.setVisibility(View.INVISIBLE);
        setTitle("Refuel List");

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FuelDetails.this,AddRefuel.class);
                intent.putExtra("vehicleId",getIntent().getStringExtra("vehicleId"));
                intent.putExtra("odometer",getIntent().getStringExtra("odometer"));
                startActivity(intent);
            }
        });

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateStartDate();
            }

        };

        final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateEndDate();
            }

        };
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(FuelDetails.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();



            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog datePickerDialog=new DatePickerDialog(FuelDetails.this, date1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();


            }
        });


    }
    private  void initList()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference ref = db.collection("refuel");
        Query query = ref.whereEqualTo("vehicleId", getIntent().getStringExtra("vehicleId"));



        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                refuel.clear();

                if(task.isSuccessful())
                {

                    for(QueryDocumentSnapshot d : task.getResult())
                    {
                        Log.e("check:",checkDateBetween(d.getString("date"))+"");
                        if (checkDateBetween(d.getString("date")))
                        {
                            refuel.add(new Refuel(d.getId(),d.getString("date"),d.getString("totalAmount")));
                        }
//                      Toast.makeText(MyVehicles.this,"Su",Toast.LENGTH_SHORT);
                    }
                    listView.setAdapter(new CustomAdapter(refuel));
                    if(refuel.isEmpty())
                    {
                        nothing.setVisibility(View.VISIBLE);
                    }
                    else {
                        nothing.setVisibility(View.INVISIBLE);
                    }
                }
                else
                {
                    Toast.makeText(FuelDetails.this,"Unable to load data",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private  boolean checkDateBetween(String cd)
    {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        try{
        Date date1= sdf.parse(startDate.getText().toString());
        Date date2= sdf.parse(endDate.getText().toString());
        Date date= sdf.parse(cd);
        if((date.after(date1)|| date.equals(date1)) && (date.before(date2) || date.equals(date2)))
        {
            return true;
        }
        }
        catch (Exception e)
        {
//            Toast.makeText(FuelDetails.this,"Unable to parse",Toast.LENGTH_SHORT).show();

        }
        return false;
    }

    private void updateStartDate() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        startDate.setText(sdf.format(myCalendar.getTime()));
        if(!endDate.getText().toString().equals("dd/mm/yyyy") && !startDate.getText().toString().equals("dd/mm/yyyy"))
        {
            initList();
        }
    }

    private void updateEndDate() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        endDate.setText(sdf.format(myCalendar.getTime()));
        if(!endDate.getText().toString().equals("dd/mm/yyyy") && !startDate.getText().toString().equals("dd/mm/yyyy"))
        {
            initList();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initList();
    }

    private class CustomAdapter extends BaseAdapter {

        ArrayList<Refuel> services;
        CustomAdapter(ArrayList<Refuel> v)
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
            desc.setText("Rs. "+services.get(i).totalAmount);

            return  view;

        }
    }

}
