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
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ideamosweb.futlife.Adapters.RowRecyclerPlayerAdapter;
import com.ideamosweb.futlife.Controllers.PlayerController;
import com.ideamosweb.futlife.Controllers.PreferenceController;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.ConsolePreference;
import com.ideamosweb.futlife.Models.GamePreference;
import com.ideamosweb.futlife.Models.Player;
import com.ideamosweb.futlife.Models.User;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.Service.Api;
import com.ideamosweb.futlife.EventBus.MessageBusSearch;
import com.ideamosweb.futlife.EventBus.StationBus;
import com.ideamosweb.futlife.Utils.MaterialDialog;
import com.ideamosweb.futlife.Utils.ToastMessages;
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
 * Creado por Deimer Villa on 16/01/17.
 * Función:
 */
public class TabPlayers extends Fragment {

    private Context context;
    private UserController userController;
    private PlayerController playerController;
    private PreferenceController preferenceController;
    private MaterialDialog dialog;
    private ToastMessages toast;
    private Utils utils;

    //Elementos
    @Bind(R.id.recycler_players)
    RecyclerView recycler;
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout swipe_refresh;
    @Bind(R.id.lbl_data_not_found)
    TextView lbl_data_not_found;

    public static TabPlayers newInstance() {
        return new TabPlayers();
    }

    public TabPlayers(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frame_players, container, false);
        ButterKnife.bind(this, view);
        setupActivity();
        return view;
    }

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
    public void recievedChallenge(MessageBusSearch messageBusSearch){
        boolean active = messageBusSearch.isActive();
        String keyword = messageBusSearch.search();
        if(active) {
            if(!keyword.equalsIgnoreCase("")) {
                resultsInRecycler(keyword);
            }
        } else {
            List<Player> players = playerController.get();
            setupRecycler(players);
        }
    }

    public void setupActivity(){
        context = this.getContext();
        userController = new UserController(context);
        playerController = new PlayerController(context);
        preferenceController = new PreferenceController(context);
        dialog = new MaterialDialog(context);
        toast = new ToastMessages(context);
        utils = new Utils(context);
        setupSwipeRefresh();
        setupLabelNotFound();
        List<Player> players = playerController.get();
        setupRecycler(players);
    }

    public void setupSwipeRefresh(){
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPlayers();
            }
        });
        swipe_refresh.setColorSchemeResources(
                R.color.color_success,
                R.color.color_warnings,
                R.color.color_errors,
                R.color.iconsFacebook
        );
    }

    public void setupLabelNotFound(){
        lbl_data_not_found.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/bebas_neue_bold.ttf"));
    }

    public void loadPlayers(){
        if(utils.connect()) {
            getPlayers(true);
        } else {
            dialog.dialogWarnings("Error de conexión", "No se pudo detectar una conexión estable a internet.");
        }
    }

    public void setupRecycler(List<Player> players){
        recycler.removeAllViewsInLayout();
        RowRecyclerPlayerAdapter adapter = new RowRecyclerPlayerAdapter(context, players);
        recycler.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        );
        recycler.setAdapter(adapter);
        recycler.setHasFixedSize(true);
        recycler.setVisibility(View.VISIBLE);
    }

    public void resultsInRecycler(String keyword){
        List<Player> players = playerController.search(keyword);
        if(players.isEmpty()) {
            recycler.setVisibility(View.GONE);
            lbl_data_not_found.setVisibility(View.VISIBLE);
        } else {
            recycler.setVisibility(View.VISIBLE);
            lbl_data_not_found.setVisibility(View.GONE);
            RowRecyclerPlayerAdapter adapter = new RowRecyclerPlayerAdapter(context, players);
            recycler.setLayoutManager(
                    new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            );
            recycler.setAdapter(adapter);
            recycler.setHasFixedSize(true);
        }
    }

//*********************************** Metodos asincronicos ***********************************//

    public void getPlayers(final boolean flag) {
        User user = userController.show();
        String token = "Bearer " + user.getToken();
        int user_id = user.getUser_id();
        final String url = getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.getUsers(token, user_id, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                if (success) {
                    JsonArray data = jsonObject.getAsJsonArray("data");
                    savePlayers(data);
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
        if(retrofitError.getKind().equals(RetrofitError.Kind.NETWORK)){
            dialog.dialogErrors("Error de conexión", retrofitError.getMessage());
        } else {
            int status = retrofitError.getResponse().getStatus();
            if(status == 400) {
                String error = retrofitError.getBody().toString();
                JsonParser parser = new JsonParser();
                JsonObject jsonErrors = (JsonObject)parser.parse(error);
                String message = jsonErrors.get("error").getAsString();
                dialog.dialogWarnings("¡Alerta!", message);
            } else {
                String message = retrofitError.getMessage();
                dialog.dialogErrors("Error " + status, message);
            }
        }
    }

    public void savePlayers(JsonArray array){
        try {
            if(array.size() > 0) {
                for (int i = 0; i < array.size(); i++) {
                    JsonObject json = array.get(i).getAsJsonObject();
                    Player player = new Gson().fromJson(json, Player.class);
                    if(playerController.create(player) == 1){
                        if(json.has("preferences")){
                            int user_id = player.getUser_id();
                            JsonArray preferences = json.getAsJsonArray("preferences");
                            savePreferencesPlayers(preferences, user_id);
                        }
                        System.out.println(player.toString());
                    }
                }
                List<Player> players = playerController.get();
                setupRecycler(players);
                toast.toastSuccess("Jugadores cargados...");
            } else {
                toast.toastWarning("Sin jugadores disponibles...");
            }
        } catch (JsonIOException e){
            Log.e("TabPlayers(savePlayers)", "Error ex: " + e.getMessage());
        }
    }

    public void savePreferencesPlayers(JsonArray array, int user_id){
        try {
            if(array.size() > 0) {
                for (int i = 0; i < array.size(); i++) {
                    JsonObject preference = array.get(i).getAsJsonObject();
                    JsonObject json_console = preference.getAsJsonObject("console");
                    int preference_id = preference.get("id").getAsInt();
                    String player_id = "";
                    if(!preference.get("player_id").isJsonNull()){
                        player_id = preference.get("player_id").getAsString();
                    }
                    ConsolePreference console = new Gson().fromJson(json_console, ConsolePreference.class);
                    console.setPreference_id(preference_id);
                    console.setUser_id(user_id);
                    console.setPlayer_id(player_id);
                    if(preferenceController.create(console)){
                        System.out.println("consola: " + console.toString());
                        JsonArray array_games = preference.getAsJsonArray("games");
                        for (int j = 0; j < array_games.size(); j++) {
                            JsonObject json_game = array_games.get(j).getAsJsonObject();
                            GamePreference game = new Gson().fromJson(json_game, GamePreference.class);
                            game.setUser_id(user_id);
                            game.setConsole_id(console.getConsole_id());
                            if(preferenceController.create(game)) {
                                System.out.println("juegos:" + game.toString());
                            }
                        }
                    }
                }
            }
        } catch (JsonIOException e){
            Log.e("Timeline(savePreferencesPlayers)", "Error ex: " + e.getMessage());
        }
    }

//region Filtro de busqueda por items



//endregion

}
