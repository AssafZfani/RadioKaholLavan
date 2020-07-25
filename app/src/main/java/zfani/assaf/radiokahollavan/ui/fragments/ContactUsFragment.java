package zfani.assaf.radiokahollavan.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import zfani.assaf.radiokahollavan.App;
import zfani.assaf.radiokahollavan.R;
import zfani.assaf.radiokahollavan.base.BaseFragment;

public class ContactUsFragment extends BaseFragment implements View.OnClickListener {

    @Override
    protected int getTitle() {
        return R.string.app_name;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contact_us, container, false);
        final TextView tvDescription = root.findViewById(R.id.tvDescription);
        root.findViewById(R.id.btnInstagram).setOnClickListener(this);
        root.findViewById(R.id.btnTwitter).setOnClickListener(this);
        root.findViewById(R.id.btnFacebook).setOnClickListener(this);
        root.findViewById(R.id.btnEmail).setOnClickListener(this);
        root.findViewById(R.id.btnMessage).setOnClickListener(this);
        root.findViewById(R.id.btnPhone).setOnClickListener(this);
        root.findViewById(R.id.btnTerms).setOnClickListener(this);
        root.findViewById(R.id.btnWeb).setOnClickListener(this);
        App.appInfo.observe(getViewLifecycleOwner(), tvDescription::setText);
        tvDescription.setMovementMethod(new ScrollingMovementMethod());
        return root;
    }

    @Override
    public void onClick(View v) {
        String action = null;
        switch (v.getId()) {
            case R.id.btnInstagram:
                action = "https://www.instagram.com/radio_kahol_lavan/";
                break;
            case R.id.btnTwitter:
                action = "https://mobile.twitter.com/RadioKaholLavan";
                break;
            case R.id.btnFacebook:
                action = "https://m.facebook.com/radiokahollavan";
                break;
            case R.id.btnTerms:
                action = "https://www.radiokahollavan.co.il/terms-of-use-app";
                break;
            case R.id.btnWeb:
                action = "https://www.radiokahollavan.co.il/";
                break;
            case R.id.btnEmail:
                action = "mailto:radiokahollavan@gmail.com";
                break;
            case R.id.btnMessage:
                action = "sms:0515055555";
                break;
            case R.id.btnPhone:
                action = "tel:0515055555";
                break;
        }
        if (action != null) {
            startActivity(new Intent(v.getId() == R.id.btnMessage ? Intent.ACTION_SENDTO : Intent.ACTION_VIEW, Uri.parse(action)));
        }
    }
}
