package com.ideamosweb.futlife.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.JsonObject;
import com.ideamosweb.futlife.Controllers.PreferenceController;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.EventBus.MessageBusInformation;
import com.ideamosweb.futlife.EventBus.StationBus;
import com.ideamosweb.futlife.Models.ConsolePreference;
import com.ideamosweb.futlife.Models.Player;
import com.ideamosweb.futlife.Models.User;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.Service.Api;
import com.ideamosweb.futlife.Utils.MaterialDialog;
import com.ideamosweb.futlife.Utils.ToastMessages;
import com.ideamosweb.futlife.Utils.Utils;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Creado por Deimer Villa on 16/02/17.
 * Función:
 */
public class TabInformation extends Fragment {

    private Context context;
    private static Player player_instance;
    private UserController userController;
    private PreferenceController preferenceController;
    private MaterialDialog dialog;
    private ToastMessages toast;
    private Utils utils;
    private static boolean principal_instance;
    private Typeface bebas_regular;

    @Bind(R.id.lbl_username_profile)
    TextView lbl_username_profile;
    @Bind(R.id.lbl_fullname_profile)
    TextView lbl_fullname_profile;
    @Bind(R.id.lbl_ubication_profile)
    TextView lbl_ubication_profile;
    @Bind(R.id.lbl_email_profile)
    TextView lbl_email_profile;
    @Bind(R.id.lbl_telephone_profile)
    TextView lbl_telephone_profile;
    @Bind(R.id.lbl_birthdate_profile)
    TextView lbl_birthdate_profile;
    @Bind(R.id.layout_players_ids)
    LinearLayout layout_players_ids;
    @Bind(R.id.layout_items_players_ids)
    LinearLayout layout_items_players_ids;

    public static TabInformation newInstance(boolean principal, Player player) {
        principal_instance = principal;
        player_instance = player;
        return new TabInformation();
    }

