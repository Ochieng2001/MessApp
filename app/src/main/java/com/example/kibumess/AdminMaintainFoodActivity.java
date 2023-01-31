package com.example.kibumess;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class AdminMaintainFoodActivity extends AppCompatActivity {
    private Button applyChanges;
    private EditText name,price,description;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_food);
        applyChanges=findViewById(R.id.applychangesbtn);
        name=findViewById(R.id.product_name_maintain);
        price=findViewById(R.id.product_price_maintain);
        description=findViewById(R.id.product_description_maintain);
        imageView=findViewById(R.id.product_image_maintain);
    }
}