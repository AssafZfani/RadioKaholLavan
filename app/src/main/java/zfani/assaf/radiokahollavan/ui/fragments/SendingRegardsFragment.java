package zfani.assaf.radiokahollavan.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import zfani.assaf.radiokahollavan.R;
import zfani.assaf.radiokahollavan.base.BaseFragment;
import zfani.assaf.radiokahollavan.viewmodel.SendingRegardsViewModel;

public class SendingRegardsFragment extends BaseFragment {

    @Override
    protected int getTitle() {
        return R.string.title_sending_regards;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SendingRegardsViewModel sendingRegardsViewModel = new ViewModelProvider(this).get(SendingRegardsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_sending_regards, container, false);
        TextView tvSubTitle = root.findViewById(R.id.tvSubTitle);
        EditText etName = root.findViewById(R.id.etName);
        EditText etMassage = root.findViewById(R.id.etMassage);
        root.findViewById(R.id.btnSendingRegards).setOnClickListener(v -> {
            String name = etName.getText().toString(), message = etMassage.getText().toString();
            if (name.isEmpty() || message.isEmpty()) {
                Toast.makeText(getContext(), R.string.empty_fields, Toast.LENGTH_SHORT).show();
            } else {
                sendingRegardsViewModel.sendRegards(name, message);
            }
        });
        sendingRegardsViewModel.getText().observe(getViewLifecycleOwner(), text -> {
            tvSubTitle.setText(text);
            etMassage.setText("");
        });
        return root;
    }
}
