package com.example.user.books;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookHolder> {

    private List<Lent> data;
    private List<Lent> cdata;
    private Context context;
    private int access; // 0 for subjects, 1 for borrowed, 2 for lent, 3 for pending

    BookAdapter(Context context, List<Lent> data, int access) {
        this.data = data;
        cdata = new ArrayList<>(data);
        this.context = context;
        this.access = access;
    }

    @Override
    public BookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        if(access == 0)
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_layout2, parent, false);
        return new BookHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BookHolder holder, int position) {
        String title = data.get(position).getTitle();
        holder.tv.setText(title);
        if(data.get(position).getSelectedImage() != null) {
            BitmapFactory.Options options= new BitmapFactory.Options();
            options.inSampleSize= 8;
            byte[] decodedString = Base64.decode(data.get(position).getSelectedImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);
            holder.iv.setImageBitmap(decodedByte);
        } else holder.iv.setImageResource(R.drawable.samplebook);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class BookHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv, tv2;
        ImageView iv;
        BookHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv);
            iv = itemView.findViewById(R.id.iv);
            if(access == 0) {
                tv2 = itemView.findViewById(R.id.tv2);
            }
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String bId = data.get(getAdapterPosition()).getBookId();
            String username = ((Global) context.getApplicationContext()).getUsername();
            if (access == 2  || (access == 3 && data.get(getAdapterPosition()).getBorrower() != null
                && !data.get(getAdapterPosition()).getOwner().equals(username)))
            {
                Intent i = new Intent(context, BorrowedBookActivity.class);
                i.putExtra("bookId", bId);
                i.putExtra("access", access);
                context.startActivity(i);
            }
            else
            {
                Intent i = new Intent(context, BookActivity.class);
                i.putExtra("bookId", bId);
                i.putExtra("access", access);
                View sharedView1 = iv;
                String transitionName1 = "bookImage";
                ActivityOptions transitionActivityOptions =
                        ActivityOptions.makeSceneTransitionAnimation((MainHome)context,
                                Pair.create(sharedView1, transitionName1));
                context.startActivity(i, transitionActivityOptions.toBundle());
            }
        }
    }

    void filter(String text) {
        data.clear();
        if(text.isEmpty()){
            data.addAll(cdata);
        } else{
            text = text.toLowerCase();
            for(Lent item: cdata)
                if(item.getTitle().toLowerCase().contains(text))
                    data.add(item);
        }
        notifyDataSetChanged();
    }

    void remove(String bId) {
        for(Lent item: cdata)
            if(item.getBookId().equals(bId))
                data.remove(item);
        notifyDataSetChanged();
    }
}