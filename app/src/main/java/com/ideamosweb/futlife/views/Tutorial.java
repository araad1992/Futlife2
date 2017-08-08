package com.ideamosweb.futlife.views;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import com.ideamosweb.futlife.Adapters.TutoSwiperAdapter;
import com.ideamosweb.futlife.R;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Tutorial extends Activity {

    private Context context;

    @Bind(R.id.view_pager)ViewPager view_pager;
    @Bind(R.id.fab)FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        ButterKnife.bind(this);
        setupActivity();
    }

    public void setupActivity() {
        context = this;
        setupSwiper();
    }

    public void setupSwiper() {
        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.tuto_one);
        images.add(R.drawable.tuto_two);
        images.add(R.drawable.tuto_three);
        TutoSwiperAdapter swipe_adapter = new TutoSwiperAdapter(context, images);
        view_pager.setAdapter(swipe_adapter);
        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageScrollStateChanged(int state) {}
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 2:
                        if(!fab.isShown()) {
                            fab.show();
                        }
                        break;
                }
            }
        });
    }

    @OnClick(R.id.fab)
    public void clickNext(){
        finish();
    }

}
