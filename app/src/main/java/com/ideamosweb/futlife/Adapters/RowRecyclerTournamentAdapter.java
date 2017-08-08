package com.ideamosweb.futlife.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ideamosweb.futlife.Controllers.ConsoleController;
import com.ideamosweb.futlife.Controllers.GameController;
import com.ideamosweb.futlife.Models.Tournament;
import com.ideamosweb.futlife.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Creado por Deimer Villa on 14/12/16.
 * Función:
 */
public class RowRecyclerTournamentAdapter extends RecyclerView.Adapter<RowRecyclerTournamentAdapter.AdapterView> {

    private Context context;
    private List<Tournament> tournaments = new ArrayList<>();

    private ConsoleController consoleController;
    private GameController gameController;

    public RowRecyclerTournamentAdapter(Context context, List<Tournament> tournaments) {
        this.context = context;
        this.tournaments = tournaments;
    }

    @Override
    public AdapterView onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_card_tournament, parent, false);
        return new AdapterView(layoutView);
    }

    @Override
    public void onBindViewHolder(final AdapterView holder, int position) {
        setupContext();
        Tournament tournament = tournaments.get(position);
        String name = tournament.getName();
        int console_id = tournament.getConsole_id();
        int game_id = tournament.getGame_id();

        colorConsole(holder.bar_vertical_color, console_id);
        imageGameTournament(holder.img_cover_game, game_id);

        setTypeFaceName(holder.lbl_name_tournament, name);
        imageConsoleThumbnail(holder.img_console_tournament, console_id);
        showDetailsTournament(holder.layout_details_tournament, tournament);
        holder.card_tournament.setTag(tournament.getCode());
    }

    public void setupContext() {
        consoleController = new ConsoleController(context);
        gameController = new GameController(context);
    }

    public void setTypeFaceName(TextView lbl_name, String name){
        lbl_name.setText(name);
        Typeface bebas_bold = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_bold.ttf");
        lbl_name.setTypeface(bebas_bold);
    }

    public void colorConsole(View bar_color, int console_id){
        switch (console_id) {
            case 1:
                bar_color.setBackgroundColor(ContextCompat.getColor(context, R.color.iconsAccent));
                break;
            case 2:
                bar_color.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
                break;
            case 3:
                bar_color.setBackgroundColor(ContextCompat.getColor(context, R.color.divider));
                break;
            case 4:
                bar_color.setBackgroundColor(ContextCompat.getColor(context, R.color.color_errors));
                break;
        }
    }

    public void imageGameTournament(ImageView img_cover_game, int game_id){
        String image_game = gameController.find(game_id).getAvatar();
        Picasso.with(context).load(image_game).centerCrop().fit().into(img_cover_game);
    }

    public void imageConsoleThumbnail(ImageView img_console, int console_id){
        String name = consoleController.find(console_id).getName();
        switch (name) {
            case "XBOX 360":
                Picasso.with(context).load(R.drawable.thumbnail_360).into(img_console);
                break;
            case "XBOX ONE":
                Picasso.with(context).load(R.drawable.thumbnail_one).into(img_console);
                break;
            case "PS3":
                Picasso.with(context).load(R.drawable.thumbnail_ps3).into(img_console);
                break;
            case "PS4":
                Picasso.with(context).load(R.drawable.thumbnail_ps4).into(img_console);
                break;
        }
    }

    public void showDetailsTournament(LinearLayout layout_details, Tournament tournament){
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Rewards
        View layout_reward = inflater.inflate(R.layout.template_row_textview, null);
        ImageView img_reward = (ImageView)layout_reward.findViewById(R.id.img_icon_template);
        TextView lbl_reward = (TextView)layout_reward.findViewById(R.id.lbl_text_template);
        Picasso.with(context).load(R.drawable.icon_reward).into(img_reward);
        lbl_reward.setText("$" + Float.toString(tournament.getReward()) + " COP");
        lbl_reward.setTextColor(ContextCompat.getColor(context, R.color.primaryTextReward));
        lbl_reward.setTypeface(bebas_regular);
        layout_details.addView(layout_reward);
        //Mode tournament
        View layout_mode = inflater.inflate(R.layout.template_row_textview, null);
        ImageView img_mode = (ImageView)layout_mode.findViewById(R.id.img_icon_template);
        TextView lbl_mode = (TextView)layout_mode.findViewById(R.id.lbl_text_template);
        setIconModeGame(tournament.getMode_id(), img_mode, lbl_mode);
        lbl_mode.setTextColor(ContextCompat.getColor(context, R.color.icons));
        lbl_mode.setTypeface(bebas_regular);
        layout_details.addView(layout_mode);
        //State
        View layout_state = inflater.inflate(R.layout.template_row_textview, null);
        ImageView img_state = (ImageView)layout_state.findViewById(R.id.img_icon_template);
        TextView lbl_state = (TextView)layout_state.findViewById(R.id.lbl_text_template);
        setIconStateTournament(tournament.getState(), img_state, lbl_state);
        setIconModeGame(tournament.getMode_id(), img_mode, lbl_mode);
        lbl_state.setTypeface(bebas_regular);
        layout_details.addView(layout_state);
    }

    public void setIconModeGame(int mode_game, ImageView img_mode, TextView lbl_mode){
        switch (mode_game) {
            case 1:
                img_mode.setPadding(6,6,6,6);
                Picasso.with(context).load(R.drawable.icon_playoff).into(img_mode);
                lbl_mode.setText("MODO PLAYOFF");
                break;
            case 2:
                Picasso.with(context).load(R.drawable.icon_groups).into(img_mode);
                lbl_mode.setText("GRUPOS Y PLAYOFF");
                break;
        }
    }

    public void setIconStateTournament(int state, ImageView img_mode, TextView lbl_mode){
        switch (state) {
            case 1:
                img_mode.setPadding(2,2,2,2);
                Picasso.with(context).load(R.drawable.icon_enabled).into(img_mode);
                lbl_mode.setText("DISPONIBLE");
                lbl_mode.setTextColor(ContextCompat.getColor(context, R.color.color_success));
                break;
            case 2:
                img_mode.setPadding(2,2,2,2);
                Picasso.with(context).load(R.drawable.icon_in_game).into(img_mode);
                lbl_mode.setText("EN JUEGO");
                lbl_mode.setTextColor(ContextCompat.getColor(context, R.color.primaryTextDisable));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return tournaments.size();
    }

    public class AdapterView extends RecyclerView.ViewHolder {
        @Bind(R.id.card_tournament)CardView card_tournament;
        @Bind(R.id.bar_vertical_color)View bar_vertical_color;
        @Bind(R.id.img_cover_game)ImageView img_cover_game;
        @Bind(R.id.lbl_name_tournament)TextView lbl_name_tournament;
        @Bind(R.id.layout_details_tournament)LinearLayout layout_details_tournament;
        @Bind(R.id.img_console_tournament)ImageView img_console_tournament;
        public AdapterView(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
