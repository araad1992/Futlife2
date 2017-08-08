package com.ideamosweb.futlife.Fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ideamosweb.futlife.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Creado por Deimer Villa on 1/06/17.
 * Función:
 */
public class DialogAvatar extends Fragment {

    private Context context;
    private static String avatar_instance;
    private static String name_instance;

    @Bind(R.id.lbl_name_player)
    TextView lbl_name_player;
    @Bind(R.id.img_avatar_user)
    ImageView img_avatar_user;

    public static DialogAvatar newInstance(String url_avatar, String name) {
        DialogAvatar dialog_avatar = new DialogAvatar();
        Bundle args = new Bundle();
        args.putString("url_avatar", url_avatar);
        args.putString("name_user", name);
        dialog_avatar.setArguments(args);
        return dialog_avatar;
    }

    public DialogAvatar(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_avatar_view, container, false);
        ButterKnife.bind(this, view);
        readBundles(getArguments());
        setupFrame();
        return view;
    }

    public void readBundles(Bundle bundle){
        if (bundle != null) {
            avatar_instance = bundle.getString("url_avatar");
            name_instance = bundle.getString("name_user");
        }
    }

    public void setupFrame(){
        context = getActivity().getApplicationContext();
        loadAvatarUser();
    }

    public void loadAvatarUser(){
        Glide.with(context)
                .load(avatar_instance)
                .fitCenter()
                .centerCrop()
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.avatar_default)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .into(img_avatar_user);
        lbl_name_player.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf"));
        lbl_name_player.setText(name_instance);
    }

    @OnClick(R.id.but_return)
    public void setReturn(){
        getActivity().finish();
    }

}
