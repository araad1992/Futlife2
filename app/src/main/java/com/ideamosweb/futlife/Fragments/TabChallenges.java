package com.ideamosweb.futlife.Fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.ideamosweb.futlife.Adapters.RowRecyclerChallengeStateAdapter;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.Challenge;
import com.ideamosweb.futlife.Models.User;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.EventBus.MessageBusChallenge;
import com.ideamosweb.futlife.EventBus.StationBus;
import com.ideamosweb.futlife.Service.Api;
import com.squareup.otto.Subscribe;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Creado por Deimer Villa on 27/03/17.
 * Función:
 */
public class TabChallenges extends Fragment {

    private Context context;
    private UserController userController;

    //Elementos
    @Bind(R.id.progress_bar)
    ProgressBar progress_bar;
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
            getChallenges();
        }
    }

    public void setupTab(){
        context = getActivity().getApplicationContext();
        userController = new UserController(context);
        setFontLabel();
        getChallenges();
    }

    public void setFontLabel(){
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        lbl_data_not_found.setTypeface(bebas_regular);
    }

    public void getChallenges(){
        User user = userController.show();
        String token = "Bearer " + user.getToken();
        final String url = getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.showChallenges(token, user.getUser_id(), new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                if (success) {
                    JsonArray data = jsonObject.getAsJsonArray("data");
                    saveChallenges(data);
                }
            }
            @Override
            public void failure(RetrofitError error) {
                progress_bar.setVisibility(View.GONE);
                lbl_data_not_found.setVisibility(View.VISIBLE);
                lbl_data_not_found.setText("Error de conexión, vuelve a intentar");
                try {
                    Log.d("TabChallenges(getChallenges)", "Errors: " + error.getBody().toString());
                    Log.d("TabChallenges(getChallenges)", "Errors body: " + error.getBody().toString());
                } catch (Exception ex) {
                    Log.e("TabChallenges(getChallenges)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
                }
            }
        });
    }

    public void saveChallenges(JsonArray data){
        try {
            User user = userController.show();
            if(data.size() > 0) {
                List<Challenge> challenges = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    JsonObject json_challenge = data.get(i).getAsJsonObject();
                    Challenge challenge = new Gson().fromJson(json_challenge, Challenge.class);
                    if(user.getUser_id() == challenge.getPlayer_one() && challenge.isVisible_one()) {
                        challenges.add(challenge);
                    } else if(user.getUser_id() == challenge.getPlayer_two() && challenge.isVisible_two()) {
                        challenges.add(challenge);
                    }
                }
                progress_bar.setVisibility(View.GONE);
                setupRecycler(challenges);
            } else {
                progress_bar.setVisibility(View.GONE);
                lbl_data_not_found.setVisibility(View.VISIBLE);
            }
        } catch (JsonIOException e){
            Log.e("TabChallenges(saveChallenges)", "Error ex: " + e.getMessage());
        }
    }

    public void setupRecycler(List<Challenge> challenges){
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

}
