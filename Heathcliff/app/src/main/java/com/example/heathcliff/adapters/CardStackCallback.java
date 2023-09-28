package com.example.heathcliff.adapters;

import androidx.recyclerview.widget.DiffUtil;


import com.example.heathcliff.model.Pet;

import java.util.List;

public class CardStackCallback extends DiffUtil.Callback {

    private List<Pet> oldList, newList;

    public CardStackCallback(List<Pet> oldList, List<Pet> newList){
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getName() == newList.get(newItemPosition).getName();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition) == newList.get(newItemPosition);
    }
}
