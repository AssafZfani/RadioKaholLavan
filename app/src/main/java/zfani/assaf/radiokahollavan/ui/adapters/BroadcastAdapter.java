package zfani.assaf.radiokahollavan.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import zfani.assaf.radiokahollavan.R;
import zfani.assaf.radiokahollavan.model.Broadcast;

public class BroadcastAdapter extends ListAdapter<Broadcast, BroadcastViewHolder> {

    public BroadcastAdapter() {
        super(new DiffUtil.ItemCallback<Broadcast>() {
            @Override
            public boolean areItemsTheSame(@NonNull Broadcast oldItem, @NonNull Broadcast newItem) {
                return oldItem.getId().equalsIgnoreCase(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Broadcast oldItem, @NonNull Broadcast newItem) {
                return oldItem.name.equalsIgnoreCase(newItem.name) && oldItem.broadcasterName.equalsIgnoreCase(newItem.broadcasterName);
            }
        });
    }

    @NonNull
    @Override
    public BroadcastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BroadcastViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_broadcast, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BroadcastViewHolder holder, int position) {
        holder.bindData(getItem(position));
    }
}
