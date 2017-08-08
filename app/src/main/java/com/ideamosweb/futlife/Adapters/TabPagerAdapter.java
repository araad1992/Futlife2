package com.ideamosweb.futlife.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.ideamosweb.futlife.Fragments.TabConsoles;
import com.ideamosweb.futlife.Fragments.TabGames;
import com.ideamosweb.futlife.Fragments.TabMyTournaments;
import com.ideamosweb.futlife.Fragments.TabTournaments;
import com.melnykov.fab.FloatingActionButton;
import java.util.List;

/**
 * Creado por Deimer Villa on 15/11/16.
 * Función:
 */
public class TabPagerAdapter extends FragmentPagerAdapter {

    private int page_count;
    private List<String> tab_titles;
    private FloatingActionButton fab_confirm;

    public TabPagerAdapter(FragmentManager fm, int pages, List<String> tabs, FloatingActionButton fab_confirm) {
        super(fm);
        this.page_count = pages;
        this.tab_titles = tabs;
        this.fab_confirm = fab_confirm;
    }

    @Override
    public int getCount() {
        return page_count;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = null;
        String title = tab_titles.get(position);
        switch (title){
            case "consolas":
                f = TabConsoles.newInstance(fab_confirm);
                break;
            case "juegos":
                f = TabGames.newInstance();
                break;
            case "torneos":
                f = TabTournaments.newInstance(fab_confirm);
                break;
            case "mis torneos":
                f = TabMyTournaments.newInstance(fab_confirm);
                break;
        }
        return f;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tab_titles.get(position);
    }

}
