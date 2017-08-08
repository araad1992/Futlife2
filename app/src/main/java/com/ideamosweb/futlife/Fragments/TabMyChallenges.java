package com.ideamosweb.futlife.Fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.Challenge;
import com.ideamosweb.futlife.Models.Player;
import com.ideamosweb.futlife.Models.User;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.Service.Api;
import com.ideamosweb.futlife.Utils.ToastMessages;
import com.ideamosweb.futlife.Utils.Utils;

import java.util.ArrayList;
import java.util.List;
import az.plainpie.PieView;
import az.plainpie.animation.PieAngleAnimation;
import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Creado por Deimer Villa on 16/01/17.
 * Función:
 */
public class TabMyChallenges extends Fragment {

    private Context context;
    private UserController userController;
    private ToastMessages toast;
    private Utils utils;
    private List<Challenge> challenges;
    private static Player player_instance;
    private static boolean principal_instance;
    private int goals_favor;
    private int goals_against;

    //Elementos
    @Bind(R.id.progress_bar)
    ProgressBar progress_bar;
    @Bind(R.id.lbl_data_not_found)
    TextView lbl_data_not_found;
    @Bind(R.id.layout_statitics)
    LinearLayout layout_statitics;
    @Bind(R.id.lbl_title_matchs)
    TextView lbl_title_matchs;
    @Bind(R.id.layout_results_challenges)
    LinearLayout layout_results_challenges;
    @Bind(R.id.layout_percentage)
    LinearLayout layout_percentage;
    @Bind(R.id.lbl_percentage_victories)
    TextView lbl_percentage_victories;
    @Bind(R.id.pie_view_victories)
    PieView pie_view_victories;
    @Bind(R.id.lbl_percentage_defeats)
    TextView lbl_percentage_defeats;
    @Bind(R.id.pie_view_defeats)
    PieView pie_view_defeats;
    @Bind(R.id.lbl_pj) TextView lbl_pj;
    @Bind(R.id.lbl_pg) TextView lbl_pg;
    @Bind(R.id.lbl_pp) TextView lbl_pp;
    @Bind(R.id.lbl_gf) TextView lbl_gf;
    @Bind(R.id.lbl_gc) TextView lbl_gc;
    @Bind(R.id.txt_pj) TextView txt_pj;
    @Bind(R.id.txt_pg) TextView txt_pg;
    @Bind(R.id.txt_pp) TextView txt_pp;
    @Bind(R.id.txt_gf) TextView txt_gf;
    @Bind(R.id.txt_gc) TextView txt_gc;

    public static TabMyChallenges newInstance(boolean principal, Player player_adapter) {
        principal_instance = principal;
        player_instance = player_adapter;
        return new TabMyChallenges();
    }

