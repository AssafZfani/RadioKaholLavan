package zfani.assaf.radiokahollavan.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import zfani.assaf.radiokahollavan.App;
import zfani.assaf.radiokahollavan.R;
import zfani.assaf.radiokahollavan.base.BaseFragment;
import zfani.assaf.radiokahollavan.model.Broadcast;
import zfani.assaf.radiokahollavan.player.RadioManager;
import zfani.assaf.radiokahollavan.player.RadioService;
import zfani.assaf.radiokahollavan.ui.activities.AudioTrackActivity;
import zfani.assaf.radiokahollavan.ui.adapters.BroadcastViewHolder;

public class LiveBroadcastFragment extends BaseFragment {

    private AudioManager audioManager;
    private SeekBar seekbar;
    private final BroadcastReceiver volumeBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (seekbar != null && audioManager != null) {
                seekbar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            }
        }
    };
    private TextView tvYemeniTitle;

    @Override
    protected boolean isLiveBroadcast() {
        return true;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_live_broadcast, container, false);
        BroadcastViewHolder broadcastViewHolder = new BroadcastViewHolder(root.findViewById(R.id.vBroadcast));
        App.songTitle.observe(getViewLifecycleOwner(), songTitle -> {
            String day = App.daysArray[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1];
            Date now = new Date(System.currentTimeMillis());
            App.broadcasts.observe(getViewLifecycleOwner(), map -> {
                List<Broadcast> broadcastList = map.get(day);
                if (broadcastList != null) {
                    for (Broadcast broadcast : broadcastList) {
                        if (broadcast.startDate.before(now) && broadcast.endDate.after(now)) {
                            broadcastViewHolder.bindData(broadcast);
                        }
                    }
                }
            });
        });
        root.findViewById(R.id.ivAudioTrack).setOnClickListener(v -> startActivity(new Intent(getActivity(), AudioTrackActivity.class)));
        seekbar = root.findViewById(R.id.seekBar);
        tvYemeniTitle = root.findViewById(R.id.tvYemeniTitle);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        audioManager = (AudioManager) requireContext().getSystemService(Context.AUDIO_SERVICE);
        seekbar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        seekbar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int newVolume, boolean b) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        requireActivity().registerReceiver(volumeBroadcastReceiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
        App.status.observe(getViewLifecycleOwner(), status -> {
            boolean isPlaying = status.equals(RadioManager.PlaybackStatus.PLAYING);
            boolean isYemeni = getArguments() != null && getArguments().getBoolean("isYemeni", false);
            if (isYemeni) {
                tvYemeniTitle.setText(isPlaying ? R.string.title_kahol_lavan_yemeni : R.string.title_radio_kahol_lavan_yemeni);
                tvYemeniTitle.setVisibility(View.VISIBLE);
            } else {
                tvYemeniTitle.setVisibility(View.INVISIBLE);
            }
            Intent intent = new Intent(requireContext(), RadioService.class);
            intent.setAction(isYemeni ? RadioService.YEMENI : RadioService.MAIN);
            requireActivity().startService(intent);
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        requireActivity().unregisterReceiver(volumeBroadcastReceiver);
    }
}
