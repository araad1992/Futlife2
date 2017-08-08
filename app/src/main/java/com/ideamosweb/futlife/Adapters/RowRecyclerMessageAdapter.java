package com.ideamosweb.futlife.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.Message;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.Utils.Utils;

import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Creado por Deimer Villa on 18/04/17.
 * Función:
 */
public class RowRecyclerMessageAdapter extends RecyclerView.Adapter<RowRecyclerMessageAdapter.AdapterView> {

    private UserController userController;
    private Utils utils;

    private Context context;
    private List<Message> messages = new ArrayList<>();

    public RowRecyclerMessageAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public AdapterView onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_message_in, parent, false);
        return new AdapterView(layoutView);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class AdapterView extends RecyclerView.ViewHolder {
        @Bind(R.id.card_message)CardView card_message;
        @Bind(R.id.layout_message_chat)LinearLayout layout_message_chat;
        @Bind(R.id.lbl_text_message)TextView lbl_text_message;
        @Bind(R.id.lbl_time_message)TextView lbl_time_message;
        public AdapterView(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onBindViewHolder(final AdapterView holder, int position) {
        userController = new UserController(context);
        utils = new Utils(context);
        Message message = messages.get(position);
        int message_id = message.getMessage_id();
        int from_user = message.getFrom_user();
        setupCardMessage(holder, message_id, from_user);
        setupLabels(holder.lbl_text_message, holder.lbl_time_message, message);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) holder.card_message.getBackground().setAlpha(0);
        else holder.card_message.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
    }

    public void setupCardMessage(AdapterView holder, int message_id, int from_user){
        holder.layout_message_chat.setTag(message_id);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.card_message.getLayoutParams();
        FrameLayout.LayoutParams frame_params = (FrameLayout.LayoutParams)holder.layout_message_chat.getLayoutParams();
        if(from_user == 0) {
            holder.layout_message_chat.setBackgroundResource(R.drawable.background_message_admin);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            frame_params.gravity = Gravity.CENTER;
        }else if(from_user == userController.show().getUser_id()){
            holder.layout_message_chat.setBackgroundResource(R.drawable.background_message_out);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            frame_params.gravity = Gravity.END;
        } else {
            holder.layout_message_chat.setBackgroundResource(R.drawable.background_message_in);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            frame_params.gravity = Gravity.START;
        }
        holder.card_message.setLayoutParams(params);
        holder.layout_message_chat.setLayoutParams(frame_params);
    }

    public void setupLabels(TextView lbl_message_text, TextView lbl_time_message, Message message){
        lbl_message_text.setText(message.getMessage_text());
        lbl_time_message.setText(utils.getTimeNow(message.getCreated_at()));
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        lbl_message_text.setTypeface(bebas_regular);
        lbl_time_message.setTypeface(bebas_regular);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)lbl_time_message.getLayoutParams();
        if(message.getFrom_user() == userController.show().getUser_id()){
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMarginEnd(52);
        } else {
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.setMarginStart(52);
        }
        lbl_time_message.setLayoutParams(params);
    }

}
