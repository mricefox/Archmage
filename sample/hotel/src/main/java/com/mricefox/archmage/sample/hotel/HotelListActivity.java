package com.mricefox.archmage.sample.hotel;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.mricefox.archmage.runtime.Archmage;
import com.mricefox.archmage.runtime.DefaultTargetUriParser;
import com.mricefox.archmage.runtime.TargetFindCallback;
import com.mricefox.archmage.runtime.Transfer;
import com.mricefox.archmage.sample.ticket.export.TicketTargetConstants;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/3/27
 */

public class HotelListActivity extends AppCompatActivity {
    private static final String TAG = HotelListActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_list);

        findViewById(R.id.go_ticket_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get ticket's target path from its exported class
                Archmage.transfer(DefaultTargetUriParser.createUri(TicketTargetConstants.GROUP, TicketTargetConstants.PATH_TICKET_LIST))
                        .activity(new TargetFindCallback<Transfer.TargetActivity>() {
                            @Override
                            public void found(Transfer.TargetActivity targetActivity) {
                                targetActivity.start(HotelListActivity.this);
                            }

                            @Override
                            public void notFound(Uri uri) {
                                Log.e(TAG, "Activity with uri:" + uri + " not found");
                            }
                        });
            }
        });
    }
}
