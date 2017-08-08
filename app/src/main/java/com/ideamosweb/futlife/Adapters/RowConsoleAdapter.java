package com.ideamosweb.futlife.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.widget.Space;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.flexbox.FlexboxLayout;
import com.ideamosweb.futlife.Controllers.ConsoleController;
import com.ideamosweb.futlife.Controllers.PlatformController;
import com.ideamosweb.futlife.Models.Game;
import com.ideamosweb.futlife.Objects.ItemPreference;
import com.ideamosweb.futlife.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Creado por Deimer Villa on 17/02/17.
 * Función:
 */
public class RowConsoleAdapter extends RecyclerView.Adapter<RowConsoleAdapter.AdapterView> {

    private Context context;
    public List<ItemPreference> items = new ArrayList<>();

    public RowConsoleAdapter(Context context, List<ItemPreference> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public AdapterView onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_console_profile, parent, false);
        return new AdapterView(layoutView);
    }

    public class AdapterView extends RecyclerView.ViewHolder {
        @Bind(R.id.img_item_console)ImageView img_item_console;
        @Bind(R.id.lbl_title_console)TextView lbl_title_console;
        @Bind(R.id.lbl_subtitle_console)TextView lbl_subtitle_console;
        @Bind(R.id.layout_name_games)FlexboxLayout layout_games;
        public AdapterView(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onBindViewHolder(AdapterView holder, int position) {
        ItemPreference item = items.get(position);
        loadConsolesThumbnail(item, holder.img_item_console);
        loadDataConsoles(holder.lbl_title_console, holder.lbl_subtitle_console, item);
        loadGamesPreferences(item, holder.layout_games);
    }

    public void loadConsolesThumbnail(ItemPreference item, ImageView thumbnail){
        int console_id = item.getConsole_id();
        switch (console_id){
            case 1:
                Picasso.with(context).load(R.drawable.xbox360)
                        .centerInside().fit().into(thumbnail);
                break;
            case 2:
                Picasso.with(context).load(R.drawable.xboxone)
                        .centerInside().fit().into(thumbnail);
                break;
            case 3:
                Picasso.with(context).load(R.drawable.play3)
                        .centerInside().fit().into(thumbnail);
                break;
            case 4:
                Picasso.with(context).load(R.drawable.play4)
                        .centerInside().fit().into(thumbnail);
                break;
        }
    }

    public void loadDataConsoles(TextView title, TextView subtitle, ItemPreference item){
        ConsoleController consoleController = new ConsoleController(context);
        PlatformController platformController = new PlatformController(context);
        title.setText(item.getName());
        subtitle.setText(platformController.show(consoleController.show(item.getConsole_id()).getPlatform_id()).getName());
        Typeface bebas_bold = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_bold.ttf");
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        title.setTypeface(bebas_regular);
        subtitle.setTypeface(bebas_bold);
    }

    public void loadGamesPreferences(ItemPreference item, FlexboxLayout layout){
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        List<Game> games = item.getGames();
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

            Space space = new Space(context);
            space.setMinimumWidth(12);
            layout.addView(layout_preferences);
            layout.addView(space);
        }
    }

}
