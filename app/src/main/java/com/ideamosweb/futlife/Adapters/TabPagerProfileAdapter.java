package com.ideamosweb.futlife.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.ideamosweb.futlife.Fragments.TabConsolesProfile;
import com.ideamosweb.futlife.Fragments.TabInformation;
import com.ideamosweb.futlife.Fragments.TabMyChallenges;
import com.ideamosweb.futlife.Models.Player;
import com.melnykov.fab.FloatingActionButton;
import java.util.List;

/**
 * Creado por Deimer Villa on 17/02/17.
 * Función:
 */
public class TabPagerProfileAdapter extends FragmentPagerAdapter {

    private List<String> tab_titles;
    private FloatingActionButton fab_adapter;
    private boolean principal_adapter;
    private Player player_adapter;

    public TabPagerProfileAdapter(
            FragmentManager fm, List<String> tabs,
            FloatingActionButton fab, boolean principal, Player player
    ) {
        super(fm);
        this.tab_titles = tabs;
        this.fab_adapter = fab;
        this.principal_adapter = principal;
        this.player_adapter = player;
    }

    @Override
    public int getCount() {
        return tab_titles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tab_titles.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        String title = tab_titles.get(position);
        Fragment f = null;
        switch (title){
            case "consolas":
                f = TabConsolesProfile.newInstance(fab_adapter, principal_adapter, player_adapter);
                break;
            case "información":
                f = TabInformation.newInstance(principal_adapter, player_adapter);
                break;
            case "estadísticas":
                f = TabMyChallenges.newInstance(principal_adapter, player_adapter);
                break;
        }
        return f;
    }
}
