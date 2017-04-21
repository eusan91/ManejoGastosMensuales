package com.santamaria.manejogastosmensuales.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.santamaria.manejogastosmensuales.Domain.CategoryDetail;
import com.santamaria.manejogastosmensuales.R;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by santamae on 4/21/2017.
 */

public class CategoryDetailedAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<CategoryDetail> categoryDetailList;

    public CategoryDetailedAdapter(Context context, int layout, List<CategoryDetail> categoryDetailList) {
        this.context = context;
        this.layout = layout;
        this.categoryDetailList = categoryDetailList;
    }

    @Override
    public int getCount() {

        if (categoryDetailList != null){
            return categoryDetailList.size();
        }

        return 0;
    }

    @Override
    public Object getItem(int i) {
        return categoryDetailList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;

        if (view == null){

            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(layout, null);

            viewHolder.detail = (TextView) view.findViewById(R.id.detail);
            viewHolder.date = (TextView) view.findViewById(R.id.date);
            viewHolder.currency = (TextView) view.findViewById(R.id.currency);
            viewHolder.amount = (TextView) view.findViewById(R.id.amount);

            view.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) view.getTag();

        }

        viewHolder.detail.setText(categoryDetailList.get(position).getDetail());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        viewHolder.date.setText(dateFormat.format(categoryDetailList.get(position).getDate()));
        viewHolder.currency.setText("$");
        viewHolder.amount.setText(categoryDetailList.get(position).getAmount()+"");

        return view;
    }

    static class ViewHolder {

        private TextView detail;
        private TextView date;
        private TextView currency;
        private TextView amount;
    }

}
