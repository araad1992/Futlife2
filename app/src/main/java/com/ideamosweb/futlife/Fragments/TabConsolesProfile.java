package com.ideamosweb.futlife.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.ideamosweb.futlife.Adapters.RowConsoleAdapter;
import com.ideamosweb.futlife.Controllers.PreferenceController;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.ConsolePreference;
import com.ideamosweb.futlife.Models.Game;
import com.ideamosweb.futlife.Models.GamePreference;
import com.ideamosweb.futlife.Models.User;
import com.ideamosweb.futlife.Objects.ItemPreference;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.Service.Api;
import com.ideamosweb.futlife.Utils.ToastMessages;
import com.melnykov.fab.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Creado por Deimer Villa on 16/02/17.
 * Función:
 */
public class TabConsolesProfile extends Fragment {

    private Context context;
    private UserController userController;
    private PreferenceController preferenceController;
    private ToastMessages toast;
    private static FloatingActionButton fab_instance;
    private static boolean principal_instance;
    private static int user_id;

    @Bind(R.id.progress_bar)ProgressBar progress_bar;
    @Bind(R.id.layout_data_not_found)LinearLayout layout_data_not_found;
    @Bind(R.id.lbl_data_not_found)TextView lbl_data_not_found;
    @Bind(R.id.recycler_consoles_profile)RecyclerView recycler_consoles_profile;

    public static TabConsolesProfile newInstance(
            FloatingActionButton fab, boolean principal, int user_id_instance) {
        fab_instance = fab;
        principal_instance = principal;
        user_id = user_id_instance;
        return new TabConsolesProfile();
    }

    public TabConsolesProfile(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frame_consoles_profile, container, false);
        ButterKnife.bind(this, view);
        setupTab();
        return view;
    }

    public void setupTab(){
        context = getActivity().getApplicationContext();
        userController = new UserController(context);
        preferenceController = new PreferenceController(context);
        toast = new ToastMessages(context);
        if(principal_instance) {
            progress_bar.setVisibility(View.GONE);
            setupListUser();
        } else {
            updatePreferencesRival();
        }
    }

//****************************** Metodos para el usuario logueado ******************************//

    public void setupListUser(){
        int user_id = userController.show().getUser_id();
        List<ConsolePreference> preferences = preferenceController.consoles(user_id);
        if(preferences.isEmpty()) {
            layout_data_not_found.setVisibility(View.VISIBLE);
            lbl_data_not_found.setText("Aún no has completado tu registro de preferencias de juego.");
            recycler_consoles_profile.setVisibility(View.GONE);
        } else {
            List<ItemPreference> items = consolesPreferenceUser(user_id, preferences);
            RowConsoleAdapter adapter = new RowConsoleAdapter(context, items);
            recycler_consoles_profile.setLayoutManager(
                    new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            );
            recycler_consoles_profile.setAdapter(adapter);
            fab_instance.attachToRecyclerView(recycler_consoles_profile);
            recycler_consoles_profile.setVisibility(View.VISIBLE);
        }
    }

    public List<ItemPreference> consolesPreferenceUser(int user_id, List<ConsolePreference> preferences) {
        List<ItemPreference> items = new ArrayList<>();
        for (int i = 0; i < preferences.size(); i++) {
            ItemPreference item = new ItemPreference();
            item.setConsole_code(preferences.get(i).getCode());
            item.setConsole_id(preferences.get(i).getConsole_id());
            item.setName(preferences.get(i).getName());
            item.setAvatar(preferences.get(i).getAvatar());
            item.setGames(gamesPreference(user_id, preferences.get(i).getConsole_id()));
            items.add(item);
        }
        return items;
    }

//*********************************** Metodos para jugadores ***********************************//

    public void updatePreferencesRival(){
        final User user = userController.show();
        String token = "Bearer " + user.getToken();
        String url = getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.showPreference(token, user_id, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                if(success) {
                    JsonArray preferences = jsonObject.getAsJsonArray("preferences");
                    savePreferencesPlayer(preferences);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                progress_bar.setVisibility(View.GONE);
                toast.toastWarning("Imposible conectar a red.");
                try {
                    Log.d("ChatRoom(updatePreferencesRival)", "Errors body: " + error.getMessage());
                } catch (Exception ex) {
                    Log.e("ChatRoom(updatePreferencesRival)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
                }
            }
        });
    }

    public void savePreferencesPlayer(JsonArray array){
        try {
            List<ItemPreference> items = new ArrayList<>();
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
                    System.out.println("consola: " + console.toString());

                    ItemPreference item = new ItemPreference();
                    item.setConsole_id(console.getConsole_id());
                    item.setName(console.getName());
                    item.setAvatar(console.getAvatar());

                    JsonArray array_games = preference.getAsJsonArray("games");
                    List<Game> games = new ArrayList<>();
                    for (int j = 0; j < array_games.size(); j++) {
                        JsonObject json_game = array_games.get(j).getAsJsonObject();
                        Game game = new Gson().fromJson(json_game, Game.class);
                        games.add(game);
                        System.out.println("juegos:" + game.toString());
                    }
                    item.setGames(games);
                    items.add(item);
                }
                setupListPlayer(items);
            }
        } catch (JsonIOException e){
            Log.e("ChatRoom(savePreferencesPlayer)", "Error ex: " + e.getMessage());
        }
    }

    public void setupListPlayer(List<ItemPreference> items){
        if(items.isEmpty()) {
            layout_data_not_found.setVisibility(View.VISIBLE);
            recycler_consoles_profile.setVisibility(View.GONE);
        } else {
            progress_bar.setVisibility(View.GONE);
            RowConsoleAdapter adapter = new RowConsoleAdapter(context, items);
            recycler_consoles_profile.setLayoutManager(
                    new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            );
            recycler_consoles_profile.setAdapter(adapter);
            recycler_consoles_profile.setVisibility(View.VISIBLE);
            fab_instance.attachToRecyclerView(recycler_consoles_profile);
        }
    }

    public List<Game> gamesPreference(int user_id, int console_id){
        List<GamePreference> gamePreferences = preferenceController.games(user_id, console_id);
        List<Game> games = new ArrayList<>();
        for (int i = 0; i < gamePreferences.size(); i++) {
            Game game = new Game();
            game.setGame_id(gamePreferences.get(i).getGame_id());
            game.setName(gamePreferences.get(i).getName());
            game.setAvatar(gamePreferences.get(i).getThumbnail());
            game.setYear(gamePreferences.get(i).getYear());
            games.add(game);
        }
        return games;
    }

}
