package com.ideamosweb.futlife.Fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.ideamosweb.futlife.Adapters.RowRecyclerChallengeAdapter;
import com.ideamosweb.futlife.Controllers.ChallengeController;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.Challenge;
import com.ideamosweb.futlife.Models.User;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.Service.Api;
import com.ideamosweb.futlife.EventBus.MessageBusChallenge;
import com.ideamosweb.futlife.EventBus.StationBus;
import com.ideamosweb.futlife.Utils.MaterialDialog;
import com.ideamosweb.futlife.Utils.Utils;
import com.squareup.otto.Subscribe;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Creado por Deimer Villa on 26/01/17.
 * Función:
 */
public class TabChallengesLive extends Fragment {

    private Context context;
    private UserController userController;
    private ChallengeController challengeController;
    private MaterialDialog dialog;
    private Utils utils;

    //Elementos
    @Bind(R.id.marker_progress)
    ProgressBar progress_bar;
    @Bind(R.id.recycler_challenges_live)
    RecyclerView recycler;
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout swipe_refresh;
    @Bind(R.id.lbl_data_not_found)
    TextView lbl_data_not_found;

    public static TabChallengesLive newInstance() {
        return new TabChallengesLive();
    }

    public TabChallengesLive() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frame_challenges_live, container, false);
        ButterKnife.bind(this, view);
        setupActivity();
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
            getOpenChallenges(false);
        }
    }

    public void setupActivity(){
        context = this.getContext();
        userController = new UserController(context);
        challengeController = new ChallengeController(context);
        dialog = new MaterialDialog(context);
        utils = new Utils(context);
        loadChallengers();
        setupSwipeRefresh();
        lbl_data_not_found.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf"));
    }

    public void loadChallengers(){
        if(utils.connect()) {
            getOpenChallenges(false);
        } else {
            dialog.dialogWarnings("Error de conexión", "No se pudo detectar una conexión estable a internet.");
        }
    }

    public void setupSwipeRefresh(){
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOpenChallenges(true);
            }
        });
        swipe_refresh.setColorSchemeResources(
                R.color.color_success,
                R.color.color_warnings,
                R.color.color_errors,
                R.color.iconsFacebook
        );
    }

    public void setupRecycler(List<Challenge> challenges){
        recycler.removeAllViewsInLayout();
        RowRecyclerChallengeAdapter adapter = new RowRecyclerChallengeAdapter(context, challenges);
        recycler.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        );
        recycler.setAdapter(adapter);
        recycler.setHasFixedSize(true);
        recycler.setVisibility(View.VISIBLE);
        progress_bar.setVisibility(View.GONE);
        lbl_data_not_found.setVisibility(View.GONE);
    }

    public void getOpenChallenges(final boolean flag){
        User user = userController.show();
        String token = "Bearer " + user.getToken();
        int user_id = user.getUser_id();
        final String url = getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.getOpenChallenges(token, user_id, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                if (success) {
                    JsonArray data = jsonObject.getAsJsonArray("data");
                    toListChallenges(data);
                }
                if(flag) swipe_refresh.setRefreshing(false);
            }
            @Override
            public void failure(RetrofitError error) {
                if(flag) swipe_refresh.setRefreshing(false);
                errorsRequest(error);
                try {
                    Log.d("TabPlayers(getPlayers)", "Errors: " + error.getBody().toString());
                    Log.d("TabPlayers(getPlayers)", "Errors body: " + error.getBody().toString());
                } catch (Exception ex) {
                    Log.e("TabPlayers(getPlayers)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
                }
            }
        });
    }

    public void errorsRequest(RetrofitError retrofitError){
        progress_bar.setVisibility(View.GONE);
        if(retrofitError.getKind().equals(RetrofitError.Kind.NETWORK)){
            dialog.dialogErrors("Error de conexión", retrofitError.getMessage());
        } else {
            int status = retrofitError.getResponse().getStatus();
            if(status == 404) {
                List<Challenge> challenges = challengeController.get();
                if(challenges.isEmpty()) {
                    recycler.setVisibility(View.GONE);
                    lbl_data_not_found.setVisibility(View.VISIBLE);
                } else {
                    setupRecycler(challenges);
                }
            } else {
                String message = retrofitError.getMessage();
                dialog.dialogErrors("Error " + status, message);
            }
        }
    }

    public void toListChallenges(JsonArray array){
        try {
            List<Challenge> challenges = challengeController.get();
            if(array.size() > 0) {
                for (int i = 0; i < array.size(); i++) {
                    JsonObject json = array.get(i).getAsJsonObject();
                    Challenge challenge = new Gson().fromJson(json, Challenge.class);
                    challenges.add(challenge);
                }
                dataChallenge(challenges);
            }
            setupRecycler(challenges);
        } catch (JsonIOException e){
            Log.e("TabChallengesLive(toListChallenges)", "Error ex: " + e.getMessage());
        }
    }

    public void dataChallenge(List<Challenge> challenges) {
        for (int i = 0; i < challenges.size(); i++) {
            System.out.println("Challenge: " + challenges.get(i).toString());
        }
    }

}
