package com.ideamosweb.futlife.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.ideamosweb.futlife.Controllers.ChallengeController;
import com.ideamosweb.futlife.Controllers.PlayerController;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.Challenge;
import com.ideamosweb.futlife.Models.Player;
import com.ideamosweb.futlife.Models.Report;
import com.ideamosweb.futlife.Models.User;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.EventBus.MessageBusReport;
import com.ideamosweb.futlife.EventBus.StationBus;
import com.ideamosweb.futlife.Utils.Utils;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Creado por Deimer Villa on 9/06/17.
 * Función:
 */
public class RowRecyclerReportAdapter extends RecyclerView.Adapter<RowRecyclerReportAdapter.AdapterView> {

    private Context context;
    private UserController userController;
    private PlayerController playerController;
    private ChallengeController challengeController;
    private Utils utils;
    private List<Report> reports = new ArrayList<>();
    private Typeface bebas_bold;
    private Typeface bebas_regular;

    public RowRecyclerReportAdapter(Context context, List<Report> reports) {
        this.context = context;
        this.reports = reports;
    }

    @Override
    public AdapterView onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_card_report, parent, false);
        return new AdapterView(layoutView);
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public class AdapterView extends RecyclerView.ViewHolder {
        @Bind(R.id.card_report)CardView card_report;
        @Bind(R.id.img_avatar_user)CircleImageView img_avatar_user;
        @Bind(R.id.lbl_username)TextView lbl_username;
        @Bind(R.id.lbl_date_reported)TextView lbl_date_reported;
        @Bind(R.id.lbl_amount_bet)TextView lbl_amount_bet;
        @Bind(R.id.img_state_report)ImageView img_state_report;
        @Bind(R.id.lbl_state_report)TextView lbl_state_report;
        @Bind(R.id.view_separe)View view_separe;
        public AdapterView(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onBindViewHolder(final AdapterView holder, int position) {
        userController = new UserController(context);
        playerController = new PlayerController(context);
        challengeController = new ChallengeController(context);
        utils = new Utils(context);
        bebas_bold = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_bold.ttf");
        bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        Report report = reports.get(position);
        setupAvatar(report, holder);
        setupLabels(report, holder);
        setupStateReport(report, holder);
        clickCardReport(report, holder);
        isLast(holder, position);
    }

    public void setupAvatar(Report report, AdapterView holder){
        Player player = setupPlayerRival(report);
        if(player != null) {
            holder.lbl_username.setText(player.getUsername());
            holder.lbl_username.setTypeface(bebas_bold);
            loadAvatarRival(holder.img_avatar_user, player.getThumbnail());
        } else {
            loadAvatarRival(holder.img_avatar_user, "");
        }
    }

    public void setupLabels(Report report, AdapterView holder){
        Challenge challenge = challengeController.show(report.getChallenge_id());
        String date = utils.setDatetimeFormat(challenge.getUpdated_at());
        holder.lbl_date_reported.setText(date);
        holder.lbl_amount_bet.setText("$" + Math.round(challenge.getInitial_value()));
        holder.lbl_date_reported.setTypeface(bebas_regular);
        holder.lbl_amount_bet.setTypeface(bebas_regular);
    }

    public Player setupPlayerRival(Report report){
        Player player = null;
        User user = userController.show();
        Challenge challenge = challengeController.show(report.getChallenge_id());
        if(challenge.getPlayer_one() != user.getUser_id()) {
            player = playerController.find(challenge.getPlayer_one());
        } else if(challenge.getPlayer_two() != user.getUser_id()) {
            player = playerController.find(challenge.getPlayer_two());
        }
        return player;
    }

    public void loadAvatarRival(CircleImageView avatar, String url_thumbnail){
        Picasso.with(context)
                .load(url_thumbnail)
                .fit()
                .placeholder(R.drawable.avatar_default)
                .error(R.drawable.avatar_default)
                .centerCrop()
                .into(avatar);
    }

    public void setupStateReport(Report report, AdapterView holder){
        String state = report.getState();
        holder.lbl_state_report.setText(state);
        holder.lbl_state_report.setTypeface(bebas_regular);
        switch (state) {
            case "Pendiente":
                Picasso.with(context).load(R.drawable.ic_pending_state).into(holder.img_state_report);
                break;
            case "Validando":
                Picasso.with(context).load(R.drawable.ic_validate_state).into(holder.img_state_report);
                break;
            case "Cerrado":
                Picasso.with(context).load(R.drawable.ic_close_state).into(holder.img_state_report);
                break;
        }
    }

    public void clickCardReport(final Report report, AdapterView holder){
        holder.card_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StationBus.getBus().post(new MessageBusReport(true, report.getReport_id()));
            }
        });
    }

    public void isLast(AdapterView holder, int position){
        int last = getItemCount()-1;
        if(position == last) {
            holder.view_separe.setVisibility(View.GONE);
        } else {
            holder.view_separe.setVisibility(View.VISIBLE);
        }
    }

}
