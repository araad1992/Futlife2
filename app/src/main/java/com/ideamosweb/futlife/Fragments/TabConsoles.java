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
import com.ideamosweb.futlife.Adapters.RowRecyclerConsoleAdapter;
import com.ideamosweb.futlife.Adapters.SpaceItemView;
import com.ideamosweb.futlife.Controllers.ConsoleController;
import com.ideamosweb.futlife.Controllers.PreferenceController;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.ConsolePreference;
import com.ideamosweb.futlife.R;
import com.melnykov.fab.FloatingActionButton;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Creado por Deimer Villa on 16/11/16.
 * Función:
 */
public class TabConsoles extends Fragment {

    private Context context;
    private UserController userController;
    private PreferenceController preferenceController;
    private static FloatingActionButton fab_instance;

    //Elementos
    @Bind(R.id.recycler_consoles)
    RecyclerView recycler;

    public static TabConsoles newInstance(FloatingActionButton fab_confirm) {
        fab_instance = fab_confirm;
        return new TabConsoles();
    }

    public TabConsoles(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frame_consoles, container, false);
        ButterKnife.bind(this, view);
        setupActivity();
        return view;
    }

    public void setupActivity(){
        context = getActivity().getApplicationContext();
        userController = new UserController(context);
        preferenceController = new PreferenceController(context);
        setupRecycler();
    }

    public void setupRecycler(){
        List<ConsolePreference> consoles = preferenceController.consoles(userController.show().getUser_id());
        RowRecyclerConsoleAdapter adapter = new RowRecyclerConsoleAdapter(context, consoles);
        recycler.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        );
        recycler.setAdapter(adapter);
        SpaceItemView space = new SpaceItemView(1);
        recycler.addItemDecoration(space);
        fab_instance.attachToRecyclerView(recycler);
    }


}
