package com.example.user.books;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookHolder> {

    List<String> data;
    Context context;
    public BookAdapter(Context context, List<String> data) {
        this.context = context;
        this.data = data;
    }
    public void newData(List<String> data)  {
        this.data = data;
    }
    @Override
    public BookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new BookHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BookHolder holder, int position) {
        String title = data.get(position);
        holder.tv.setText(title);
        holder.iv.setImageResource(R.drawable.ic_launcher_background);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class BookHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tv;
        public ImageView iv;
        public BookHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv);
            iv = itemView.findViewById(R.id.iv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String tm = data.get(getAdapterPosition());
            Toast.makeText(context, tm, Toast.LENGTH_SHORT).show();
            context.startActivity(new Intent(context, BookActivity.class));
        }
    }
}