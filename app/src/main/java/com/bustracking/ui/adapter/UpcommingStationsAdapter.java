package com.bustracking.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bustracking.R;
import com.bustracking.model.functionalmodels.BookingModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UpcommingStationsAdapter extends RecyclerView.Adapter<UpcommingStationsAdapter.RecentSearchViewHolder> {

    /*variable declaration*/
    private onClickListener mListener;
    private Context mContext;
    private ArrayList<BookingModel> mRecentSearchList;

    /*constructor*/
    public UpcommingStationsAdapter(Context aContext, ArrayList<BookingModel> aRecentSearchlist) {
        /* initialize parameter*/
        this.mContext = aContext;
        this.mRecentSearchList = aRecentSearchlist;
    }

    /*set onClick listener*/
    public void setOnClickListener(onClickListener mListener) {
        this.mListener = mListener;
    }

    /*  inflate layout */
    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public RecentSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecentSearchViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recentsearch, null));
    }

    /*bind viewholder*/
    @Override
    public void onBindViewHolder(@NonNull RecentSearchViewHolder holder, int position) {

        final BookingModel mBookibgModel = mRecentSearchList.get(position);

        holder.mTvDestination.setText(mBookibgModel.getDestination());
        holder.mTvDate.setText(mBookibgModel.getDate());
    }

    /*item count*/
    @Override
    public int getItemCount() {
        return mRecentSearchList.size();
    }

    /*onclick listener interface*/
    public interface onClickListener {

        void onClick(BookingModel cardModel);
    }

    /*view holder*/
    class RecentSearchViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvDestination, mTvDate;

        RecentSearchViewHolder(View itemView) {
            super(itemView);
            mTvDestination = itemView.findViewById(R.id.upCommingStationName);
            mTvDate = itemView.findViewById(R.id.upCommingStationDate);
        }
    }

}

