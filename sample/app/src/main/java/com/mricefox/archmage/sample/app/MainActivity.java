package com.mricefox.archmage.sample.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.mricefox.archmage.annotation.Target;
import com.mricefox.archmage.sample.R;
import com.mricefox.archmage.sample.hotel.HotelListActivity;
import com.mricefox.archmage.sample.ticket.TicketListActivity;

@Target(path = "/host/Main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.ticket_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TicketListActivity.class));
            }
        });

        findViewById(R.id.hotel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HotelListActivity.class));
            }
        });
    }
}
