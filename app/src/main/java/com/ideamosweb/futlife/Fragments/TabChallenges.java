package com.ideamosweb.futlife.Fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ideamosweb.futlife.Adapters.RowRecyclerChallengeStateAdapter;
import com.ideamosweb.futlife.Controllers.ChallengeController;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.Challenge;
import com.ideamosweb.futlife.Models.User;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.EventBus.MessageBusChallenge;
import com.ideamosweb.futlife.EventBus.StationBus;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Creado por Deimer Villa on 27/03/17.
 * Función:
 */
public class TabChallenges extends Fragment {

    private Context context;
    private UserController userController;
    private ChallengeController challengeController;

    //Elementos
    @Bind(R.id.recycler_my_challenges)
    RecyclerView recycler;
    @Bind(R.id.lbl_data_not_found)
    TextView lbl_data_not_found;

    public static TabChallenges newInstance() {
        return new TabChallenges();
    }

    public TabChallenges() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frame_challenges_states, container, false);
        ButterKnife.bind(this, view);
        setupTab();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        StationBus.getBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        StationBus.getBus().unregister(this);
    }

    @Subscribe
    public void recievedChallenge(MessageBusChallenge messageBusChallenge){
        boolean active = messageBusChallenge.isActive();
        if(active) {
            setupRecycler();
        }
    }

    public void setupTab(){
        context = getActivity().getApplicationContext();
        userController = new UserController(context);
        challengeController = new ChallengeController(context);
        setFontLabel();
        setupRecycler();
    }

    public void setFontLabel(){
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        lbl_data_not_found.setTypeface(bebas_regular);
    }

    public void setupRecycler(){
        User user = userController.show();
        List<Challenge> challenges = listChallenges(user.getUser_id());
        if(challenges.isEmpty()) {
            recycler.setVisibility(View.GONE);
            lbl_data_not_found.setVisibility(View.VISIBLE);
        } else {
            recycler.setVisibility(View.VISIBLE);
            lbl_data_not_found.setVisibility(View.GONE);
            RowRecyclerChallengeStateAdapter adapter = new RowRecyclerChallengeStateAdapter(context, challenges);
            recycler.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
            recycler.setAdapter(adapter);
            recycler.getItemAnimator().setAddDuration(500);
        }
    }

    public List<Challenge> listChallenges(int user_id){
        List<Challenge> challenges = new ArrayList<>();
        List<Challenge> challenges_temp = challengeController.list(user_id);
        for (int i = 0; i < challenges_temp.size(); i++) {
            Challenge challenge = challenges_temp.get(i);
            if (challenge.getPlayer_one() != 0 && challenge.getPlayer_two() != 0) {
                challenges.add(challenge);
            } else {
                System.out.println("Este es: " + challenge.toString());
            }
        }
        return challenges;
    }

}
