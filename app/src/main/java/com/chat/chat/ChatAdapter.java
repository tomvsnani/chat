package com.chat.chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends ListAdapter {
    public static List<Entity> list = new ArrayList<>();

    Context context;
    GoogleSignInClient googleSignInClient;
    protected ChatAdapter(Context context, GoogleSignInClient googleSignInClient) {
        super(Entity.diffcall);

        this.googleSignInClient = googleSignInClient;

        this.context = context;



    }


    @Override
    public void submitList(@Nullable List list) {
        super.submitList(list != null ? new ArrayList<Entity>(list) : null);
    }

public void submit(List list){

        Log.d("inchatsize", String.valueOf(list.size()));
        submitList(list);
}

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_row_layout, parent, false);
        return new Holder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
if(getCurrentList().size()>0) {
    Entity s = (Entity) getCurrentList().get(position);
    Log.d("inchat", String.valueOf(s.getMessage()));
    ((Holder) holder).fromtext.setText(s.getFrom());
    ((Holder) holder).messageText.setText(s.getMessage());
}

    }


    @Override
    public int getItemCount() {
        if (!getCurrentList().isEmpty()) {
            return getCurrentList().size();
        } else
            return 0;
    }


    class Holder extends RecyclerView.ViewHolder{
        TextView fromtext;
        TextView messageText;
        Holder(@NonNull View itemView) {
            super(itemView);
          fromtext = itemView.findViewById(R.id.fromwhom);
          messageText = itemView.findViewById(R.id.messagereceived);
        }



    }
}