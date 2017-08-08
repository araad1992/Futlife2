package com.ideamosweb.futlife.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ideamosweb.futlife.Fragments.TabChallenges;
import com.ideamosweb.futlife.Fragments.TabChallengesLive;
import com.ideamosweb.futlife.Fragments.TabPlayers;
import java.util.List;

/**
 * Creado por Deimer Villa on 22/02/17.
 * Función:
 */
public class TabPagerTimelineAdapter extends FragmentPagerAdapter {

    private List<String> tab_titles;

    public TabPagerTimelineAdapter(FragmentManager fm, List<String> tabs) {
        super(fm);
        this.tab_titles = tabs;
    }

    @Override
    public int getCount() {
        return tab_titles.size();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = null;
        String title = tab_titles.get(position);
        switch (title){
            case "mis retos":
                f = TabChallenges.newInstance();
                break;
            case "jugadores":
                f = TabPlayers.newInstance();
                break;
            case "¡únete!":
                f = TabChallengesLive.newInstance();
                break;
        }
        return f;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tab_titles.get(position);
    }

}
