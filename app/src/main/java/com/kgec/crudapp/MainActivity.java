package com.kgec.crudapp;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    //private FloatingActionButton fab_btn;
    private com.github.clans.fab.FloatingActionButton fab_btn;

    //private EditText et_name,et_email,et_address,et_phone;

    private DatabaseReference reference;
    private ProgressDialog loadingbar;
    private RecyclerView recyclerView;
    private DataAdapter dataAdapter;
    private List<Data>list;
    private TextView textView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView=findViewById(R.id.internet_check);
//        textView.setVisibility(View.GONE);

        mToolbar=findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Manage Employees");

        //swipeRefreshLayout=findViewById(R.id.swipeRefreshLayout);
        reference= FirebaseDatabase.getInstance().getReference().child("Employee Details");





        fab_btn=findViewById(R.id.fab1);
        loadingbar=new ProgressDialog(this);

        list=new ArrayList<>();
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                swipeRefreshLayout.setRefreshing(false);
//                RearrangeItems();
//            }
//        });
        list.clear();
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                swipeRefreshLayout.setRefreshing(false);
//
//            }
//        });

        fab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                LayoutInflater layoutInflater=LayoutInflater.from(MainActivity.this);
                View view1=layoutInflater.inflate(R.layout.custominput,null);
                builder.setView(view1);

               final AlertDialog dialog=builder.create();



               EditText et_email=view1.findViewById(R.id.etd_email);
                EditText et_phone=view1.findViewById(R.id.etd_phone);
              EditText  et_address=view1.findViewById(R.id.etd_address);
                EditText et_name=view1.findViewById(R.id.etd_name);
                Button btn1=view1.findViewById(R.id.btn_save);


                btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String name=et_name.getText().toString() ;
                        String phone=et_phone.getText().toString() ;
                        String address=et_address.getText().toString() ;
                        String email=et_email.getText().toString() ;


                        if (TextUtils.isEmpty(name)){
                            Toast.makeText(MainActivity.this, "Please Enter Name. . .", Toast.LENGTH_SHORT).show();
                        }
                        else if (TextUtils.isEmpty(phone)){
                            Toast.makeText(MainActivity.this, "Please Enter Phone No. . .", Toast.LENGTH_SHORT).show();
                        }
                        else if (phone.length()!=10){
                            Toast.makeText(MainActivity.this, "Phone Number must be 10 digits. . .", Toast.LENGTH_SHORT).show();
                        }
                        else if (TextUtils.isEmpty(address)){
                            Toast.makeText(MainActivity.this, "Please Enter Address. . .", Toast.LENGTH_SHORT).show();
                        }
                        else if (TextUtils.isEmpty(email)){
                            Toast.makeText(MainActivity.this, "Please Enter Email. . .", Toast.LENGTH_SHORT).show();
                        }
                        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                            Toast.makeText(MainActivity.this, "Please Enter Valid Email. . .", Toast.LENGTH_SHORT).show();
                        }
                        else {

                            loadingbar.setMessage("Adding Data. . . . ");
                            loadingbar.show();
                            AddData(name,email,address,phone);

                            dialog.dismiss();



                        }
                        list.clear();
                    }


                });

            dialog.show();

            }




        });

        recyclerView=findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        showData();






    }

    private void showData() {
        //textView.setVisibility(View.GONE);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    list.clear();

                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        Data data=snapshot.getValue(Data.class);
                        list.add(data);
                    }
                    dataAdapter=new DataAdapter(MainActivity.this,list);
                    dataAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(dataAdapter);
                   // list.clear();

                }
                else {
                    Toast.makeText(MainActivity.this, "No Data is here", Toast.LENGTH_SHORT).show();
                    Log.e("No Data", "No data" );
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
       // dataAdapter.notifyDataSetChanged();

    }

    private void AddData(String name,String email,String address,String phone){
        String uid=reference.push().getKey();

        HashMap<String,Object>map=new HashMap<>();

        map.put("Name",name);
        map.put("Email",email);
        map.put("address",address);
        map.put("phone",phone);
        map.put("uid",uid);


        reference.child(uid).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Data Added Successfully. . .", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                    //list.clear();
                    Log.e("here done", "Here done" );

                }
                else {
                    String message=task.getException().getMessage();
                    Toast.makeText(MainActivity.this, "Failed. . .   "+message, Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

       isConnected(this);


    }


        public boolean isConnected(Context context) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())) {
                return true;
            } else {
                showDialog();
                return false;
            }
        }
    private void showDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Connect to wifi or quit")
                .setCancelable(true)
                .setPositiveButton("Connect to WIFI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
//    private void showDialog()
//    {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("Please Turn On Internet");
//
//        AlertDialog alert = builder.create();
//        alert.show();
//    }
}