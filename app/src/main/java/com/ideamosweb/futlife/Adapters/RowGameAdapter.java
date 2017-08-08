package com.ideamosweb.futlife.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ideamosweb.futlife.Models.Console;
import com.ideamosweb.futlife.Models.Game;
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
public class RowGameAdapter extends RecyclerView.Adapter<RowGameAdapter.AdapterView> {

    private Context context;
    public List<Game> games = new ArrayList<>();
    public List<Console> consoles = new ArrayList<>();

    public RowGameAdapter(Context context, List<Game> games, List<Console> consoles) {
        this.context = context;
        this.games = games;
        this.consoles = consoles;
    }

    @Override
    public AdapterView onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_game_profile, parent, false);
        return new AdapterView(layoutView);
    }

    public class AdapterView extends RecyclerView.ViewHolder {
        @Bind(R.id.img_item_game)ImageView img_item_game;
        @Bind(R.id.lbl_title_game)TextView lbl_title_game;
        @Bind(R.id.layout_name_consoles)LinearLayout layout_name_consoles;
        public AdapterView(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getItemCount() {
        return consoles.size();
    }

    @Override
    public void onBindViewHolder(AdapterView holder, int position) {
        Console console = consoles.get(position);
        loadConsolesThumbnail(console.getConsole_id(), holder.img_item_game);
        holder.lbl_title_game.setText(console.getName());
        loadGamesPreferences(holder.layout_name_consoles);
    }

    public void loadConsolesThumbnail(int console_id, ImageView thumbnail){
        switch (console_id){
            case 1:
                Picasso.with(context).load(R.drawable.play3)
                        .fit().into(thumbnail);
                break;
            case 2:
                Picasso.with(context).load(R.drawable.play4)
                        .fit().into(thumbnail);
                break;
            case 3:
                Picasso.with(context).load(R.drawable.xbox360)
                        .fit().into(thumbnail);
                break;
            case 4:
                Picasso.with(context).load(R.drawable.xboxone)
                        .fit().into(thumbnail);
                break;
        }
    }

    public void loadGamesPreferences(LinearLayout layout){
        if(!games.isEmpty()){
            for (int i = 0; i < games.size(); i++) {
                TextView lbl_name_game = new TextView(context);
                lbl_name_game.setText(games.get(i).getName());
                lbl_name_game.setTextColor(ContextCompat.getColor(context, R.color.primaryText));
                layout.addView(lbl_name_game);
            }
        }
    }

}
