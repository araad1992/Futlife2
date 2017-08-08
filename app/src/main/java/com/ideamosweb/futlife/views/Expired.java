package com.ideamosweb.futlife.views;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import com.ideamosweb.futlife.Adapters.RowRecyclerExpiredAdapter;
import com.ideamosweb.futlife.Controllers.ChallengeController;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.Challenge;
import com.ideamosweb.futlife.Models.User;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.Utils.Utils;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

public class Expired extends AppCompatActivity {

    //Iniciadores
    private Context context;
    private UserController userController;
    private ChallengeController challengeController;
    private Utils utils;

    //Elementos de la vista
    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.lbl_title_activity)TextView lbl_title_activity;
    @Bind(R.id.lbl_data_not_found)TextView lbl_data_not_found;
    @Bind(R.id.recycler_expired)RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expired);
        ButterKnife.bind(this);
        setupActivity();
    }

    public void setupActivity(){
        context = this;
        userController = new UserController(context);
        challengeController = new ChallengeController(context);
        utils = new Utils(context);
        setupToolbar();
        getChallengesExpired();
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
                    finish();
                }
            });
        }
        Typeface bebas_bold = Typeface.createFromAsset(getAssets(), "fonts/bebas_neue_bold.ttf");
        lbl_title_activity.setTypeface(bebas_bold);
    }

    public void getChallengesExpired(){
        User user = userController.show();
        List<Challenge> expireds = new ArrayList<>();
        List<Challenge> challenges = challengeController.list(user.getUser_id());
        for (int i = 0; i < challenges.size(); i++) {
            Challenge challenge = challenges.get(i);
            if(challenge.getState().equalsIgnoreCase("vencido") || challenge.getState().equalsIgnoreCase("expirado")) {
                if(utils.compareDates(challenge.getDeadline())) {
                    expireds.add(challenge);
                }
            }
        }
        setupRecycler(expireds);
    }

    public void setupRecycler(List<Challenge> expireds){
        if(expireds.isEmpty()) {
            Typeface bebas_regular = Typeface.createFromAsset(getAssets(), "fonts/bebas_neue_regular.ttf");
            lbl_data_not_found.setTypeface(bebas_regular);
            recycler.setVisibility(View.GONE);
        } else {
            lbl_data_not_found.setVisibility(View.GONE);
            recycler.removeAllViewsInLayout();
            RowRecyclerExpiredAdapter adapter = new RowRecyclerExpiredAdapter(context, expireds);
            recycler.setLayoutManager(
                    new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            );
            recycler.setAdapter(adapter);
            recycler.setHasFixedSize(true);
            recycler.setVisibility(View.VISIBLE);
        }
    }

}
