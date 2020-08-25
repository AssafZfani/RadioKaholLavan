package zfani.assaf.radiokahollavan.base;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import zfani.assaf.radiokahollavan.App;
import zfani.assaf.radiokahollavan.R;
import zfani.assaf.radiokahollavan.player.RadioManager;
import zfani.assaf.radiokahollavan.ui.activities.MainActivity;

public class BaseFragment extends Fragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View root = requireView();
        TextView tvTitle = root.findViewById(R.id.tvTitle);
        int title = getTitle();
        if (tvTitle != null && title != 0) {
            tvTitle.setText(title);
        }
        View vPlayer = root.findViewById(R.id.vPlayer);
        if (vPlayer != null) {
            vPlayer.setBackgroundColor(Color.WHITE);
        }
        TextView tvSongName = root.findViewById(R.id.tvSongName);
        ImageView ivPlayOrPause = root.findViewById(R.id.ivPlayOrPause);
        App.songTitle.observe(getViewLifecycleOwner(), song -> {
            tvSongName.setText(song == null || song.isEmpty() ? getString(R.string.app_name) : song);
            tvSongName.setSelected(true);
        });
        ivPlayOrPause.setOnClickListener(v -> ((MainActivity) requireActivity()).radioManager.playOrStop());
        App.status.observe(getViewLifecycleOwner(), status -> {
            if (isLiveBroadcast()) {
                ivPlayOrPause.setBackgroundResource(!status.equals(RadioManager.PlaybackStatus.PLAYING) ? R.drawable.ic_play : R.drawable.ic_pause);
            } else {
                ivPlayOrPause.setImageResource(!status.equals(RadioManager.PlaybackStatus.PLAYING) ? R.drawable.ic_play_circle : R.drawable.ic_pause_circle);
            }
        });
    }

    protected int getTitle() {
        return 0;
    }

    protected boolean isLiveBroadcast() {
        return false;
    }
}
