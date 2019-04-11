package com.joinmedevelopment.joinme;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Collectors;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder>{

    private static ArrayList<Friend> friendsList;

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView textViewFriendName;
        //TextView textViewFriendEmail;
        Button buttonConfirmFriend;

        FriendViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cardView);
            textViewFriendName = (TextView) itemView.findViewById(R.id.textViewFriendName);
            //textViewFriendEmail = (TextView) itemView.findViewById(R.id.textViewFriendEmail);
            buttonConfirmFriend = (Button) itemView.findViewById(R.id.buttonConfirmFriend);

        }
    }

    public FriendAdapter(final HashMap<String, Friend> friendsMap) {
        friendsList = new ArrayList<>(friendsMap.values());
        final String currentUserID = FirebaseAuth.getInstance().getUid();

        Iterator it = friendsList.iterator();
        while (it.hasNext()) {
            Friend f = (Friend)it.next();

            if (f.getIdWhoAdded().equals(currentUserID) && !f.isFriend()) {
                it.remove();
            }
        }
    }

    @NonNull
    @Override
    public FriendAdapter.FriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friend_list_item, viewGroup, false);
        FriendAdapter.FriendViewHolder holder = new FriendAdapter.FriendViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendAdapter.FriendViewHolder friendViewHolder, int i) {
        final String currentUserID = FirebaseAuth.getInstance().getUid();

        final Friend f = friendsList.get(i);

        // If you added the friend and they haven't confirmed you as a friend, don't list as friend in RecyclerView
        if (f.getIdWhoAdded().equals(currentUserID) && !f.isFriend()) {
            return;
        }

        final boolean isFriend = f.isFriend();

        if (f.getName() == null)
            friendViewHolder.textViewFriendName.setText(f.getId());
        else
            friendViewHolder.textViewFriendName.setText(f.getName());

        if (isFriend)
            friendViewHolder.buttonConfirmFriend.setText("Remove");
        else
            friendViewHolder.buttonConfirmFriend.setText("Confirm");

        final int j = i;
        friendViewHolder.buttonConfirmFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference databaseUsers = FirebaseDatabase.getInstance().getReference("users");
                final DatabaseReference databaseReference = databaseUsers.child(currentUserID + "/friends/" + f.getId());

                databaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<HashMap<String, Friend>> typeIndicator = new GenericTypeIndicator<HashMap<String, Friend>>() {};
                        HashMap<String, Friend> friendsMap = dataSnapshot.child(f.getId()).child("friends").getValue(typeIndicator);

                        if (friendsMap == null)
                            friendsMap = new HashMap<String, Friend>();

                        if (isFriend) {
                            f.setFriend(false);
                            HashMap<String, Friend> userFriendsMap = dataSnapshot.child(currentUserID).child("friends").getValue(typeIndicator);
                            friendsMap.remove(currentUserID);


                            userFriendsMap.remove(f.getId());
                            databaseUsers.child(currentUserID).child("friends").setValue(userFriendsMap);
                        }
                        else {
                            f.setFriend(true);

                            friendsMap.put(currentUserID, new Friend(currentUserID, true));
                            new Friend(f.getId(), true);

                            databaseReference.setValue(f);
                        }

                        databaseUsers.child(f.getId()).child("friends").setValue(friendsMap);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }
}
