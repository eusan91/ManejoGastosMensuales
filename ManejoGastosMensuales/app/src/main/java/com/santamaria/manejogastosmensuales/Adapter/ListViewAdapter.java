package com.santamaria.manejogastosmensuales.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.santamaria.manejogastosmensuales.Domain.Category;
import com.santamaria.manejogastosmensuales.R;
import com.squareup.picasso.Picasso;

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
            holder.categoryName = (TextView) convertView.findViewById(R.id.tvCategoryName);
            holder.total= (TextView) convertView.findViewById(R.id.tvValorCantidad);
            holder.imageView= (ImageView) convertView.findViewById(R.id.imageViewCategory);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        String currentCategoryName = categoryList.get(position).getNombre();
        float currentTotal = categoryList.get(position).getTotal();
        Object currentPictureObject = categoryList.get(position).getPicture();

        if (currentPictureObject instanceof String){
            String currPicture = currentPictureObject.toString();

            if (currPicture.contains("http")){
                //url
                Picasso.with(context).load( currentPictureObject.toString()).fit().into(holder.imageView);
            } else {
                //create a file
            }

        } else if (currentPictureObject instanceof Integer){
            Picasso.with(context).load((int) currentPictureObject).fit().into(holder.imageView);
        }





        holder.categoryName.setText(currentCategoryName);
        holder.total.setText(currentTotal+"");

        return convertView;
    }


    static class ViewHolder {

        private TextView categoryName;
        private TextView total;
        private ImageView imageView;

    }
}
