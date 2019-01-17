package com.example.user.books;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SwipeAdapter extends RecyclerView.Adapter<SwipeAdapter.SwipeHolder> {

    private List<String> data;
    private Context context;

    SwipeAdapter(Context context, List<String> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public SwipeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.swipe_item, parent, false);
        return new SwipeHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SwipeHolder holder, int position) {
        String title = data.get(position);
        holder.tv.setText(title);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class SwipeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv;
        ImageView iv;
        SwipeHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv);
            iv = itemView.findViewById(R.id.iv);
            itemView.setOnClickListener(this);
        }

        @SuppressLint("RtlHardcoded")
        @Override
        public void onClick(View view) {
            String tm = data.get(getAdapterPosition());
            FragmentActivity myContext = (FragmentActivity)context;
            Toast.makeText(context, tm, Toast.LENGTH_SHORT).show();
            SystemClock.sleep(150);
            Fragment fragment = new HomeFragment(tm);
            FragmentManager fragmentManager = myContext.getSupportFragmentManager();
            fragment.setEnterTransition(new Slide(Gravity.RIGHT));
            fragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment)
                    .addToBackStack(tm).commit();
        }
    }
}