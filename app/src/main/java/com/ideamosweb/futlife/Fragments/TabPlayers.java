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
import com.ideamosweb.futlife.Adapters.RowRecyclerPlayerAdapter;
import com.ideamosweb.futlife.Controllers.UserController;
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
import java.util.ArrayList;
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

    //Componentes
    private Context context;
    private UserController userController;
    private MaterialDialog dialog;
    private ToastMessages toast;
    private Utils utils;

    //Elementos de control
    private RowRecyclerPlayerAdapter adapter;
    private List<Player> players;

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
        //boolean active = messageBusSearch.isActive();
        //String keyword = messageBusSearch.search();
        //if(active) {
        //    if(!keyword.equalsIgnoreCase("")) {
        //        resultsInRecycler(keyword);
        //    }
        //} else {
        //    List<Player> players = playerController.get();
        //    setupRecycler(players, false);
        //}
    }

    public void setupActivity(){
        context = this.getContext();
        userController = new UserController(context);
        dialog = new MaterialDialog(context);
        toast = new ToastMessages(context);
        utils = new Utils(context);

        adapter = null;
        players = new ArrayList<>();
        swipe_refresh.setRefreshing(true);
        getPlayers(true);
        setupSwipeRefresh();
        setupLabelNotFound();
        configureRecyclerView();
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

    private void configureRecyclerView() {
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0) {
                    swipe_refresh.setRefreshing(true);
                    getPlayers(true);
                }
            }
        });
    }

    public void setupRecycler(boolean flag){
        recycler.removeAllViewsInLayout();
        adapter = new RowRecyclerPlayerAdapter(context, players);
        recycler.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        );
        recycler.setAdapter(adapter);
        recycler.setHasFixedSize(false);
        if(flag) recycler.scrollToPosition(players.size() - 1);
        recycler.setVisibility(View.VISIBLE);
    }

    //public void resultsInRecycler(String keyword){
    //    if(players.isEmpty()) {
    //        recycler.setVisibility(View.GONE);
    //        lbl_data_not_found.setVisibility(View.VISIBLE);
    //    } else {
    //        recycler.setVisibility(View.VISIBLE);
    //        lbl_data_not_found.setVisibility(View.GONE);
    //        RowRecyclerPlayerAdapter adapter = new RowRecyclerPlayerAdapter(context, players);
    //        recycler.setLayoutManager(
    //                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
    //        );
    //        recycler.setAdapter(adapter);
    //        recycler.setHasFixedSize(true);
    //    }
    //}

//*********************************** Metodos asincronicos ***********************************//

    public void getPlayers(final boolean flag) {
        User user = userController.show();
        String token = "Bearer " + user.getToken();
        int user_id = user.getUser_id();
        int skip = players.size();
        final String url = getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.getUsers(token, user_id, skip, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                if (success) {
                    JsonArray data = jsonObject.getAsJsonArray("data");
                    processPlayers(data, flag);
                }
                if(flag)swipe_refresh.setRefreshing(false);
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
                String message = "No hay más jugadores";
                System.out.println(message);
                //toast.toastWarning(message);
            } else {
                String message = retrofitError.getMessage();
                dialog.dialogErrors("Error " + status, message);
            }
        }
    }

    public void processPlayers(JsonArray array, boolean flag) {
        try {
            if(array.size() > 0) {
                for (int i = 0; i < array.size(); i++) {
                    JsonObject json = array.get(i).getAsJsonObject();
                    Player player = new Gson().fromJson(json, Player.class);
                    if(!players.contains(player)) {
                        players.add(player);
                    }
                }
                setupRecycler(flag);
                toast.toastSuccess("Jugadores cargados...");
            } else {
                toast.toastWarning("Sin jugadores disponibles...");
            }
        } catch (JsonIOException e){
            Log.e("TabPlayers(savePlayers)", "Error ex: " + e.getMessage());
        }
    }

}
