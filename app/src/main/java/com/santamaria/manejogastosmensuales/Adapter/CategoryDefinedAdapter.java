package com.santamaria.manejogastosmensuales.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.santamaria.manejogastosmensuales.Domain.CategoryDefined;
import com.santamaria.manejogastosmensuales.R;

import java.util.List;

/**
 * Created by Santamaria on 22/04/2017.
 */

public class CategoryDefinedAdapter extends BaseAdapter {

    private Context context;
    private List<CategoryDefined> categoryDefinedList;
    private int layout;

    public CategoryDefinedAdapter(Context context, List<CategoryDefined> categoryDefinedList, int layout) {
        this.context = context;
        this.categoryDefinedList = categoryDefinedList;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return categoryDefinedList.size();
    }

    @Override
    public Object getItem(int i) {
        return categoryDefinedList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if (view == null){

            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(layout, null);

            viewHolder.categoryName = view.findViewById(R.id.textViewCategoryDefined);
            viewHolder.LinearColorLayout = view.findViewById(R.id.LinearColorBackground);
            view.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) view.getTag();

        }

        viewHolder.categoryName.setText(categoryDefinedList.get(i).getCategoryName());
        viewHolder.LinearColorLayout.setBackgroundColor(categoryDefinedList.get(i).getColor());


        return view;
    }

    static class ViewHolder {

        TextView categoryName;
        LinearLayout LinearColorLayout;
    }
}
