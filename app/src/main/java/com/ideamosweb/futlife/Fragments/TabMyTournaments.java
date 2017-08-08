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

import com.ideamosweb.futlife.Adapters.RowRecyclerTournamentAdapter;
import com.ideamosweb.futlife.Adapters.SpaceItemView;
import com.ideamosweb.futlife.Models.Tournament;
import com.ideamosweb.futlife.R;
import com.melnykov.fab.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Creado por Deimer Villa on 13/12/16.
 * Función:
 */
public class TabMyTournaments extends Fragment {

    private Context context;
    private static FloatingActionButton fab_instance;

    //Elementos
    @Bind(R.id.recycler_my_tournaments)
    RecyclerView recycler;

    public static TabMyTournaments newInstance(FloatingActionButton fab_versus) {
        fab_instance = fab_versus;
        return new TabMyTournaments();
    }

    public TabMyTournaments(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frame_my_tournaments, container, false);
        ButterKnife.bind(this, view);
        setupActivity();
        return view;
    }

    public void setupActivity(){
        context = getActivity().getApplicationContext();
        setupRecycler();
    }

    public void setupRecycler(){
        List<Tournament> tournaments = getTournaments();
        RowRecyclerTournamentAdapter adapter = new RowRecyclerTournamentAdapter(context, tournaments);
        recycler.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        );
        recycler.setAdapter(adapter);
        SpaceItemView space = new SpaceItemView(1);
        recycler.addItemDecoration(space);
        fab_instance.attachToRecyclerView(recycler);
    }

    public List<Tournament> getTournaments(){
        List<Tournament> tournaments = new ArrayList<>();

        Tournament tournament5 = new Tournament();
        tournament5.setTournament_id(5);
        tournament5.setName("TORNEO CARNAVALERO");
        tournament5.setValue_inscription(30000);
        tournament5.setReward(100000);
        tournament5.setState(1);
        tournament5.setStart_date("NOVIEMBRE 12");
        tournament5.setStart_time("08:00 A.M.");
        tournament5.setStart_date("NOVIEMBRE 15");
        tournament5.setStart_time("12:00 A.M.");
        tournament5.setConsole_id(4);
        tournament5.setGame_id(1);
        tournament5.setMode_id(1);
        tournament5.setActive(true);
        tournament5.setSubscribed(false);
        tournaments.add(tournament5);

        Tournament tournament6 = new Tournament();
        tournament6.setTournament_id(6);
        tournament6.setName("TORNEO NAVIDEÑO");
        tournament6.setValue_inscription(30000);
        tournament6.setReward(100000);
        tournament6.setState(2);
        tournament6.setStart_date("NOVIEMBRE 12");
        tournament6.setStart_time("08:00 A.M.");
        tournament6.setStart_date("NOVIEMBRE 15");
        tournament6.setStart_time("12:00 A.M.");
        tournament6.setConsole_id(1);
        tournament6.setGame_id(3);
        tournament6.setMode_id(2);
        tournament6.setActive(true);
        tournament6.setSubscribed(true);
        tournaments.add(tournament6);

        Tournament tournament7 = new Tournament();
        tournament7.setTournament_id(7);
        tournament7.setName("TORNEO HALLOWEEN");
        tournament7.setValue_inscription(50000);
        tournament7.setReward(1500000);
        tournament7.setState(1);
        tournament7.setStart_date("NOVIEMBRE 12");
        tournament7.setStart_time("08:00 A.M.");
        tournament7.setStart_date("NOVIEMBRE 15");
        tournament7.setStart_time("12:00 A.M.");
        tournament7.setConsole_id(1);
        tournament7.setGame_id(3);
        tournament7.setMode_id(1);
        tournament7.setActive(true);
        tournament7.setSubscribed(true);
        tournaments.add(tournament7);

        return tournaments;
    }

}
