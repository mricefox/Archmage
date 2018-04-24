package com.mricefox.archmage.sample.share;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mricefox.archmage.annotation.Target;
import com.mricefox.archmage.sample.share.export.ShareFragmentContract;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/4/23
 */
@Target(path = "/share/ShareArea")
public class ShareFragment extends Fragment {
    private ShareFragmentContract contract;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final String src = getArguments().getString("source");

        View v = inflater.inflate(R.layout.fragment_share, container, false);

        v.findViewById(R.id.share_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contract.shareDone("Share done, src:" + src, new ShareFragmentContract.ShareCallback() {
                    @Override
                    public void callback(String data) {
                        Toast.makeText(getActivity(), "Callback from src:" + src + " data:" + data, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ShareFragmentContract) {
            contract = (ShareFragmentContract) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ShareFragmentContract");
        }
    }
}
