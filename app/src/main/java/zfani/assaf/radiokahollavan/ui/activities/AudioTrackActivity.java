package zfani.assaf.radiokahollavan.ui.activities;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import zfani.assaf.radiokahollavan.R;
import zfani.assaf.radiokahollavan.base.BaseActivity;
import zfani.assaf.radiokahollavan.ui.adapters.SongAdapter;
import zfani.assaf.radiokahollavan.viewmodel.AudioTrackViewModel;

public class AudioTrackActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_track);
        ((TextView) findViewById(R.id.tvTitle)).setText(R.string.last_played);
        RecyclerView rvSongList = findViewById(R.id.rvSongList);
        rvSongList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 50;
            }
        });
        SongAdapter songAdapter;
        rvSongList.setAdapter(songAdapter = new SongAdapter());
        new ViewModelProvider(this).get(AudioTrackViewModel.class).getAudioTrack().observe(this, songs -> {
            songAdapter.submitList(songs);
            findViewById(R.id.spin_kit).setVisibility(View.GONE);
        });
    }
}
