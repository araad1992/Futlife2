package com.ideamosweb.futlife.Adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.ideamosweb.futlife.R;
import com.squareup.picasso.Picasso;
import java.util.List;

/**
 * Creado por Deimer Villa on 6/07/17.
 * Función:
 */
public class TutoSwiperAdapter extends PagerAdapter {

    private Context context;
    private List<Integer> images;

    public TutoSwiperAdapter(Context context, List<Integer> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.swipe_layout, container, false);
        ImageView img_swipe = (ImageView) view.findViewById(R.id.img_swipe);
        loadImageSwipe(img_swipe, position);
        container.addView(view);
        return view;
    }

    public void loadImageSwipe(ImageView img_swipe, int position){
        Picasso.with(context)
                .load(images.get(position))
                .fit()
                .centerCrop()
                .into(img_swipe);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

}
