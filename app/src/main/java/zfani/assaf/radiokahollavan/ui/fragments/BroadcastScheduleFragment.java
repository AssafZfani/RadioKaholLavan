package zfani.assaf.radiokahollavan.ui.fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import zfani.assaf.radiokahollavan.App;
import zfani.assaf.radiokahollavan.R;
import zfani.assaf.radiokahollavan.base.BaseFragment;
import zfani.assaf.radiokahollavan.model.Broadcast;
import zfani.assaf.radiokahollavan.ui.adapters.BroadcastAdapter;

public class BroadcastScheduleFragment extends BaseFragment {

    private RecyclerView rvBroadcastList;
    private BroadcastAdapter broadcastAdapter;

    @Override
    protected int getTitle() {
        return R.string.title_broadcast_schedule;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_broadcast_schedule, container, false);
        TabLayout tlBroadcastTab = root.findViewById(R.id.tlBroadcastTab);
        for (String title : getResources().getStringArray(R.array.tab_titles)) {
            tlBroadcastTab.addTab(tlBroadcastTab.newTab().setText(title));
        }
        tlBroadcastTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                bindData(tab.getPosition(), false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        rvBroadcastList = root.findViewById(R.id.rvBroadcastList);
        rvBroadcastList.setAdapter(broadcastAdapter = new BroadcastAdapter());
        rvBroadcastList.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 50;
            }
        });
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        TabLayout.Tab tab = tlBroadcastTab.getTabAt(currentDay);
        if (tab != null) {
            tab.select();
        }
        bindData(currentDay, true);
        return root;
    }

    private void bindData(int position, boolean toScroll) {
        App.broadcasts.observe(getViewLifecycleOwner(), map -> {
            List<Broadcast> broadcastList = map.get(App.daysArray[position]);
            if (broadcastList != null) {
                Collections.sort(broadcastList);
                broadcastAdapter.submitList(broadcastList);
                if (toScroll) {
                    scrollToCurrentBroadcast(broadcastList);
                }
            }
        });
    }

    private void scrollToCurrentBroadcast(List<Broadcast> broadcasts) {
        Date now = new Date(System.currentTimeMillis());
        for (int i = 0; i < broadcasts.size(); i++) {
            Broadcast broadcast = broadcasts.get(i);
            if (now.after(broadcast.startDate) && now.before(broadcast.endDate)) {
                rvBroadcastList.scrollToPosition(i);
            }
        }
    }
}
