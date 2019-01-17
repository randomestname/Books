package com.example.user.books;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.MODE_PRIVATE;

public class AccountFragment extends Fragment {

    View view;
    Toolbar toolbar;
    TextView tv_name, tv_username, tv_email, tv_number, tv_semester, tv_points, tv_lent, tv_borrowed, tv_branch;
    ImageView iv_user;
    SharedPreferences sp;
    String username = null;
    DatabaseReference dbu;
    User uu;

    public AccountFragment() {};

    @SuppressLint("ValidFragment")
    public AccountFragment(String username) {
        this.username = username;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, container, false);

        toolbar = getActivity().findViewById(R.id.toolbar);
        tv_name = view.findViewById(R.id.tv_name);
        tv_username = view.findViewById(R.id.tv_username);
        tv_email = view.findViewById(R.id.tv_email);
        tv_points = view.findViewById(R.id.tv_points);
        tv_semester = view.findViewById(R.id.tv_semester);
        tv_number = view.findViewById(R.id.tv_number);
        tv_lent = view.findViewById(R.id.tv_lent);
        tv_borrowed = view.findViewById(R.id.tv_borrowed);
        tv_branch = view.findViewById(R.id.tv_branch);
        iv_user = view.findViewById(R.id.iv);

        if(username == null) {
            setHasOptionsMenu(true);
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            sp = getActivity().getSharedPreferences("Settings", MODE_PRIVATE);

            tv_name.setText(sp.getString("name", null));
            tv_username.setText(sp.getString("username", null));
            tv_points.setText(String.valueOf(sp.getInt("points", 0)));
            tv_lent.setText(String.valueOf(sp.getInt("lentb", 0)));
            tv_borrowed.setText(String.valueOf(sp.getInt("borrowed", 0)));
            tv_email.setText(sp.getString("email", null));
            tv_number.setText(sp.getString("phone", null));
            tv_semester.setText("Semester: IV");
            tv_branch.setText("Branch: BTech Computer");

            if (sp.getString("image", null) != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                byte[] decodedString = Base64.decode(sp.getString("image", null), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);
                iv_user.setImageBitmap(decodedByte);
            }

        } else {
            dbu = FirebaseDatabase.getInstance().getReference("user");
            dbu.child(username).addListenerForSingleValueEvent(postListener);
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit, menu);
        MenuItem item = menu.findItem(R.id.action_edit);

        item.setOnMenuItemClickListener(
                new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Intent intent= new Intent(getContext(), SignUpActivity.class);
                        intent.putExtra("access", 4);

                        startActivity(intent);
                        Toast.makeText(getContext(), "Edit Profile!", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
        );
        if(((Global) getActivity().getApplication()).isTutorial())
            onNextLayout(toolbar, new Runnable() {
                @Override
                public void run() {
                    showTapTargetView();
                }
            });
    }

    //Logic for tutorial starts here
    static void onNextLayout(final View view, final Runnable runnable) {
        final ViewTreeObserver observer = view.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final ViewTreeObserver trueObserver;
                if (observer.isAlive()) {
                    trueObserver = observer;
                } else {
                    trueObserver = view.getViewTreeObserver();
                }
                removeOnGlobalLayoutListener(trueObserver, this);
                runnable.run();
            }
        });
    }

    static void removeOnGlobalLayoutListener(ViewTreeObserver observer,
                                             ViewTreeObserver.OnGlobalLayoutListener listener) {
        observer.removeOnGlobalLayoutListener(listener);
    }

    void showTapTargetView() {
        ((Global) getActivity().getApplication()).setTutorial();
        TapTargetSequence tapTargetSequence = new TapTargetSequence(getActivity())
                .targets(
                        TapTarget.forToolbarMenuItem(toolbar, R.id.action_edit, "Edit profile",
                                "Remember you cannot change a lot of things!")
                ).listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() { }
                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) { }
                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) { }
                });
        tapTargetSequence.start();
    }
    //Logic for tutorial ends here

    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists())
            {
                uu= dataSnapshot.getValue(User.class);
                tv_name.setText(uu.getName());
                if (uu.getUserName() != null)
                    tv_username.setText(uu.getUserName());
                tv_points.setText(String.valueOf(uu.getPoints()));
                tv_lent.setText(String.valueOf(uu.getLentb()));
                tv_borrowed.setText(String.valueOf(uu.getBorrowed()));
                tv_email.setText(uu.getEmail());
                tv_number.setText(uu.getPhone());

                if (uu.getSelectedImage() != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    byte[] decodedString = Base64.decode(uu.getSelectedImage(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);
                    iv_user.setImageBitmap(decodedByte);
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}
