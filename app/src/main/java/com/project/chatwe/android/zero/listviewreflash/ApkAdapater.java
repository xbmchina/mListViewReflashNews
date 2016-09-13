package com.project.chatwe.android.zero.listviewreflash;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/12.
 */
public class ApkAdapater extends BaseAdapter {
    Context context;
    ArrayList<ItemEntity> list;
    public ApkAdapater(Context context ,ArrayList<ItemEntity> list) {
        this.context=context;
        this.list=list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view;
        ItemEntity entity=list.get(position);
        if (convertView==null){
            view= LayoutInflater.from(context).inflate(R.layout.item_of_reflashlistview,null);
            viewHolder=new ViewHolder();
            viewHolder.title= (TextView) view.findViewById(R.id.title);
            viewHolder.content= (TextView) view.findViewById(R.id.content);
            viewHolder.imageInfo= (ImageView) view.findViewById(R.id.image);
            view.setTag(viewHolder);
        }else {
            view=convertView;
            viewHolder= (ViewHolder) view.getTag();
        }
        viewHolder.content.setText(entity.getContent());
        viewHolder.title.setText(entity.getTitle());
        viewHolder.imageInfo.setImageResource(R.drawable.test_icon);

        return view;
    }

    public void onDateChange(ArrayList<ItemEntity> itemEntities) {
        this.list=itemEntities;
        this.notifyDataSetChanged();
    }


    class  ViewHolder{
        TextView title,content;
        ImageView imageInfo;

    }
}
