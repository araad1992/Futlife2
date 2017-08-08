package com.ideamosweb.futlife.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ideamosweb.futlife.Adapters.CoverFlowConsoleAdapter;
import com.ideamosweb.futlife.Controllers.ConsoleController;
import com.ideamosweb.futlife.Controllers.PreferenceController;
import com.ideamosweb.futlife.Models.Console;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.Utils.CustPagerTransformer;
import com.ideamosweb.futlife.Utils.MaterialDialog;

import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectConsole extends Activity {

    //Iniciadores
    private Context context;
    private ConsoleController consoleController;
    private PreferenceController preferenceController;
    private MaterialDialog dialog;

    //Elementos de la vista
    @Bind(R.id.lbl_title)TextView lbl_title;
    @Bind(R.id.lbl_subtitle)TextView lbl_subtitle;
    @Bind(R.id.view_pager)ViewPager view_pager;
    @Bind(R.id.lbl_title_list)TextView lbl_title_list;
    @Bind(R.id.layout_list_item)LinearLayout layout_list_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_console);
        ButterKnife.bind(this);
        setupActivity();
    }

    public void setupActivity(){
        context = this;
        consoleController = new ConsoleController(context);
        preferenceController = new PreferenceController(context);
        dialog = new MaterialDialog(context);
        setupLabels();
        setupPagerContainer();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void setupLabels(){
        Typeface bebas_bold = Typeface.createFromAsset(getAssets(), "fonts/bebas_neue_bold.ttf");
        Typeface bebas_regular = Typeface.createFromAsset(getAssets(), "fonts/bebas_neue_regular.ttf");
        lbl_title.setTypeface(bebas_bold);
        lbl_subtitle.setTypeface(bebas_regular);
        lbl_title_list.setTypeface(bebas_regular);
    }

    public void setupPagerContainer(){
        List<Console> consoles = consoleController.show();
        view_pager.setPageTransformer(true, new CustPagerTransformer(context));
        view_pager.setAdapter(new CoverFlowConsoleAdapter(context, consoles, layout_list_item));
        view_pager.setPageMargin(10);
        view_pager.setOffscreenPageLimit(100);
    }

    @OnClick(R.id.fab_next)
    public void next(){
        if(!(preferenceController.stockConsoles())) {
            dialog.dialogWarnings("¡Alto!",
                    "Antes de avanzar, debes seleccionar al menos una consola.");
        } else {
            startActivity(new Intent(SelectConsole.this, SelectGame.class));
            overridePendingTransition(R.anim.slide_open_translate, R.anim.slide_close_scale);
            finish();
        }
    }

}