    public TabMyChallenges() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frame_statistics, container, false);
        ButterKnife.bind(this, view);
        setupTab();
        return view;
    }

    public void setupTab(){
        context = getActivity().getApplicationContext();
        userController = new UserController(context);
        toast = new ToastMessages(this.getContext());
        utils = new Utils(context);
        challenges = new ArrayList<>();
        goals_favor = 0;
        goals_against = 0;
        setupLabelsFrame();
        getChallengesFromUser();
    }

    public void setupLabelsFrame(){
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        lbl_data_not_found.setTypeface(bebas_regular);
        lbl_title_matchs.setTypeface(bebas_regular);
        lbl_percentage_victories.setTypeface(bebas_regular);
        lbl_percentage_defeats.setTypeface(bebas_regular);
        lbl_pj.setTypeface(bebas_regular);
        lbl_pg.setTypeface(bebas_regular);
        lbl_pp.setTypeface(bebas_regular);
        lbl_gf.setTypeface(bebas_regular);
        lbl_gc.setTypeface(bebas_regular);
        txt_pj.setTypeface(bebas_regular);
        txt_pg.setTypeface(bebas_regular);
        txt_pp.setTypeface(bebas_regular);
        txt_gf.setTypeface(bebas_regular);
        txt_gc.setTypeface(bebas_regular);
    }

    public void getChallengesFromUser(){
        if(utils.connect()) {
            if(principal_instance){
                getChallenges(userController.show().getUser_id());
            } else {
                getChallenges(player_instance.getUser_id());
            }
        } else {
            toast.toastError("No se pudo detectar una conexión estable a internet.");
        }
    }

    public void getChallenges(int user_id){
        User user = userController.show();
        String token = "Bearer " + user.getToken();
        final String url = getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.showChallenges(token, user_id, new Callback<JsonObject>() {
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
                toast.toastError(error.getMessage());
                progress_bar.setVisibility(View.GONE);
                lbl_data_not_found.setVisibility(View.VISIBLE);
                lbl_data_not_found.setText("Error de conexión, vuelve a intentar");
                try {
                    Log.d("TabMyChallenges(getChallenges)", "Errors: " + error.getBody().toString());
                    Log.d("TabMyChallenges(getChallenges)", "Errors body: " + error.getBody().toString());
                } catch (Exception ex) {
                    Log.e("TabMyChallenges(getChallenges)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
                }
            }
        });
    }

    public void saveChallenges(JsonArray data){
        try {
            if(data.size() > 0) {
                for (int i = 0; i < data.size(); i++) {
                    JsonObject json_challenge = data.get(i).getAsJsonObject();
                    Challenge challenge = new Gson().fromJson(json_challenge, Challenge.class);
                    if(challenge.getState().equalsIgnoreCase("terminado")) {
                        challenges.add(challenge);
                    }
                }
                if(challenges.isEmpty()) {
                    progress_bar.setVisibility(View.GONE);
                    lbl_data_not_found.setVisibility(View.VISIBLE);
                } else {
                    setupPieChart();
                    createHistoryMatch();
                    layout_statitics.setVisibility(View.VISIBLE);
                    progress_bar.setVisibility(View.GONE);
                }
            } else {
                progress_bar.setVisibility(View.GONE);
                lbl_data_not_found.setVisibility(View.VISIBLE);
            }
        } catch (JsonIOException e){
            Log.e("TabMyChallenges(saveChallenges)", "Error ex: " + e.getMessage());
        }
    }

    public void createHistoryMatch(){
        for (int i = 0; i < challenges.size(); i++) {
            int player_one = challenges.get(i).getPlayer_one();
            int player_two = challenges.get(i).getPlayer_two();
            int score_one = challenges.get(i).getScore_player_one();
            int score_two = challenges.get(i).getScore_player_two();
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout layout_scores = (LinearLayout)inflater.inflate(R.layout.template_icon_results, null);
            RelativeLayout layout_score_one = (RelativeLayout)layout_scores.findViewById(R.id.layout_result_1);
            RelativeLayout layout_score_two = (RelativeLayout)layout_scores.findViewById(R.id.layout_result_2);
            if(principal_instance) {
                int user_id = userController.show().getUser_id();
                if(user_id == player_one) {
                    setElements(score_one, score_two, layout_score_one, layout_score_two);
                } else if(user_id == player_two) {
                    setElements(score_two, score_one, layout_score_one, layout_score_two);
                }
            } else {
                int user_id = player_instance.getUser_id();
                if(user_id == player_one) {
                    setElements(score_one, score_two, layout_score_one, layout_score_two);
                } else if(user_id == player_two) {
                    setElements(score_two, score_one, layout_score_one, layout_score_two);
                }
            }
            layout_results_challenges.addView(layout_scores);
            if(i == 4) break;
        }
    }

    public void setElements(int score_one, int score_two, View view_one, View view_two){
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        if(score_one > score_two) {
            view_two.setVisibility(View.GONE);
            TextView lbl_score_one = (TextView)view_one.findViewById(R.id.lbl_result_template_1);
            lbl_score_one.setText("V");
            lbl_score_one.setTypeface(bebas_regular);
        } else if(score_one < score_two) {
            view_one.setVisibility(View.GONE);
            TextView lbl_score_two = (TextView)view_two.findViewById(R.id.lbl_result_template_2);
            lbl_score_two.setText("D");
            lbl_score_two.setTypeface(bebas_regular);
        }
    }

    public void setupPieChart(){
        pie_view_victories.setPercentageBackgroundColor(ContextCompat.getColor(context, R.color.color_success));
        pie_view_defeats.setPercentageBackgroundColor(ContextCompat.getColor(context, R.color.color_errors));
        int total_games = challenges.size();
        int victories = 0;
        int defeats = 0;
        for (int i = 0; i < challenges.size(); i++) {
            Challenge challenge = challenges.get(i);
            if (isWinner(challenge.getPlayer_one(), challenge.getPlayer_two(), challenge.getScore_player_one(), challenge.getScore_player_two())) {
                victories++;
            } else {
                defeats++;
            }
        }
        setupInfoStatitics(total_games, victories, defeats);
        if(total_games > 0) {
            float percentage_victories = (float)((victories * 100) / total_games);
            float percentage_defeats = (float)((defeats * 100) / total_games);
            pie_view_victories.setPercentage(percentage_victories);
            pie_view_defeats.setPercentage(percentage_defeats);
            PieAngleAnimation anim_victories = new PieAngleAnimation(pie_view_victories);
            anim_victories.setDuration(1500);
            PieAngleAnimation anim_defeats = new PieAngleAnimation(pie_view_defeats);
            anim_defeats.setDuration(1500);
            pie_view_victories.startAnimation(anim_victories);
            pie_view_defeats.startAnimation(anim_defeats);
        } else {
            layout_percentage.setVisibility(View.GONE);
        }
    }

    public void setupInfoStatitics(int total_games, int total_wins, int total_losers) {
        getGoals();
        txt_pj.setText(String.valueOf(total_games));
        txt_pg.setText(String.valueOf(total_wins));
        txt_pp.setText(String.valueOf(total_losers));
        txt_gf.setText(String.valueOf(goals_favor));
        txt_gc.setText(String.valueOf(goals_against));
    }

    public boolean isWinner(int player_one, int player_two, int score_one, int score_two) {
        boolean is_winner = false;
        int user_id;
        if(principal_instance) {
            user_id = userController.show().getUser_id();
        } else {
            user_id = player_instance.getUser_id();
        }
        if(user_id == player_one) {
            if(score_one > score_two) {
                is_winner = true;
            }
        } else if(user_id == player_two) {
            if(score_two > score_one) {
                is_winner = true;
            }
        }
        return is_winner;
    }

    public void getGoals(){
        int user_id;
        if(principal_instance) {
            user_id = userController.show().getUser_id();
        } else {
            user_id = player_instance.getUser_id();
        }
        for (int i = 0; i < challenges.size(); i++) {
            if(user_id == challenges.get(i).getPlayer_one()) {
                goals_favor = goals_favor + challenges.get(i).getScore_player_one();
                goals_against = goals_against + challenges.get(i).getScore_player_two();
            } else if(user_id == challenges.get(i).getPlayer_two()) {
                goals_favor = goals_favor + challenges.get(i).getScore_player_two();
                goals_against = goals_against + challenges.get(i).getScore_player_one();
            }
        }
    }

}
