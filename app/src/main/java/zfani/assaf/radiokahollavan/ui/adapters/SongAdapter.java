package zfani.assaf.radiokahollavan.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import zfani.assaf.radiokahollavan.R;
import zfani.assaf.radiokahollavan.model.Song;

public class SongAdapter extends ListAdapter<Song, SongViewHolder> {

    public SongAdapter() {
        super(new DiffUtil.ItemCallback<Song>() {
            @Override
            public boolean areItemsTheSame(@NonNull Song oldItem, @NonNull Song newItem) {
                return oldItem.getDate() == newItem.getDate();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Song oldItem, @NonNull Song newItem) {
                return oldItem.getTitle().equalsIgnoreCase(newItem.getTitle()) && oldItem.getDate() == newItem.getDate();
            }
        });
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SongViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_song, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        holder.bindData(getItem(position));
    }
}
