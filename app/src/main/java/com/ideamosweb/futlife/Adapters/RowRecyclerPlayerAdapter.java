package com.ideamosweb.futlife.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ideamosweb.futlife.Models.Player;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.views.Profile;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Creado por Deimer Villa on 25/01/17.
 * Función:
 */
public class RowRecyclerPlayerAdapter extends RecyclerView.Adapter<RowRecyclerPlayerAdapter.AdapterView> {

    private Context context;
    private List<Player> players = new ArrayList<>();

    public RowRecyclerPlayerAdapter(Context context, List<Player> players) {
        this.context = context;
        this.players = players;
    }

    @Override
    public AdapterView onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_players_home, parent, false);
        return new AdapterView(layoutView);
    }

    @Override
    public void onBindViewHolder(final AdapterView holder, int position) {
        Player player = players.get(position);
        String avatar = player.getThumbnail();
        String username = player.getUsername();
        String fullname = player.getName();

        animateCardPlayer(holder.card_player);
        avatarProfilePlayer(holder.img_avatar_user, avatar);
        setTypeFaceItem(holder.lbl_username, username, true);
        setTypeFaceItem(holder.lbl_fullname, fullname, false);
        clickButDetails(holder.but_challenge, position, true);
        clickButDetails(holder.card_player, position, false);
        clickCardDetails(holder.img_avatar_user, position);
    }

    public void animateCardPlayer(CardView card_player){
        YoYo.with(Techniques.FadeInUp).duration(400).playOn(card_player);
    }

    public void clickCardDetails(CircleImageView avatar, final int position){
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Player player = players.get(position);
                Activity activity = (Activity)context;
                activity.startActivity(
                        new Intent(context, Profile.class)
                                .putExtra("tab_select", 0)
                                .putExtra("code", player.getCode())
                                .putExtra("principal", false));
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    public void clickButDetails(final View element, final int position, final boolean flag){
        final int[] time = {100};
        element.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Player player = players.get(position);
                if(flag) {
                    time[0] = 400;
                    YoYo.with(Techniques.RotateIn).duration(400).playOn(element);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SheetDialogPlayer sheet_dialog = SheetDialogPlayer.newInstance(player, context);
                        sheet_dialog.setCancelable(false);
                        sheet_dialog.show(((FragmentActivity)context).getSupportFragmentManager(), "Sheet Dialog");
                    }
                }, time[0]);
            }
        });
    }

    public void setTypeFaceItem(TextView lbl_item, String text, boolean item){
        if(item) {
            lbl_item.setText(text);
            Typeface bebas_bold = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_bold.ttf");
            lbl_item.setTypeface(bebas_bold);
        } else {
            lbl_item.setText(text);
            Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
            lbl_item.setTypeface(bebas_regular);
        }
    }

    public void avatarProfilePlayer(ImageView avatar, String avatar_player){
        if(avatar_player != null) {
            Picasso.with(context).load(avatar_player)
                    .fit()
                    .placeholder(R.drawable.avatar_default)
                    .error(R.drawable.avatar_default)
                    .centerCrop().into(avatar);
        } else {
            Picasso.with(context)
                    .load(R.drawable.avatar_default)
                    .fit().centerCrop().into(avatar);
        }
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public class AdapterView extends RecyclerView.ViewHolder {
        @Bind(R.id.card_player)CardView card_player;
        @Bind(R.id.img_avatar_user)CircleImageView img_avatar_user;
        @Bind(R.id.lbl_username)TextView lbl_username;
        @Bind(R.id.lbl_fullname)TextView lbl_fullname;
        @Bind(R.id.but_challenge)ImageButton but_challenge;
        public AdapterView(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
