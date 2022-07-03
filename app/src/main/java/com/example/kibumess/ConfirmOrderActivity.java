package com.example.kibumess;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.rey.material.widget.Button;
import com.rey.material.widget.EditText;
import com.rey.material.widget.TextView;

public class ConfirmOrderActivity extends AppCompatActivity {

    private Button confirm;
    private TextView TotalAmt;
    private EditText PhoneNumber;
    private String totalAmount="";
    private  String productID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cornfirm_order);
        confirm=findViewById(R.id.confirm_button);
       TotalAmt=findViewById(R.id.amount);
        PhoneNumber=findViewById(R.id.phonenumber);

           totalAmount=getIntent().getStringExtra("Total Price");
        TotalAmt.setText(totalAmount+"/=");

    }

}