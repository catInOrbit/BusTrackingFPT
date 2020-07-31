package com.bustracking.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bustracking.BaseActivity;
import com.bustracking.R;
import com.bustracking.model.functionalmodels.NewOfferModel;
import com.bustracking.ui.adapter.ViewOfferAdapter;
import com.bustracking.utils.Constants;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class OffersActivity extends BaseActivity implements View.OnClickListener {
    /*variable declaration*/
    private RecyclerView mRvOffer;
    private ArrayList<NewOfferModel> mOfferList;
    private ImageView mIvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        initLayouts();
        initializeListeners();

    }

    /* initialize listener */
    private void initializeListeners() {
        mIvBack.setOnClickListener(this);
        mOfferList = new ArrayList<>();
        mOfferList = (ArrayList<NewOfferModel>)getIntent().getSerializableExtra(Constants.intentdata.OFFER);

        mRvOffer.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mRvOffer.setAdapter(new ViewOfferAdapter(OffersActivity.this, mOfferList));
        RunLayoutAnimation(mRvOffer);
    }

    /* init layout */
    private void initLayouts() {
        mIvBack = findViewById(R.id.ivBack);
        mRvOffer = findViewById(R.id.rvOffer);
    }

    /* onClick listener */
    @Override
    public void onClick(View v) {
        if (v == mIvBack) onBackPressed();
    }
}
