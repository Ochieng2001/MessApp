package com.example.kibumess;

import static com.example.kibumess.Constants.BUSINESS_SHORT_CODE;
import static com.example.kibumess.Constants.CALLBACKURL;
import static com.example.kibumess.Constants.PARTYB;
import static com.example.kibumess.Constants.PASSKEY;
import static com.example.kibumess.Constants.TRANSACTION_TYPE;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.androidstudy.daraja.data.model.AccessToken;
import com.example.kibumess.Model.STKPush;
import com.example.kibumess.Prevalent.Prevalent;
import com.example.kibumess.services.DarajaApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ConfirmOrderActivity extends AppCompatActivity implements View.OnClickListener{

    public static  final String PRICE="PRICE";
    private DarajaApiClient mApiClient;
    private ProgressDialog mProgressDialog;
    private int Amount;



//    @BindView(R.id.etAmount)
   private TextView mAmount;
    @BindView(R.id.etPhone)
    EditText mPhone;
    @BindView(R.id.btnPay)
    Button mPay;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cornfirm_order);

        mPay=findViewById(R.id.btnPay);
//        mPay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ConfirmOrder();
//            }
//        });

        ButterKnife.bind(this);

        mProgressDialog = new ProgressDialog(this);
        mApiClient = new DarajaApiClient();
        mApiClient.setIsDebug(true); //Set True to enable logging, false to disable.

        mPay.setOnClickListener(this);

       mAmount= findViewById(R.id.etAmount);
        Intent i=getIntent();
        Amount=i.getIntExtra(PRICE,0);
        mAmount.setText(String.valueOf(Amount));
        mAmount.setTextSize(20);
        mAmount.setTextColor(Color.RED);



        getAccessToken();

    }

    private void getAccessToken() {
        mApiClient.setGetAccessToken(true);
        mApiClient.mpesaService().getAccessToken().enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(@NonNull Call<AccessToken> call, @NonNull Response<AccessToken> response) {

                if (response.isSuccessful()) {

                    mApiClient.setAuthToken(response.body().getAccess_token());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessToken> call, @NonNull Throwable t) {

            }
        });

    }

    @Override
    public void onClick(View view) {

        if (view== mPay){
            String phone_number = mPhone.getText().toString();
            String amount = mAmount.getText().toString();

            performSTKPush(phone_number,amount);
             //ConfirmOrder();



        }

    }
    public void performSTKPush(String phone_number,String amount) {
        mProgressDialog.setMessage("Processing your request");
        mProgressDialog.setTitle("Please Wait...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
        String timestamp = Utils.getTimestamp();
        STKPush stkPush = new STKPush(
                BUSINESS_SHORT_CODE,
                Utils.getPassword(BUSINESS_SHORT_CODE, PASSKEY, timestamp),
                timestamp,
                TRANSACTION_TYPE,
                String.valueOf(amount),
                Utils.sanitizePhoneNumber(phone_number),
                PARTYB,
                Utils.sanitizePhoneNumber(phone_number),
                CALLBACKURL,
                "KIBU MESS ", //Account reference
                "For Food Application"  //Transaction description
        );

        mApiClient.setGetAccessToken(false);

        //Sending the data to the Mpesa API, remember to remove the logging when in production.
        mApiClient.mpesaService().sendPush(stkPush).enqueue(new Callback<STKPush>() {
            @Override
            public void onResponse(@NonNull Call<STKPush> call, @NonNull Response<STKPush> response) {
                mProgressDialog.dismiss();
                try {
                    if (response.isSuccessful()) {
                        Timber.d("post submitted to API. %s", response.body());

                       // ConfirmOrder();

                        //Toast.makeText(ConfirmOrderActivity.this, "You have successfully place the order", Toast.LENGTH_SHORT).show();
                    } else {
                        Timber.e("Response %s", response.errorBody().string());
                       // Toast.makeText(ConfirmOrderActivity.this, "You have cancelled the transaction", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<STKPush> call, @NonNull Throwable t) {
                mProgressDialog.dismiss();
                Timber.e(t);
            }
        });
    }


    private void ConfirmOrder()
    {
        final String saveCurrentDate,saveCurrentTime;
        Calendar calForDate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd,yyy");
        saveCurrentDate=currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("MMM dd,yyy");
        saveCurrentTime=currentTime.format(calForDate.getTime());

        final DatabaseReference ordersRef= FirebaseDatabase.getInstance()
                .getReference().child("Orders");

        HashMap<String,Object>orderMap=new HashMap<>();
      //  orderMap.put("amount",mAmount);
        //orderMap.put("phone",mPhone);
        orderMap.put("date",saveCurrentDate);
        orderMap.put("time",saveCurrentTime);
        ordersRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child("User View")
                            .child(Prevalent.currentOnlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(ConfirmOrderActivity.this, "Your Final order has been placed successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(ConfirmOrderActivity.this,HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK/ Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        Toast.makeText(ConfirmOrderActivity.this, "Failed to place ordr successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(ConfirmOrderActivity.this,HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK/ Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                    finish();

                                }
                            });
                }
                else
                {
                    Toast.makeText(ConfirmOrderActivity.this, "Not  successfully placed", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(ConfirmOrderActivity.this,HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK/ Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}