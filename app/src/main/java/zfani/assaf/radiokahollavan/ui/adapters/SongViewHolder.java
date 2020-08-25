package zfani.assaf.radiokahollavan.ui.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import zfani.assaf.radiokahollavan.R;
import zfani.assaf.radiokahollavan.model.Song;

class SongViewHolder extends RecyclerView.ViewHolder {

    private final TextView tvSongName;

    SongViewHolder(@NonNull View itemView) {
        super(itemView);
        tvSongName = itemView.findViewById(R.id.tvSongName);
    }

    void bindData(Song song) {
        if (song == null) {
            itemView.setVisibility(View.GONE);
        } else {
            itemView.setVisibility(View.VISIBLE);
            tvSongName.setText(song.getTitle());
        }
    }
}
