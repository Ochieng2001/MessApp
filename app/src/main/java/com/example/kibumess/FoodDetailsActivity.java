package com.example.kibumess;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kibumess.Model.Food;
import com.example.kibumess.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.Button;
import com.rey.material.widget.ImageView;
import com.rey.material.widget.TextView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class FoodDetailsActivity extends AppCompatActivity {
    private ImageView productImage,add,sub;
    private TextView productPrice,prodductDescription,productName;
    private Button addTocartBtn;
    private  TextView numberBtn;
   // private FloatingActionButton addTocartBtn;
    private  String productID="";
    int minteger = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);
        productID=getIntent().getStringExtra("pid");

        addTocartBtn=findViewById(R.id.pd_add_to_cart_button);
        numberBtn=findViewById(R.id.number_btn);
        productImage=findViewById(R.id.product_image_details);
        prodductDescription=findViewById(R.id.product_description_details);
        productPrice=findViewById(R.id.product_price_details);
        productImage=findViewById(R.id.product_image_details);
        productName=findViewById(R.id.product_name_details);
        sub=findViewById(R.id.sub);


        add=findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseInteger();

            }
        });

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseinteger();
            }
        });

        addTocartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingToCartList();
            }
        });




        getProductDetails(productID);




    }

    private void addingToCartList() {
        String saveCurrentDate,saveCurrentTime;
        Calendar calForDate= Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MM dd,yyy");

        saveCurrentDate=currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentDate.format(calForDate.getTime());

       final DatabaseReference cartListRef=FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String,Object> cartMap=new HashMap<>();
        cartMap.put("pid",productID);
        cartMap.put("pname",productName.getText().toString());
        cartMap.put("price",productPrice.getText().toString());
        cartMap.put("date",saveCurrentDate);
        cartMap.put("time",saveCurrentTime);
        cartMap.put("quantity",numberBtn.getText().toString());
        cartMap.put("dscount","");

        cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                .child("Foods").child(productID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone())
                                    .child("Foods").child(productID)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(FoodDetailsActivity.this, "Added to  cart  list", Toast.LENGTH_SHORT).show();
                                                Intent intent=new Intent(FoodDetailsActivity.this,HomeActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                        }

                    }
                });
    }


    private void getProductDetails(String productID)
    {
        DatabaseReference productsRef= FirebaseDatabase.getInstance().getReference().child("Foods");

        productsRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    Food food=dataSnapshot.getValue(Food.class);
                    productName.setText(food.getPname());
                    productPrice.setText(food.getPrice());
                    prodductDescription.setText(food.getDescription());
                    Picasso.get().load(food.getImage()).into(productImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void increaseInteger() {
        if(minteger<10)
        {
            minteger = minteger + 1;
            display(minteger);
        }
        else
        {
            Toast.makeText(this, "You cannot exit more than 10  quantities", Toast.LENGTH_SHORT).show();
        }

    }

    public void decreaseinteger() {
        if(minteger>0)
        {
            minteger = minteger - 1;
            display(minteger);
        }
        else
        {
            Toast.makeText(this, "Quantity cannot be negative", Toast.LENGTH_SHORT).show();
        }

    }
    private void display(int number) {
        TextView displayInteger = (TextView) findViewById(
                R.id.number_btn);
        displayInteger.setText("" + number);
    }
}