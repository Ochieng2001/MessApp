package com.example.kibumess;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kibumess.Model.Food;
import com.example.kibumess.Prevalent.Prevalent;
import com.example.kibumess.ViewHolder.FoodViewHolder;
import com.example.kibumess.databinding.ActivityHomeBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{

   // private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;

    private DatabaseReference FoodsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    private  String type="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        if(bundle !=null)
        {
            type=getIntent().getExtras().get("Admin").toString();
        }


        binding.appBarHome.toolbar.setTitle("Home");
        setSupportActionBar(binding.appBarHome.toolbar);
        FoodsRef= FirebaseDatabase.getInstance().getReference().child("Foods");


        binding.appBarHome.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
             Intent intent=new Intent(HomeActivity.this,CartActivity.class);
             startActivity(intent);
            }
        });
        DrawerLayout drawer= findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(
                this,drawer,binding.appBarHome.toolbar,R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView= findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView=navigationView.getHeaderView(0);
        TextView userNameTextView=headerView.findViewById(R.id.user_profie_name);
        CircleImageView circleImageView=headerView.findViewById(R.id.user_profile_image);

        if(!type.equals("Admin"))
        {
            userNameTextView.setText(Prevalent.currentOnlineUser.getName());
            Picasso.get()
                    .load(Prevalent.currentOnlineUser.getImage())
                    .placeholder(R.drawable.profile).into(circleImageView);
        }

        recyclerView=findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        }


        // Food Adapter For RecyclerView
    @Override
    protected void onStart() {
        super.onStart();


        /* Mapping Firebase data and POJO class generated from Firebase DB Food Items
        to the Adapter Adapter.
        */
        FirebaseRecyclerOptions<Food> options=
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(FoodsRef,Food.class).build();


        FirebaseRecyclerAdapter<Food, FoodViewHolder>adapter=
                new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model)
                    {
                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductDescription.setText(model.getDescription());
                        holder.txtProductPrice.setText("Price= " + model.getPrice() +"Ksh");
                        Picasso.get().load(model.getImage()).into(holder.imageView);



                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {

                                if(type.equals("Admin"))
                                {
                                    Intent intent = new Intent(HomeActivity.this, AdminMaintainFoodActivity.class);
                                    intent.putExtra("pid",model.getPid());
                                    startActivity(intent);

                                }
                                else
                                {
                                    Intent intent = new Intent(HomeActivity.this, FoodDetailsActivity.class);
                                    intent.putExtra("pid",model.getPid());
                                    startActivity(intent);

                                }



                            }
                        });

                    }

                    @NonNull
                    @Override
                    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.food_items_layout,parent,false);
                        FoodViewHolder holder=new FoodViewHolder(view);
                        return holder;
                    }
                };

        //setting the adapter
        recyclerView.setAdapter(adapter);
        adapter.startListening();



    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }






    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

//        if (id == R.id.action_settings)
//        {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cart)
        {
            Intent intent=new Intent(HomeActivity.this,CartActivity.class);
            startActivity(intent);


        }
        else if (id == R.id.nav_search)
        {
            Intent intent=new Intent(HomeActivity.this,SearchFoodActivity.class);
            startActivity(intent);

        }
        else if (id == R.id.nav_categories)
        {

        }
        else if (id == R.id.nav_settings)
        {
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_logout)
        {
            Paper.book().destroy();

            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }





}