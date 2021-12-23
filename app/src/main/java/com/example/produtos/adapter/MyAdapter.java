package com.example.produtos.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.produtos.model.Product;
import com.example.produtos.R;
import com.example.produtos.activity.ViewProductActivity;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter {
    Context context;
    List<Product> products;

    public MyAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.list_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Product product = this.products.get(position);

        TextView tvNameList = holder.itemView.findViewById(R.id.tvNameList);
        tvNameList.setText(product.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ViewProductActivity.class);
                i.putExtra("pid", product.getId());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
