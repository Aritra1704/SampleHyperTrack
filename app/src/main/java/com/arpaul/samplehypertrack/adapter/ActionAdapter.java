package com.arpaul.samplehypertrack.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arpaul.samplehypertrack.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aritra Ranjan on 5/12/2017.
 */

public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ViewHolder> {

    private Context context;
    private List<String> arrAction = new ArrayList<>();
    private String TAG = "AllMessagesAdapter";

    public ActionAdapter(Context context, List<String> arrAction) {
        this.context = context;
        this.arrAction = arrAction;
    }

    public void refresh(List<String> arrAction) {
        this.arrAction = arrAction;
        notifyDataSetChanged();
    }

    @Override
    public ActionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_action, parent, false);
        return new ActionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ActionAdapter.ViewHolder holder, final int position) {
        final String strAction = arrAction.get(position);

        holder.tvAction.setText(strAction);
    }

    @Override
    public int getItemCount() {
        if(arrAction != null)
            return arrAction.size();

        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public final TextView tvAction;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            tvAction            = (TextView) view.findViewById(R.id.tvAction);
        }

        @Override
        public String toString() {
            return "";
        }
    }
}
