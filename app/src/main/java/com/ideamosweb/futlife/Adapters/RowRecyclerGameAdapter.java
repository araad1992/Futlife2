package com.ideamosweb.futlife.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
public class RowRecyclerGameAdapter extends
        RecyclerView.Adapter<RowRecyclerGameAdapter.AdapterView> {

    private Context context;
    private List<GamePreference> games = new ArrayList<>();

    public RowRecyclerGameAdapter(Context context, List<GamePreference> games) {
        this.context = context;
        this.games = games;
    }

    @Override
    public AdapterView onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_card_recycler, parent, false);
        return new AdapterView(layoutView);
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    @Override
    public void onBindViewHolder(final AdapterView holder, int position) {
        GamePreference game = games.get(position);
        String name = game.getName();
        String avatar = game.getAvatar();
        String detail = game.getYear();
        int sum = position + 1;
        loadThumbnailPreference(holder.img_item_row, avatar);
        holder.lbl_count_row.setText(String.valueOf(sum));
        holder.lbl_name_item.setText(name);
        holder.lbl_detail_item.setText(detail);
        holder.img_arrow_consoles.setVisibility(View.GONE);
    }

    public void loadThumbnailPreference(ImageView img_preference, String avatar){
        Picasso.with(context)
                .load(avatar)
                .fit()
                .centerInside()
                .error(R.drawable.loading_game)
                .placeholder(R.drawable.loading_game)
                .into(img_preference);
    }

    public class AdapterView extends RecyclerView.ViewHolder {
        @Bind(R.id.img_item_row)ImageView img_item_row;
        @Bind(R.id.lbl_count_row)TextView lbl_count_row;
        @Bind(R.id.lbl_name_item)TextView lbl_name_item;
        @Bind(R.id.lbl_detail_item)TextView lbl_detail_item;
        @Bind(R.id.img_arrow_consoles)ImageView img_arrow_consoles;
        public AdapterView(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
