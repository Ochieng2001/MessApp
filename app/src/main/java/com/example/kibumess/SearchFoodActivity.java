package com.example.kibumess;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kibumess.Model.Food;
import com.example.kibumess.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SearchFoodActivity extends AppCompatActivity {
    private Button searchBtn;
    private EditText inputText;
    private RecyclerView searchList;
    private String SearchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);

        inputText=findViewById(R.id.search_food_name);
        searchBtn=findViewById(R.id.search_btn);
        searchList=findViewById(R.id.search_list);
        searchList.setLayoutManager(new LinearLayoutManager(SearchFoodActivity.this));

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SearchInput=inputText.getText().toString();
                onStart();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Foods");
        FirebaseRecyclerOptions<Food> options=
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(reference.orderByChild("pname").startAt(SearchInput),Food.class)
                        .build();

        FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter
                =new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {

                holder.txtProductName.setText(model.getPname());
                holder.txtProductDescription.setText(model.getDescription());
                holder.txtProductPrice.setText("Price= " + model.getPrice() +"Ksh");
                Picasso.get().load(model.getImage()).into(holder.imageView);



                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SearchFoodActivity.this, FoodDetailsActivity.class);
                        intent.putExtra("pid",model.getPid());
                        startActivity(intent);

                    }
                });

            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.food_items_layout,parent,false);
                FoodViewHolder holder=new FoodViewHolder(view);
                return holder;
            }
        };
        searchList.setAdapter(adapter);
        adapter.startListening();
    }
}