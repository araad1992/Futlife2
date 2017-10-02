package com.ideamosweb.futlife.Adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hanks.library.AnimateCheckBox;
import com.ideamosweb.futlife.Controllers.ConsoleController;
import com.ideamosweb.futlife.Controllers.PreferenceController;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.Console;
import com.ideamosweb.futlife.Models.ConsolePreference;
import com.ideamosweb.futlife.R;
import com.squareup.picasso.Picasso;
import java.util.List;

/**
 * Creado por Deimer Villa on 2/11/16.
 * Función:
 */
public class CoverFlowConsoleAdapter extends PagerAdapter {

    private Context context;
    private List<Console> consoles;
    private LinearLayout linearLayout;
    private UserController userController;
    private PreferenceController preferenceController;

    public CoverFlowConsoleAdapter(Context context, List<Console> consoles, LinearLayout linearLayout) {
        this.context = context;
        this.consoles = consoles;
        this.linearLayout = linearLayout;
    }

    @Override
    public Object instantiateItem(ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_console, null);
        setupAdapter();
        //Elementos de la tarjeta
        CardView card_console = (CardView) view.findViewById(R.id.cardview_console);
        AnimateCheckBox checkbox = (AnimateCheckBox)view.findViewById(R.id.checkbox_console);
        ImageView img_console = (ImageView) view.findViewById(R.id.img_console);
        //Funciones de los elementos
        setupOptionsCard(card_console, checkbox);
        setupStateConsole(checkbox, position);
        setupCheckedConsole(checkbox, position);
        setupImageConsole(img_console, position);
        //Construccion de la tarjeta
        parent.addView(view);
        return view;
    }

    private void setupAdapter(){
        userController = new UserController(context);
        preferenceController = new PreferenceController(context);
    }

    private void setupOptionsCard(CardView card_console, final AnimateCheckBox checkbox){
        card_console.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkbox.isChecked()){
                    checkbox.setChecked(false);
                } else {
                    checkbox.setChecked(true);
                }
            }
        });
    }

    private void setupStateConsole(AnimateCheckBox checkbox, int position){
        ConsolePreference console = preferenceController.existConsole(userController.show().getUser_id(), consoles.get(position).getConsole_id());
        if(console != null){
            boolean active = console.getActive();
            if(active) {
                checkbox.setChecked(true);
            } else {
                checkbox.setChecked(false);
            }
        }
    }

    private void setupCheckedConsole(AnimateCheckBox checkbox, final int position){
        final ConsoleController consoleController = new ConsoleController(context);
        checkbox.setOnCheckedChangeListener(new AnimateCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View buttonView, boolean isChecked) {
                Console console = consoles.get(position);
                setConsolePreference(console, isChecked);
                consoleController.update(console);
                buildListConsoles();
            }
        });
    }

    private void setConsolePreference(Console console, boolean active){
        ConsolePreference preference = preferenceController.existConsole(userController.show().getUser_id(), console.getConsole_id());
        if(preference == null) {
            preference = new ConsolePreference();
            preference.setUser_id(userController.show().getUser_id());
            preference.setConsole_id(console.getConsole_id());
            preference.setName(console.getName());
            preference.setAvatar(console.getAvatar());
            preference.setThumbnail(console.getThumbnail());
            preference.setActive(true);
        } else {
            preference.setActive(active);
        }
        preferenceController.create(preference);
        System.out.println(preference.toString());
    }

    private void setupImageConsole(ImageView img_console, int position){
        Picasso.with(context)
                .load(consoles.get(position).getAvatar())
                .fit()
                .placeholder(R.drawable.loading_console)
                .error(R.drawable.loading_console)
                .into(img_console);
        img_console.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    }

    private void buildListConsoles(){
        List<ConsolePreference> preferences = preferenceController.consoles(userController.show().getUser_id());
        linearLayout.removeAllViews();
        if(!preferences.isEmpty()){
            for (int i = 0; i < preferences.size(); i++) {
                int cont = i + 1;
                TextView lbl_console = new TextView(context);
                String name = cont + ". " + preferences.get(i).getName();
                lbl_console.setText(name);
                lbl_console.setPadding(0,0,0,8);
                linearLayout.addView(lbl_console);
            }
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return consoles.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

}
