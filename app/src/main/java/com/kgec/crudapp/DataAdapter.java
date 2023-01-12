package com.kgec.crudapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    public Context mContext;
    public List<Data>list;

    public DataAdapter(Context mContext, List<Data> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view=  LayoutInflater.from(mContext).inflate(R.layout.new_layout,parent,false);
        return new DataAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Data item=list.get(position);

        holder.name.setText(item.getName());
        holder.email.setText(item.getEmail());
        holder.address.setText(item.getAddress());
        holder.phone.setText(item.getPhone());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("Clicked", "Clicked. . ." );
                AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
                LayoutInflater layoutInflater=LayoutInflater.from(mContext);
                View view1=layoutInflater.inflate(R.layout.updateanddelete,null);
                builder.setView(view1);

                final AlertDialog dialog=builder.create();
                EditText et_email=view1.findViewById(R.id.etd_update_email);
                EditText et_phone=view1.findViewById(R.id.etd_update_phone);
                EditText  et_address=view1.findViewById(R.id.etd_update_address);
                EditText et_name=view1.findViewById(R.id.etd_update_name);
                Button btn_update=view1.findViewById(R.id.btn_update);
                Button btn_delete=view1.findViewById(R.id.btn_delete);

                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Employee Details").child(item.getUid());
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){
                            String USERNAME=dataSnapshot.child("Name").getValue().toString();
                            String EMAIL=dataSnapshot.child("Email").getValue().toString();
                            String PHONE=dataSnapshot.child("phone").getValue().toString();
                            String ADDRESS=dataSnapshot.child("address").getValue().toString();

                            et_email.setText(EMAIL);
                            et_phone.setText(PHONE);
                            et_address.setText(ADDRESS);
                            et_name.setText(USERNAME);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                btn_update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String name=et_name.getText().toString();
                        String phone=et_phone.getText().toString();
                        String address=et_address.getText().toString();
                        String email=et_email.getText().toString();

                        if (TextUtils.isEmpty(name)){
                            Toast.makeText(mContext, "Please Provide name", Toast.LENGTH_SHORT).show();
                        }
                        else if(TextUtils.isEmpty(phone)){
                            Toast.makeText(mContext, "Please provide phone number", Toast.LENGTH_SHORT).show();
                        }
                        else if (phone.length()!=10){
                            Toast.makeText(mContext, "Phone Number must be 10 digits. . .", Toast.LENGTH_SHORT).show();
                        }
                        else if(TextUtils.isEmpty(address)){
                            Toast.makeText(mContext, "Please provide Address", Toast.LENGTH_SHORT).show();
                        }
                        else if(TextUtils.isEmpty(email)){
                            Toast.makeText(mContext, "Please provide email", Toast.LENGTH_SHORT).show();
                        }
                        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                            Toast.makeText(mContext, "Please Enter Valid Email. . .", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            updateTask(name,email,phone,address,databaseReference);
                            dialog.dismiss();
                        }

//                        list.clear();

                    }
                });

                btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder builder1=new AlertDialog.Builder(mContext);
                        builder1.setTitle("Are you want to delete?");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Employee Details");
                                reference.child(item.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(mContext, "Data deleted. . .", Toast.LENGTH_SHORT).show();
                                            Log.e("Done", "Deleted" );
                                            dialog.dismiss();
                                            notifyDataSetChanged();
                                            //list.clear();
                                        }
                                        else {
                                            String e =task.getException().getMessage();
                                            Toast.makeText(mContext, "Failed. ..   "+e, Toast.LENGTH_SHORT).show();
                                            Log.e("Failed ", "Failed. . .  "+e );
                                            notifyDataSetChanged();
                                        }

                                    }
                                });
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialog.cancel();
                            }
                        });

                        builder1.show();
                        list.clear();

                    }
                });
                dialog.show();

            }
        });

    }

    private void updateTask(String name, String email, String phone, String address, DatabaseReference databaseReference) {
        HashMap<String,Object>map=new HashMap<>();
        map.put("Name",name);
        map.put("Email",email);
        map.put("phone",phone);
        map.put("address",address);

//        databaseReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()){
//                    Log.e("Update Successful", "Update Successful" );
//                    Toast.makeText(mContext, "Update Successfully. . .", Toast.LENGTH_SHORT).show();
//                    list.clear();
//                    notifyDataSetChanged();
//                }
//
//                else {
//
//                    String e=task.getException().getMessage();
//                    Log.e("Update Failed. . .", "Failed. . .   "+e );
//                    Toast.makeText(mContext, "Failed. . .  "+e, Toast.LENGTH_SHORT).show();
//
//                }
//          //  list.clear();
//
//            }
//
//        });

        databaseReference.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                if (task.isSuccessful()){
                    Log.e("Update Successful", "Update Successful" );
                    Toast.makeText(mContext, "Update Successfully. . .", Toast.LENGTH_SHORT).show();
//                    list.clear();
                    notifyDataSetChanged();
                Log.e("size", String.valueOf(list.size()) );
//                }

//                else {
//
//                    String e=task.getException().getMessage();
//                    Log.e("Update Failed. . .", "Failed. . .   "+e );
//                    Toast.makeText(mContext, "Failed. . .  "+e, Toast.LENGTH_SHORT).show();
//
//                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,email,phone,address;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.show_name);
            email=itemView.findViewById(R.id.show_email);
            phone=itemView.findViewById(R.id.show_phone);
            address=itemView.findViewById(R.id.show_address);
        }
    }

}
