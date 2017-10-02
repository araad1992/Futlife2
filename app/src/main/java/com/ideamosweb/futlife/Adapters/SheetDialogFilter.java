package com.ideamosweb.futlife.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.flexbox.FlexboxLayout;
import com.ideamosweb.futlife.Controllers.ConsoleController;
import com.ideamosweb.futlife.Controllers.GameController;
import com.ideamosweb.futlife.EventBus.MessageBusTimeline;
import com.ideamosweb.futlife.EventBus.StationBus;
import com.ideamosweb.futlife.Models.Console;
import com.ideamosweb.futlife.Models.Game;
import com.ideamosweb.futlife.R;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fisk.chipcloud.ChipCloud;
import fisk.chipcloud.ChipCloudConfig;
import fisk.chipcloud.ChipListener;

/**
 * Created by renesantis on 9/24/17.
 */

public class SheetDialogFilter extends BottomSheetDialogFragment {

    private static Context context;
    private ConsoleController consoleController;
    private GameController gameController;
    private List<String> list_tags;

    @Bind(R.id.layout_items)FlexboxLayout layout_items;
    @Bind(R.id.lbl_title_filter)TextView lbl_title_filter;
    @Bind(R.id.lbl_subtitle_filter)TextView lbl_subtitle_filter;

    public static SheetDialogFilter newInstance(Context context_instance) {
        context = context_instance;
        return new SheetDialogFilter();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sheet_dialog_filter, container, false);
        ButterKnife.bind(this, view);
        setViewsFilter();
        return view;
    }

    public void setViewsFilter(){
        consoleController = new ConsoleController(context);
        gameController = new GameController(context);
        list_tags = new ArrayList<>();
        setFontLabels();
        setupItems();
    }

    public void setFontLabels(){
        Typeface bebas_regular = Typeface.createFromAsset(getContext().getAssets(), "fonts/bebas_neue_regular.ttf");
        lbl_title_filter.setTypeface(bebas_regular);
        lbl_subtitle_filter.setTypeface(bebas_regular);
    }

    public void setupItems() {
        List<String> name_items = new ArrayList<>();
        List<Console> consoles = consoleController.show();
        List<Game> games = gameController.show();
        for (int i = 0; i < consoles.size(); i++) {
            name_items.add(consoles.get(i).getName());
        }
        for (int i = 0; i < games.size(); i++) {
            name_items.add(games.get(i).getName());
        }

        ChipCloudConfig config = configChip();
        ChipCloud chipCloud = new ChipCloud(context, layout_items, config);
        chipCloud.addChips(name_items);
        checkTag(chipCloud);
    }

    public void checkTag(final ChipCloud chipCloud){
        chipCloud.setListener(new ChipListener() {
            @Override
            public void chipCheckedChange(int index, boolean checked, boolean userClick) {
                if(checked) {
                    list_tags.add(chipCloud.getLabel(index));
                    System.out.println("tag: " + chipCloud.getLabel(index));
                    System.out.println("index: " + index);
                } else {
                    int position = 0;
                    for (int i = 0; i < list_tags.size(); i++) {
                        if(list_tags.contains(chipCloud.getLabel(index))) {
                            position = i;
                            break;
                        }
                    }
                    list_tags.remove(position);
                }
            }
        });
    }

    public ChipCloudConfig configChip() {
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        return new ChipCloudConfig()
                .typeface(bebas_regular)
                .selectMode(ChipCloud.SelectMode.multi)
                .checkedChipColor(ContextCompat.getColor(context, R.color.color_success))
                .checkedTextColor(ContextCompat.getColor(context, R.color.icons))
                .uncheckedChipColor(ContextCompat.getColor(context, R.color.divider))
                .uncheckedTextColor(ContextCompat.getColor(context, R.color.primaryText))
                .useInsetPadding(true);
    }

    @OnClick(R.id.but_cancel)
    public void cancel(){
        dismiss();
    }

    @OnClick(R.id.but_done)
    public void filter(){
        if(!list_tags.isEmpty()) {
            StationBus.getBus().post(new MessageBusTimeline(true, 2, list_tags));
        }
        dismiss();
    }

}
