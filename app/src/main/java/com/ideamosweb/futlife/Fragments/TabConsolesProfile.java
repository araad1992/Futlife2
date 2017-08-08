package com.ideamosweb.futlife.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ideamosweb.futlife.Adapters.RowConsoleAdapter;
import com.ideamosweb.futlife.Controllers.PreferenceController;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.ConsolePreference;
import com.ideamosweb.futlife.Models.Game;
import com.ideamosweb.futlife.Models.GamePreference;
import com.ideamosweb.futlife.Models.Player;
import com.ideamosweb.futlife.Objects.ItemPreference;
import com.ideamosweb.futlife.R;
import com.melnykov.fab.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Creado por Deimer Villa on 16/02/17.
 * Función:
 */
public class TabConsolesProfile extends Fragment {

    private Context context;
    private UserController userController;
    private PreferenceController preferenceController;
    private static FloatingActionButton fab_instance;
    private static boolean principal_instance;
    private static Player player_instance;

    @Bind(R.id.layout_data_not_found)LinearLayout layout_data_not_found;
    @Bind(R.id.lbl_data_not_found)TextView lbl_data_not_found;
    @Bind(R.id.recycler_consoles_profile)RecyclerView recycler_consoles_profile;

    public static TabConsolesProfile newInstance(
            FloatingActionButton fab, boolean principal, Player player) {
        fab_instance = fab;
        principal_instance = principal;
        player_instance = player;
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
        if(principal_instance) {
            setupListUser();
        } else {
            setupListPlayer();
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

    public void setupListPlayer(){
        List<ConsolePreference> preferences = preferenceController.consoles(player_instance.getUser_id());
        if(preferences.isEmpty()) {
            layout_data_not_found.setVisibility(View.VISIBLE);
            recycler_consoles_profile.setVisibility(View.GONE);
        } else {
            List<ItemPreference> items = consolesPreference(preferences);
            RowConsoleAdapter adapter = new RowConsoleAdapter(context, items);
            recycler_consoles_profile.setLayoutManager(
                    new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            );
            recycler_consoles_profile.setAdapter(adapter);
            fab_instance.attachToRecyclerView(recycler_consoles_profile);
        }
    }

    public List<ItemPreference> consolesPreference(List<ConsolePreference> preferences){
        List<ItemPreference> items = new ArrayList<>();
        for (int i = 0; i < preferences.size(); i++) {
            ItemPreference item = new ItemPreference();
            item.setConsole_code(preferences.get(i).getCode());
            item.setConsole_id(preferences.get(i).getConsole_id());
            item.setName(preferences.get(i).getName());
            item.setAvatar(preferences.get(i).getAvatar());
            item.setGames(gamesPreference(player_instance.getUser_id(), preferences.get(i).getConsole_id()));
            items.add(item);
        }
        return items;
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
