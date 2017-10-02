package com.ideamosweb.futlife.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hanks.library.AnimateCheckBox;
import com.ideamosweb.futlife.Controllers.PreferenceController;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.ConsolePreference;
import com.ideamosweb.futlife.Models.Game;
import com.ideamosweb.futlife.Models.GamePreference;
import com.ideamosweb.futlife.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

/**
 * Creado por Deimer Villa on 4/11/16.
 * Función:
 */
public class CoverFlowGameAdapter extends PagerAdapter {

    private Context context;
    private List<Game> games;
    private PreferenceController preferenceController;
    private UserController userController;

    public CoverFlowGameAdapter(Context context, List<Game> games) {
        this.context = context;
        this.games = games;
    }

    @Override
    public Object instantiateItem(ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_game, null);
        //Elementos de la tarjeta del juego
        CardView card_game = (CardView) view.findViewById(R.id.cardview_game);
        AnimateCheckBox checkbox = (AnimateCheckBox)view.findViewById(R.id.checkbox_game);
        ImageView img_game = (ImageView) view.findViewById(R.id.img_game);
        setupControllers();
        //Funciones de los elementos en la tarjeta
        setupOptionsCard(card_game, checkbox);
        setupStateGame(checkbox, position);
        setupCheckedGame(checkbox, position);
        setupImageGame(img_game, position);
        //Construccion de la tarjeta
        parent.addView(view);
        return view;
    }

    public void setupControllers(){
        preferenceController = new PreferenceController(context);
        userController = new UserController(context);
    }

    public void setupOptionsCard(CardView card_game, final AnimateCheckBox checkbox){
        card_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkbox.isChecked()){
                    checkbox.setChecked(false);
                } else {
                    checkbox.setChecked(true);
                }
            }
        });
    }

    public void setupStateGame(AnimateCheckBox checkbox, int position){
        int user_id = userController.show().getUser_id();
        int game_id = games.get(position).getGame_id();
        List<GamePreference> games = preferenceController.selectedGame(game_id, user_id);
        if(!games.isEmpty()) {
            checkbox.setChecked(true);
        }
    }

    public void setupCheckedGame(final AnimateCheckBox checkbox, final int position){
        checkbox.setOnCheckedChangeListener(new AnimateCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View buttonView, boolean isChecked) {
                if(isChecked) {
                    validateQuantityConsoles(position, checkbox);
                } else {
                    deactivateGames(position);
                }
            }
        });
    }

    public void validateQuantityConsoles(int position, AnimateCheckBox checkbox){
        List<ConsolePreference> consoles = preferenceController.consoles(userController.show().getUser_id());
        if(consoles.size() == 1) {
            int console_id = consoles.get(0).getConsole_id();
            Game game = games.get(position);
            setGamePreference(game, console_id, true);
        } else if(consoles.size() > 1) {
            dialogConsole(position, checkbox);
        }
    }

    public void deactivateGames(int position){
        int game_id = games.get(position).getGame_id();
        int user_id = userController.show().getUser_id();
        List<GamePreference> list = preferenceController.list(game_id, user_id);
        for (int i = 0; i < list.size(); i++) {
            GamePreference game = list.get(i);
            game.setActive(false);
            preferenceController.update(game);
            System.out.println(game.toString());
        }
    }

    public void setupImageGame(ImageView img_game, int position){
        Picasso.with(context)
                .load(games.get(position).getAvatar())
                .fit()
                .centerInside()
                .placeholder(R.drawable.loading_game)
                .error(R.drawable.loading_game)
                .into(img_game);
        img_game.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return games.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    public List<CheckBox> layoutOptions(LinearLayout layout){
        List<CheckBox> checkBoxes = new ArrayList<>();
        List<ConsolePreference> consoles = preferenceController.consoles(userController.show().getUser_id());
        for (int i = 0; i < consoles.size(); i++) {
            ConsolePreference console = consoles.get(i);
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            CheckBox checkBox = (CheckBox)inflater.inflate(R.layout.template_checkbox, null);
            checkBox.setText(console.getName());
            checkBox.setTextColor(ContextCompat.getColor(context, R.color.primaryText));
            checkBox.setTag(console.getConsole_id());
            checkBoxes.add(checkBox);
            layout.addView(checkBox);
        }
        return checkBoxes;
    }

    public void dialogConsole(final int position, final AnimateCheckBox checkBox){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_question, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        //elements of the dialog
        setTitleDialog(view, games.get(position).getName());
        LinearLayout layout_options = (LinearLayout)view.findViewById(R.id.layout_options_dialog);
        Button but_confirm = (Button)view.findViewById(R.id.but_ok_dialog);
        //setters
        final List<CheckBox> checkBoxes = layoutOptions(layout_options);
        but_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBoxes.size() > 0) {
                    if(notGamesSelects(checkBoxes)){
                        checkBox.setChecked(false);
                    } else {
                        selectedOptions(checkBoxes, position);
                    }
                    alertDialog.dismiss();
                }
            }
        });
        alertDialog.show();
    }

    public void setTitleDialog(View view, String title){
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        TextView lbl_title_dialog = (TextView)view.findViewById(R.id.lbl_description_dialog);
        lbl_title_dialog.setText(context.getString(R.string.dialog_description_question) + " " + title);
        lbl_title_dialog.setTypeface(bebas_regular);
    }

    public void selectedOptions(List<CheckBox> checkBoxes, int position){
        if(!checkBoxes.isEmpty()) {
            for (int i = 0; i < checkBoxes.size(); i++) {
                boolean active = false;
                int console_id = (int)checkBoxes.get(i).getTag();
                if(checkBoxes.get(i).isChecked()) {
                    active = true;
                }
                Game game = games.get(position);
                setGamePreference(game, console_id, active);
            }
        }
    }

    public void setGamePreference(Game game, int console_id, boolean active){
        int user_id = userController.show().getUser_id();
        GamePreference preference = preferenceController.existGame(game.getGame_id(), console_id, user_id);
        if(preference == null) {
            preference = new GamePreference();
            preference.setGame_id(game.getGame_id());
            preference.setUser_id(user_id);
            preference.setConsole_id(console_id);
            preference.setName(game.getName());
            preference.setYear(game.getYear());
            preference.setAvatar(game.getAvatar());
            preference.setThumbnail(game.getThumbnail());
            preference.setActive(active);
        } else {
            preference.setActive(active);
        }
        preferenceController.create(preference);
        System.out.println(preference.toString());
    }

    public boolean notGamesSelects(List<CheckBox> checkBoxes){
        boolean res = true;
        for (int i = 0; i < checkBoxes.size(); i++) {
            if(checkBoxes.get(i).isChecked()) {
                System.out.println("Si hay juegos");
                res = false;
                break;
            }
        }
        return res;
    }

}
