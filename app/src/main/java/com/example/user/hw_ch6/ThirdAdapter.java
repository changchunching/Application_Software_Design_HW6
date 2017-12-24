package com.example.user.hw_ch6;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by User on 2017/12/12.
 */

public class ThirdAdapter extends BaseAdapter {
    private Context context;
    String[] socialSites;

    public ThirdAdapter(Context context){
        this.context=context;
        socialSites=context.getResources().getStringArray(R.array.social);
    }
    @Override
    public int getCount() {
        return socialSites.length;
    }

    @Override
    public Object getItem(int position) {
        return socialSites[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row=null;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.third_custom_row, parent, false);
        }else{
            row=convertView;
        }
        TextView nav_custom_text=(TextView) row.findViewById(R.id.history_name);
        nav_custom_text.setText(socialSites[position]);
        return row;
    }
}
