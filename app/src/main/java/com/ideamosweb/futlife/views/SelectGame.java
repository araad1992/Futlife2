package com.ideamosweb.futlife.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.widget.TextView;
import com.ideamosweb.futlife.Adapters.CoverFlowGameAdapter;
import com.ideamosweb.futlife.Controllers.GameController;
import com.ideamosweb.futlife.Controllers.PreferenceController;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.Game;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.Utils.CustPagerTransformer;
import com.ideamosweb.futlife.Utils.MaterialDialog;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectGame extends Activity {

    //Iniciadores
    private Context context;
    private UserController userController;
    private GameController gameController;
    private PreferenceController preferenceController;
    private MaterialDialog dialog;
    private boolean is_edit;

    //Elementos de la vista
    @Bind(R.id.lbl_title)TextView lbl_title;
    @Bind(R.id.lbl_subtitle)TextView lbl_subtitle;
    @Bind(R.id.view_pager)ViewPager view_pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_game);
        ButterKnife.bind(this);
        setupActivity();
    }

    public void setupActivity(){
        context = this;
        userController = new UserController(context);
        gameController = new GameController(context);
        preferenceController = new PreferenceController(context);
        dialog = new MaterialDialog(context);
        is_edit = false;
        getParameters();
        setupLabels();
        setupPagerContainer();
    }

    public void getParameters() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            is_edit = bundle.getBoolean("is_edit");
            System.out.println("is_edit: " + is_edit);
        }
    }

    public void setupLabels(){
        Typeface bebas_regular = Typeface.createFromAsset(getAssets(), "fonts/bebas_neue_regular.ttf");
        Typeface bebas_bold = Typeface.createFromAsset(getAssets(), "fonts/bebas_neue_bold.ttf");
        lbl_title.setTypeface(bebas_bold);
        lbl_subtitle.setTypeface(bebas_regular);
    }

    public void setupPagerContainer(){
        List<Game> consoles = gameController.show();
        view_pager.setPageTransformer(true, new CustPagerTransformer(context));
        view_pager.setAdapter(new CoverFlowGameAdapter(context, consoles));
        view_pager.setPageMargin(70);
        view_pager.setOffscreenPageLimit(100);
    }

    @OnClick(R.id.fab_next)
    public void next(){
        if(!preferenceController.stockGames(userController.show().getUser_id())) {
            dialog.dialogWarnings("¡Alto!",
                    "Antes de avanzar, debes seleccionar al menos un juego.");
        } else {
            Intent intent = new Intent(SelectGame.this, Summary.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("is_edit", is_edit);
            intent.putExtras(bundle);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_open_translate, R.anim.slide_close_scale);
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                startActivity(new Intent(SelectGame.this, SelectConsole.class));
                overridePendingTransition(R.anim.slide_open_translate, R.anim.slide_close_scale);
                finish();
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

}
