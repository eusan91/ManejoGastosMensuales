package com.santamaria.manejogastosmensuales.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.santamaria.manejogastosmensuales.Domain.Category;
import com.santamaria.manejogastosmensuales.R;

import java.util.List;

/**
 * Created by Santamaria on 09/04/2017.
 */

public class ListViewAdapter extends BaseAdapter {

    private List<Category> categoryList;
    private Context context;
    private int layout;

    public ListViewAdapter(List<Category> categoryList, Context context, int layout) {
        this.categoryList = categoryList;
        this.context = context;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        if (categoryList != null){
            return categoryList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return categoryList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder holder;

        if (convertView == null){
            convertView = LayoutInflater.from(this.context).inflate(layout, null);

            holder = new ViewHolder();
            holder.categoryName = convertView.findViewById(R.id.tvCategoryName);
            holder.total= convertView.findViewById(R.id.tvValorCantidad);
            holder.frameColorLayout = convertView.findViewById(R.id.imageViewCategory);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        String currentCategoryName = categoryList.get(position).getNombre();
        float currentTotal = categoryList.get(position).getTotal();
        int color = categoryList.get(position).getColor();

        holder.categoryName.setText(currentCategoryName);
        holder.total.setText(currentTotal+"");
        holder.frameColorLayout.setBackgroundColor(color);

        return convertView;
    }


    private class ViewHolder {

        private TextView categoryName;
        private TextView total;
        private FrameLayout frameColorLayout;

    }
}
