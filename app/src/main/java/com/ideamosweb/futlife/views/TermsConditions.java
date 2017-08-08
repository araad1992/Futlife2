package com.ideamosweb.futlife.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.ideamosweb.futlife.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TermsConditions extends AppCompatActivity {

    private Context context;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsing_toolbar;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.nested_terms)
    NestedScrollView nested_terms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);
        ButterKnife.bind(this);
        setupActivity();
    }

    public void setupActivity() {
        context = this;
        setupCollapsingToolbar();
        setupToolbar();
        setupNested();
    }

    public void setupCollapsingToolbar(){
        Typeface bebas_bold = Typeface.createFromAsset(getAssets(), "fonts/bebas_neue_bold.ttf");
        Typeface bebas_regular = Typeface.createFromAsset(getAssets(), "fonts/bebas_neue_regular.ttf");
        collapsing_toolbar.setTitle(getString(R.string.title_activity_terms_conditions));
        collapsing_toolbar.setExpandedTitleColor(ContextCompat.getColor(context, R.color.icons));
        collapsing_toolbar.setCollapsedTitleTypeface(bebas_bold);
        collapsing_toolbar.setExpandedTitleTypeface(bebas_regular);
    }

    public void setupToolbar(){
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setContentInsetStartWithNavigation(0);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                    finish();
                }
            });
        }
    }

    public void setupNested() {
        nested_terms.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });
    }

    @OnClick(R.id.fab)
    public void clockFab(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", 12);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

}
