package com.example.kibumess;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button CreateAccountButton;
    private EditText InputName,InputPhoneNumber,Inputpassword;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        CreateAccountButton= findViewById(R.id.register_btn);
        InputName= findViewById(R.id.register_username_input);
        InputPhoneNumber= findViewById(R.id.register_phone_number_input);
        Inputpassword= findViewById(R.id.register_password_input);
        loadingBar=new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {

        String name=InputName.getText().toString();
        String phone=InputPhoneNumber.getText().toString();
        String password=Inputpassword.getText().toString();

        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "Please write your name", Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show();

        }

        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();

        }

        else
        {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait while we are checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidatePhoneNumber(name,phone,password);

        }
    }

    private void ValidatePhoneNumber(String name, String phone, String password) {

        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!(dataSnapshot.child("Users").child(phone).exists()))
                {
                    HashMap<String,Object>userDataMap=new HashMap<>();
                    userDataMap.put("phone",phone);
                    userDataMap.put("password",password);
                    userDataMap.put("name",name);

                    RootRef.child("Users").child(phone).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(RegisterActivity.this, "Congratulation Your Account has been created successfuly", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                    }

                                    else
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Network Error please try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
                else
                {
                    Toast.makeText(RegisterActivity.this, "This " + phone +"Already exist", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Please try using  another phone Number", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(intent);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}