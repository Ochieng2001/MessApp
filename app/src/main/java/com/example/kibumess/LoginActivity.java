package com.example.kibumess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kibumess.Model.Users;
import com.example.kibumess.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private Button LoginButton;
    private EditText InputPhoneNumber,Inputpassword;
    private ProgressDialog loadingBar;
    private  String parentDbName="Users";
    private CheckBox checkBoxRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton=(Button) findViewById(R.id.login_btn);
        InputPhoneNumber=(EditText) findViewById(R.id.login_phone_number_input);
        Inputpassword=(EditText) findViewById(R.id.login_password_input);
        loadingBar=new ProgressDialog(this);

        checkBoxRememberMe=findViewById(io.paperdb.R.id.checkbox);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });
    }

    private void LoginUser() {
        String phone=InputPhoneNumber.getText().toString();
        String password=Inputpassword.getText().toString();

         if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show();

        }

        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();

        }
        else
         {
             loadingBar.setTitle("Login Account");
             loadingBar.setMessage("Please wait while we are checking the credentials");
             loadingBar.setCanceledOnTouchOutside(false);
             loadingBar.show();

             AllowAccessToAccount(phone,password);
         }



    }

    private void AllowAccessToAccount(String phone, String password) {

        if(checkBoxRememberMe.isChecked())
        {
            Paper.book().write(Prevalent.UserPhonekey,phone);
            Paper.book().write(Prevalent.UserPasswordKey,password);
        }
        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(parentDbName).child(phone).exists())
                {
                    Users usersData=dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);

                    if(usersData.getPhone().equals(phone))
                    {
                        if(usersData.getPassword().equals(password))
                        {
                            Toast.makeText(LoginActivity.this, "Logged in Succesfully", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                            startActivity(intent);

                        }

                    }

                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Account with this" + phone + " does not exist", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "You need to  create your account", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}