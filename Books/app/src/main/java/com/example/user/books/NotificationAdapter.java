package com.example.user.books;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.SwipeHolder> {

    private List<NotiItemObject> data;
    private Context context;
    private MyDBHandler dbHandler;

    NotificationAdapter(Context context) {
        this.context = context;
        dbHandler = new MyDBHandler(context, null);
        data = dbHandler.getNotificationData();
        ((Global) context.getApplicationContext()).setNdata(data);
    }

    @Override
    public SwipeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.not_layout, parent, false);
        return new SwipeHolder(itemView);
    }


    @Override
    public void onBindViewHolder(SwipeHolder holder, int position) {
        String title = "Error loading";
        String titl[] = processData(data.get(position).getData());
        if(titl.length >= 1) title = titl[titl.length - 1];
        holder.tv.setText(title);
        if(data.get(position).isRead) {
            if(((Global) context.getApplicationContext()).isDay())
                holder.cv.setCardBackgroundColor(Color.parseColor("#DEDEDE"));
            else
                holder.cv.setCardBackgroundColor(Color.parseColor("#777777"));
            holder.tv.setTextColor(Color.parseColor("#BBBBBB"));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class SwipeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv;
        CardView cv;
        SwipeHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.not_main_tv);
            cv = itemView.findViewById(R.id.not_cv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String tm[] = processData(data.get(getAdapterPosition()).getData());
            if(tm.length >= 2) {
                String bId = tm[0].substring(2);
                Log.e("TAG", "Substr: " + bId);
                List<Lent> tdata = ((Global) context.getApplicationContext()).getCdata();
                for(Lent l : tdata) {
                    if (l.getBookId() != null)
                        if (l.getBookId().equals(bId)) {
                            Intent i = new Intent(context, BookActivity.class);
                            i.putExtra("bookId", bId);
                            i.putExtra("access", 3);
                            context.startActivity(i);
                            Log.e("TAG", "found the book!");
                            break;
                        }
                }
            }
        }
    }

    NotiItemObject removeItem(int position) {
        NotiItemObject noti = data.get(position);
        data.remove(position);
        ((Global) context.getApplicationContext()).decUnReadNoti();
        notifyItemRemoved(position);
        return noti;
    }

    void addItem(String noti) {
        NotiItemObject notiItemObject = new NotiItemObject(noti);
        int notiid = dbHandler.addNotification(notiItemObject);
        notiItemObject.setId(notiid);
        data.add(0, notiItemObject);
        ((Global) context.getApplicationContext()).incUnReadNoti();
        notifyItemInserted(0);
    }

    void addItem(int position, NotiItemObject noti) {
        data.add(position, noti);
        ((Global) context.getApplicationContext()).incUnReadNoti();
        notifyItemInserted(position);
    }

    public void clear() {
        final int size = data.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                data.remove(0);
            }
            notifyItemRangeRemoved(0, size);
        }
    }

    String[] processData(String tmp) {
        String id = tmp.substring(0, 2);
        String tmp2[];
        if(id.equals("I:")) {
            tmp2 = tmp.split(" ", 2);
        } else {
            tmp2 = new String[1];
            tmp2[0] = tmp;
        }
        return tmp2;
    }
}