package com.mricefox.archmage.sample.pay;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mricefox.archmage.annotation.Target;

@Target(path = "/pay/PayPage")
public class PayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        final String src = getIntent().getStringExtra("source");

        findViewById(R.id.pay_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PayActivity.this, "Pay done. source:" + src, Toast.LENGTH_SHORT).show();
                setResult("ticket".equals(src) ? 1 : 0, new Intent() {{
                    putExtra("pay_ext_info", "ticket pay done");
                }});
                finish();
            }
        });

        findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PayActivity.this, "Pay cancel. source:" + src, Toast.LENGTH_SHORT).show();
                setResult("ticket".equals(src) ? 1 : 0, new Intent() {{
                    putExtra("pay_ext_info", "ticket pay cancel");
                }});
                finish();
            }
        });
    }
}
