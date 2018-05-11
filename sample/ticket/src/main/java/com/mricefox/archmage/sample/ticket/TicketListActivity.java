package com.mricefox.archmage.sample.ticket;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mricefox.archmage.annotation.Target;
import com.mricefox.archmage.runtime.Archmage;
import com.mricefox.archmage.runtime.DefaultTargetUriParser;
import com.mricefox.archmage.runtime.ServiceFindCallback;
import com.mricefox.archmage.runtime.TargetFindCallback;
import com.mricefox.archmage.runtime.Transfer;
import com.mricefox.archmage.sample.hotel.export.HotelBean;
import com.mricefox.archmage.sample.hotel.export.HotelService;
import com.mricefox.archmage.sample.share.export.ShareFragmentContract;
import com.mricefox.archmage.sample.share.export.ShareService;
import com.mricefox.archmage.sample.ticket.export.TicketTargetConstants;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/3/27
 */
@Target(path = "/" + TicketTargetConstants.GROUP + "/" + TicketTargetConstants.PATH_TICKET_LIST)
public class TicketListActivity extends AppCompatActivity implements ShareFragmentContract {
    private static final String TAG = TicketListActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_list);

        findViewById(R.id.pay_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start activity directly
//                Archmage.transfer(DefaultTargetUriParser.createUri("pay", "PayPage"))
//                        .activity()
//                        .intent(new Intent().putExtra("source", "ticket"))
//                        .startForResult(TicketListActivity.this, 5);

                //Start activity from callback
                Archmage.transfer(DefaultTargetUriParser.createUri("pay", "PayPage"))
                        .activity(new TargetFindCallback<Transfer.TargetActivity>() {
                            @Override
                            public void found(Transfer.TargetActivity targetActivity) {
                                targetActivity.intent(new Intent().putExtra("source", "ticket"))
                                        .startForResult(TicketListActivity.this, 5);
                            }

                            @Override
                            public void notFound(Uri uri) {
                                Toast.makeText(TicketListActivity.this, "Activity with uri:" + uri + " not found", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        findViewById(R.id.get_hotel_detail_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get service directly
//                HotelBean bean = Archmage.service(HotelService.class).getHotelDetail(3);
//                Log.d(TAG, "Get hotel detail directly:" + bean);

                //Get service from callback
                Archmage.service(HotelService.class, new ServiceFindCallback<HotelService>() {
                    @Override
                    public void found(HotelService hotelService) {
                        HotelBean bean = hotelService.getHotelDetail(5);
                        Toast.makeText(TicketListActivity.this, "Get hotel detail from callback:" + bean, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void notFound(Class<HotelService> alias) {
                        //Service no found
                        Toast.makeText(TicketListActivity.this, "Hotel service not found, alias:" + alias, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

//        Bundle bundle = new Bundle();
//        bundle.putString("source", "ticket");
//        //Get fragment directly
//        Fragment fragment = Archmage.transfer(DefaultTargetUriParser.createUri("share", "ShareArea"))
//                .fragmentV4()
//                .arguments(bundle)
//                .get();
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.add(R.id.share_fragment_container, fragment, "ShareFragment");
//        transaction.commitAllowingStateLoss();

        //Get fragment from callback
        Archmage.transfer(DefaultTargetUriParser.createUri("share", "ShareArea"))
                .fragmentV4(new TargetFindCallback<Transfer.TargetFragmentV4>() {
                    @Override
                    public void found(Transfer.TargetFragmentV4 targetFragmentV4) {
                        Bundle bundle = new Bundle();
                        bundle.putString("source", "ticket");
                        Fragment fragment = targetFragmentV4.arguments(bundle).get();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.add(R.id.share_fragment_container, fragment, "ShareFragment");
                        transaction.commitAllowingStateLoss();
                    }

                    @Override
                    public void notFound(Uri uri) {
                        Toast.makeText(TicketListActivity.this, "Fragment with uri:" + uri + " not found", Toast.LENGTH_SHORT).show();
                    }
                });

        final TextView platformsTxt = (TextView) findViewById(R.id.share_platforms_txt);

        Archmage.service(ShareService.class, new ServiceFindCallback<ShareService>() {
            @Override
            public void found(ShareService shareService) {
                String[] platforms = shareService.getSharePlatforms();
                StringBuilder txt = new StringBuilder("Share platforms:");
                for (String platform : platforms) {
                    txt.append(platform).append(" ");
                }
                platformsTxt.setText(txt.toString());
            }

            @Override
            public void notFound(Class<ShareService> alias) {
                platformsTxt.setText("No platforms");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 1:
                Log.d(TAG, "result from pay page:" + data.getStringExtra("pay_ext_info"));
                break;
            default:
                throw new AssertionError();
        }
    }

    //interaction with share fragment
    @Override
    public void shareDone(String shareResult, ShareCallback shareCallback) {
        Log.d(TAG, "Share result:" + shareResult);
        shareCallback.callback("well done"); //Notify ShareFragment
    }
}
