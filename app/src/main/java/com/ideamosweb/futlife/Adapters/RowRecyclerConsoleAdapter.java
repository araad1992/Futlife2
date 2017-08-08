package com.ideamosweb.futlife.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.widget.Space;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ideamosweb.futlife.Controllers.PreferenceController;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.ConsolePreference;
import com.ideamosweb.futlife.Models.GamePreference;
import com.ideamosweb.futlife.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Creado por Deimer Villa on 16/11/16.
 * Función:
 */
public class RowRecyclerConsoleAdapter extends
        RecyclerView.Adapter<RowRecyclerConsoleAdapter.AdapterView> {

    private Context context;
    public List<ConsolePreference> consoles = new ArrayList<>();

    public RowRecyclerConsoleAdapter(Context context, List<ConsolePreference> consoles) {
        this.context = context;
        this.consoles = consoles;
    }

    @Override
    public AdapterView onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_card_recycler, parent, false);
        return new AdapterView(layoutView);
    }

    @Override
    public int getItemCount() {
        return consoles.size();
    }

    @Override
    public void onBindViewHolder(AdapterView holder, int position) {
        ConsolePreference console = consoles.get(position);
        int console_id = console.getConsole_id();
        String name = console.getName();
        String thumbnail = console.getThumbnail();
        int sum = position + 1;

        loadConsolesThumbnail(thumbnail, holder.img_item_row);
        clickDetails(holder.img_arrow_consoles, holder.layout_consoles_game);
        loadGames(console_id, holder.layout_consoles_game);

        holder.lbl_count_row.setText(String.valueOf(sum));
        holder.lbl_name_item.setText(name);
        holder.lbl_detail_item.setVisibility(View.GONE);
    }

    public void clickDetails(final ImageView img_console, final LinearLayout layout){
        img_console.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layout.getVisibility() == View.GONE) {
                    layout.setVisibility(View.VISIBLE);
                    Picasso.with(context).load(R.drawable.arrow_down_collapse)
                            .into(img_console);
                } else {
                    layout.setVisibility(View.GONE);
                    Picasso.with(context).load(R.drawable.arrow_next_collapse)
                            .into(img_console);
                }
            }
        });
    }

    public void loadGames(int console_id, LinearLayout layout){
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        UserController userController = new UserController(context);
        PreferenceController preferenceController = new PreferenceController(context);
        int user_id = userController.show().getUser_id();
        List<GamePreference> games = preferenceController.games(user_id, console_id);
        for (int i = 0; i < games.size(); i++) {
            int game_id = games.get(i).getGame_id();
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout layout_preferences = (LinearLayout)inflater.inflate(R.layout.template_row_games, null);
            RelativeLayout layout_games_1 = (RelativeLayout)layout_preferences.findViewById(R.id.layout_games_1);
            RelativeLayout layout_games_2 = (RelativeLayout)layout_preferences.findViewById(R.id.layout_games_2);
            if(game_id == 1 || game_id == 2) {
                layout_games_1.setVisibility(View.GONE);
                TextView lbl_name_game_2 = (TextView)layout_games_2.findViewById(R.id.lbl_game_template_2);
                lbl_name_game_2.setText(games.get(i).getName());
                lbl_name_game_2.setTypeface(bebas_regular);
            } else if (game_id == 3 || game_id == 4) {
                layout_games_2.setVisibility(View.GONE);
                TextView lbl_name_game_1 = (TextView)layout_games_1.findViewById(R.id.lbl_game_template_1);
                lbl_name_game_1.setText(games.get(i).getName());
                lbl_name_game_1.setTypeface(bebas_regular);
            }
            layout_preferences.setGravity(Gravity.CENTER);
            Space space = new Space(context);
            space.setMinimumHeight(12);
            layout.addView(layout_preferences);
            layout.addView(space);
        }
    }

    public void loadConsolesThumbnail(String avatar, ImageView thumbnail){
        Picasso.with(context)
                .load(avatar)
                .fit()
                .error(R.drawable.loading_console)
                .error(R.drawable.loading_console)
                .into(thumbnail);
    }

    public class AdapterView extends RecyclerView.ViewHolder {
        @Bind(R.id.img_item_row)ImageView img_item_row;
        @Bind(R.id.lbl_count_row)TextView lbl_count_row;
        @Bind(R.id.lbl_name_item)TextView lbl_name_item;
        @Bind(R.id.lbl_detail_item)TextView lbl_detail_item;
        @Bind(R.id.img_arrow_consoles)ImageView img_arrow_consoles;
        @Bind(R.id.layout_consoles_game)LinearLayout layout_consoles_game;
        public AdapterView(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
