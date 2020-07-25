package zfani.assaf.radiokahollavan.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import zfani.assaf.radiokahollavan.App;
import zfani.assaf.radiokahollavan.R;
import zfani.assaf.radiokahollavan.base.BaseFragment;
import zfani.assaf.radiokahollavan.ui.activities.AudioTrackActivity;
import zfani.assaf.radiokahollavan.utils.adapters.BroadcastViewHolder;
import zfani.assaf.radiokahollavan.viewmodel.LiveBroadcastViewModel;

public class LiveBroadcastFragment extends BaseFragment {

    @Override
    protected boolean isLiveBroadcast() {
        return true;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LiveBroadcastViewModel liveBroadcastViewModel = new ViewModelProvider(this).get(LiveBroadcastViewModel.class);
        View root = inflater.inflate(R.layout.fragment_live_broadcast, container, false);
        BroadcastViewHolder broadcastViewHolder = new BroadcastViewHolder(root.findViewById(R.id.vBroadcast));
        App.songTitle.observe(getViewLifecycleOwner(), songTitle -> liveBroadcastViewModel.getBroadcast().observe(getViewLifecycleOwner(), broadcastViewHolder::bindData));
        root.findViewById(R.id.ivAudioTrack).setOnClickListener(v -> startActivity(new Intent(getActivity(), AudioTrackActivity.class)));
        return root;
    }
}