    public TabInformation(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frame_my_info, container, false);
        ButterKnife.bind(this, view);
        setupTab();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        StationBus.getBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        StationBus.getBus().unregister(this);
    }

    @Subscribe
    public void recievedChallenge(MessageBusInformation messageBusInformation){
        boolean active = messageBusInformation.isActive();
        if(active) {
            setDataProfile();
        }
    }

    public void setupTab(){
        context = getActivity().getApplicationContext();
        userController = new UserController(context);
        preferenceController = new PreferenceController(context);
        dialog = new MaterialDialog(getContext());
        toast = new ToastMessages(getContext());
        utils = new Utils(context);
        bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        if(principal_instance){
            setDataProfile();
        } else {
            setDataPlayer();
        }
        setDataLayout();
    }

    public void loadPlayersIds(){
        if(!principal_instance){
            layout_players_ids.setVisibility(View.GONE);
        } else {
            User user_instance = userController.show();
            List<ConsolePreference> consoles = preferenceController.consoles(user_instance.getUser_id());
            for (int i = 0; i < consoles.size(); i++) {
                final ConsolePreference console = consoles.get(i);
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                RelativeLayout layout_items = (RelativeLayout)inflater.inflate(R.layout.template_textview_player_id, null);
                ImageView img_item_console = (ImageView)layout_items.findViewById(R.id.img_item_console);
                final TextView lbl_title_item = (TextView)layout_items.findViewById(R.id.lbl_title_console);
                TextView lbl_subtitle_item = (TextView)layout_items.findViewById(R.id.lbl_subtitle_console);
                loadThumbnailConsole(img_item_console, console.getConsole_id());
                loadPlayerId(lbl_title_item, lbl_subtitle_item, console);
                layout_items.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogPlayerId(console, lbl_title_item);
                    }
                });
                layout_items_players_ids.addView(layout_items);
            }
        }
    }

    public void dialogPlayerId(ConsolePreference console, TextView lbl_title_item){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_console_preference_player_id, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        setViewsDialogPlayerId(view, alertDialog, console, lbl_title_item);
        alertDialog.show();
    }

    public void setViewsDialogPlayerId(View view, final AlertDialog alertDialog, final ConsolePreference console, final TextView lbl_title_item){
        TextView lbl_title_dialog = (TextView)view.findViewById(R.id.lbl_title_dialog);
        lbl_title_dialog.setText("Agregue un PLAYER ID a la consola " + console.getName());
        lbl_title_dialog.setTypeface(bebas_regular);
        ImageView img_console = (ImageView)view.findViewById(R.id.img_console_preference);
        setAvatarPlayers(img_console, console.getAvatar());
        final EditText txt_player_id = (EditText)view.findViewById(R.id.txt_player_id);
        txt_player_id.setTypeface(bebas_regular);
        Button but_cancel = (Button)view.findViewById(R.id.but_cancel_dialog);
        Button but_done = (Button)view.findViewById(R.id.but_ok_dialog);
        but_cancel.setTypeface(bebas_regular);
        but_done.setTypeface(bebas_regular);
        but_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        but_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(utils.connect()) {
                    String player_id = txt_player_id.getText().toString().trim();
                    if(player_id.isEmpty()) {
                        toast.toastWarning("Debes agregar un player id primero.");
                    } else {
                        alertDialog.dismiss();
                        console.setPlayer_id(player_id);
                        updatePlayerId(console, lbl_title_item);
                    }
                } else {
                    alertDialog.dismiss();
                    dialog.dialogErrors("Error de conexión", "No se detecto una red de conexión estable a internet.");
                }
            }
        });
    }

    public void updatePlayerId(final ConsolePreference preference, final TextView lbl_title_item){
        dialog.dialogProgress("Actualizando player_id");
        String token = "Bearer " + userController.show().getToken();
        String url = getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.updatePreference(token, preference.getPreference_id(), preference.getPlayer_id(), new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                String message = jsonObject.get("message").getAsString();
                if (success) {
                    preferenceController.update(preference);
                    lbl_title_item.setText(preference.getPlayer_id());
                    toast.toastSuccess(message);
                } else {
                    toast.toastWarning(message);
                }
                dialog.cancelProgress();
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.cancelProgress();
                dialog.dialogWarnings("Error", error.getMessage());
                Log.d("TabInformation(autocomplete)", "Errors message: " + error.getMessage());
            }
        });
    }

    public void setAvatarPlayers(ImageView img_console, String url_image){
        Picasso.with(context)
                .load(url_image)
                .placeholder(R.drawable.loading_console)
                .fit()
                .centerInside()
                .error(R.drawable.loading_console)
                .into(img_console);
    }

    public void loadPlayerId(TextView lbl_title_item, TextView lbl_subtitle_item, ConsolePreference preference){
        if(preference.getPlayer_id() == null || preference.getPlayer_id().isEmpty()){
            lbl_title_item.setText("Sin registrar");
        } else {
            lbl_title_item.setText(preference.getPlayer_id());
        }
        lbl_subtitle_item.setText(preference.getName());
        lbl_title_item.setTypeface(bebas_regular);
        lbl_subtitle_item.setTypeface(bebas_regular);
    }

    public void loadThumbnailConsole(ImageView img_item, int console_id){
        switch (console_id){
            case 1:
                Picasso.with(context).load(R.drawable.xbox360).centerInside().fit().into(img_item);
                break;
            case 2:
                Picasso.with(context).load(R.drawable.xboxone).centerInside().fit().into(img_item);
                break;
            case 3:
                Picasso.with(context).load(R.drawable.play3).centerInside().fit().into(img_item);
                break;
            case 4:
                Picasso.with(context).load(R.drawable.play4).centerInside().fit().into(img_item);
                break;
        }
    }

    public void setViewColorDataNull(TextView label, String value, String temp){
        if(utils.valueIsNull(value)){
            label.setText(temp);
            label.setTextColor(ContextCompat.getColor(context, R.color.divider));
        } else {
            label.setText(value);
            label.setTextColor(ContextCompat.getColor(context, R.color.primaryText));
        }
    }

    public void setViewColorData(TextView label, String value, String temp){
        if(value.equalsIgnoreCase("no-data")){
            label.setText(temp);
            label.setTextColor(ContextCompat.getColor(context, R.color.divider));
        } else {
            label.setText(value);
            label.setTextColor(ContextCompat.getColor(context, R.color.primaryText));
        }
    }

    public void setDataLayout(){
        lbl_username_profile.setTypeface(bebas_regular);
        lbl_fullname_profile.setTypeface(bebas_regular);
        lbl_ubication_profile.setTypeface(bebas_regular);
        lbl_email_profile.setTypeface(bebas_regular);
        lbl_telephone_profile.setTypeface(bebas_regular);
        lbl_birthdate_profile.setTypeface(bebas_regular);
        loadPlayersIds();
    }

    public void setDataProfile(){
        User user_instance = userController.show();
        setViewColorDataNull(lbl_username_profile, user_instance.getUsername(), "Usuario");
        setViewColorDataNull(lbl_fullname_profile, user_instance.getName(), "Nombre Completo");
        setViewColorDataNull(lbl_email_profile, user_instance.getEmail(), "Correo Electrónico");
        setViewColorData(lbl_ubication_profile, user_instance.getCity_name(), "Ubicación");
        setViewColorDataNull(lbl_telephone_profile, user_instance.getTelephone(), "Teléfono");
        setViewColorDataNull(lbl_birthdate_profile, user_instance.getBirthdate(), "Fecha de Nacimiento");
    }

    public void setDataPlayer(){
        setViewColorDataNull(lbl_username_profile, player_instance.getUsername(), "Usuario");
        setViewColorDataNull(lbl_fullname_profile, player_instance.getName(), "Nombre Completo");
        setViewColorDataNull(lbl_email_profile, player_instance.getEmail(), "Correo Electrónico");
        setViewColorData(lbl_ubication_profile, player_instance.getCity_name(), "Ubicación");
        setViewColorDataNull(lbl_telephone_profile, player_instance.getTelephone(), "Teléfono");
        setViewColorDataNull(lbl_birthdate_profile, player_instance.getBirthdate(), "Fecha de Nacimiento");
    }

}
