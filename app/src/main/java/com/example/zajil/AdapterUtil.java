package com.example.zajil;

import androidx.annotation.Nullable;

import java.util.List;

interface AdapterUtil<type> {

    void set(List<type> list);

    void add(List<type> list);

    void add(type item, int position);

    void add(type item);

    @Nullable
    type get(int position);
}
