package com.arpaul.track.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arpaul.track.R;
import com.arpaul.track.YourMapActivity;
import com.hypertrack.lib.HyperTrack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aritra on 06-03-2017.
 */

public class ActionsAdapter extends RecyclerView.Adapter<ActionsAdapter.ViewHolder> {

    private Context context;
    private List<String> arrLead = new ArrayList<>();

    public ActionsAdapter(Context context, List<String> arrDashboard) {
        this.context = context;
        this.arrLead = arrDashboard;
    }

    public void refresh(List<String> arrFarms) {
        this.arrLead = arrFarms;
        notifyDataSetChanged();
    }

    @Override
    public ActionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_action, parent, false);
        return new ActionsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ActionsAdapter.ViewHolder holder, final int position) {
        final String objAction = arrLead.get(position);

        holder.tvSender.setText(objAction);
        holder.tvActionClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HyperTrack.completeAction(objAction);
                if(context instanceof YourMapActivity) {
                    ((YourMapActivity)context).hashAction.put(objAction, false);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if(arrLead != null)
            return arrLead.size();

        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public final TextView tvSender;
        public final TextView tvActionClose;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            tvSender                = (TextView) view.findViewById(R.id.tvAction);
            tvActionClose           = (TextView) view.findViewById(R.id.tvActionClose);
        }

        @Override
        public String toString() {
            return "";
        }
    }
}
