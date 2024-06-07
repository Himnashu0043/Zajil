package com.example.zajil;

import android.annotation.SuppressLint;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

abstract public class BaseAdapter<type> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements AdapterUtil<type> {

    public ArrayList<type> itemList = new ArrayList<>();


    public void removeAt(int position) {
        itemList.remove(position);
        notifyItemRemoved(position);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clear(){
        itemList.clear();
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void set(List<type> list) {
        clear();
        itemList.addAll(list);
        notifyItemRangeInserted(0, list.size());
    }

    @Override
    public void add(List<type> list) {
        int position = itemList.size();
        itemList.addAll(list);
        notifyItemInserted(position);
    }

    @Override
    public void add(type item) {
        int position = itemList.size();
        itemList.add(item);
        notifyItemInserted(position);
    }

    @Override
    public void add(type item, int position) {
        itemList.add(position, item);
        notifyItemInserted(position);
    }

    @Override
    public type get(int position) {
        return itemList.get(position);
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
