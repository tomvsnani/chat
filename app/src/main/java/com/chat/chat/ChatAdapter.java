package com.chat.chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.os.health.TimerStat;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chat.chat.Database.Database;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ChatAdapter extends ListAdapter {
    public static List<Entity> list = new ArrayList<>();

    Context context;
    GoogleSignInClient googleSignInClient;
    chatFragment chatFragment;
    SimpleDateFormat f;
    String username;

    protected ChatAdapter(Context context, GoogleSignInClient googleSignInClient,String username) {
        super(Entity.diffcall);

        this.googleSignInClient = googleSignInClient;

        this.context = context;
        this.chatFragment = (com.chat.chat.chatFragment) context;
        f = new SimpleDateFormat("yyyy-MMM-dd hh:mm a");
        f.setTimeZone(TimeZone.getDefault());
        this.username=username;

    }


    @Override
    public void submitList(@Nullable List list) {
        super.submitList(list != null ? new ArrayList<Entity>(list) : null);
    }

    public void submit(List list) {

        Log.d("inchatsize", String.valueOf(list.size()));
        submitList(list);
    }

    @Override
    public int getItemViewType(int position) {


        if (getCurrentList().size() > 0) {

            Entity entity = (Entity) getCurrentList().get(position);
//            Log.d("viewType",entity.getUri());
            if (entity.getIsImage())
                return 2;
            if (!entity.getIsImage())
                return 1;
        }

        return 0;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("viewType", String.valueOf(viewType));

        if (viewType == 1) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_row_layout, parent, false);
            return new Holder(v);
        }
        if (viewType == 2) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_row_layout_imageview, parent, false);
            return new ImageviewHolder(v);
        }
        return null;
    }

    public float convertDpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public float convertPxToDp(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        if (getCurrentList().size() > 0) {

            final Entity s = (Entity) getCurrentList().get(position);
            if (username.equals(s.getFrom()) ||
                 username .equals(s.getTo())) {
                if (getItemViewType(position) == 1 || getItemViewType(position) == 0) {
                    ((Holder) holder).messageText.setVisibility(View.VISIBLE);
                    ((Holder) holder).messageText.setText(s.getMessage());

                    ((Holder) holder).fromtext.setText(f.format(s.getTimeofMessage()).split(" ")[1]);


                    if (GoogleSignIn.getLastSignedInAccount(context).getDisplayName() != null
                            && s.getFrom().equals(GoogleSignIn.getLastSignedInAccount(context).getDisplayName())) {
                        ((Holder) holder).frameLayout.setGravity(Gravity.END);

                    } else {
                        ((Holder) holder).frameLayout.setGravity(Gravity.START);
                    }
                } else if (getItemViewType(position) == 2) {
                    if (s.getUri() != null) {
                        if (GoogleSignIn.getLastSignedInAccount(context).getDisplayName() != null
                                && s.getFrom().equals(GoogleSignIn.getLastSignedInAccount(context).getDisplayName())) {
                            ((ImageviewHolder) holder).frameLayout.setGravity(Gravity.END);

                        } else {
                            ((ImageviewHolder) holder).frameLayout.setGravity(Gravity.START);
                        }
                        final StorageReference storageReference = FirebaseStorage.getInstance().getReference("images").child(s.getUri());
                        final File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM), s.getUri());
                        try {
                            if (s.getNew_message() && !file.exists())
                                file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (file.exists()) {
                            if (s.getNew_message()) {
                                storageReference.getFile(file).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                }).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        try {
                                            Thread thread = new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    s.setLocalpath(Uri.fromFile(file).toString());
                                                    Database.getInstance(context).dao().update(s);
                                                }
                                            });
                                            thread.start();
                                            InputStream inputStream = chatFragment.getContentResolver().openInputStream(Uri.fromFile(file));

//                                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(
//                                            BitmapFactory.decodeStream(inputStream), (int) convertDpToPx(context,300), (int) convertDpToPx(context,350), false);

                                            ((ImageviewHolder) (holder)).imageView.setImageBitmap(scaleCenterCrop(BitmapFactory.decodeStream(inputStream), (int) convertDpToPx(context, 300), (int) convertDpToPx(context, 320)));
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                            } else {
                                Uri uri = Uri.parse(Database.getInstance(context).dao().getdatabyid(s.getId()).getLocalpath());
                                InputStream inputStream = null;
                                try {
                                    inputStream = chatFragment.getContentResolver().openInputStream(uri);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }

                                ((ImageviewHolder) (holder)).imageView.setImageBitmap(scaleCenterCrop(BitmapFactory.decodeStream(inputStream), (int) convertDpToPx(context, 300), (int) convertDpToPx(context, 320)));

                            }
                        }
                    }

                }

            }
            else{
                if (getItemViewType(position) == 1 || getItemViewType(position) == 0)
                ((Holder) holder).frameLayout.setVisibility(View.GONE);
                else
                ((ImageviewHolder) holder).frameLayout.setVisibility(View.GONE);

            }
        }
    }


    @Override
    public int getItemCount() {
        if (!getCurrentList().isEmpty()) {
            return getCurrentList().size();
        } else
            return 0;
    }


    class Holder extends RecyclerView.ViewHolder {
        TextView fromtext;
        TextView messageText;
        LinearLayout linearLayout;
        LinearLayout frameLayout;

        Holder(@NonNull View itemView) {
            super(itemView);
            Log.d("viewTypeentity", "inviewholder");
            fromtext = itemView.findViewById(R.id.fromwhom);
            messageText = itemView.findViewById(R.id.messagereceived);
            linearLayout = itemView.findViewById(R.id.chat_linear_layout);
            frameLayout = itemView.findViewById(R.id.chat_frame_layout);
            Log.d("viewTypeentity", "declareditems");
        }


    }

    class ImageviewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        LinearLayout frameLayout;

        public ImageviewHolder(@NonNull View itemView) {

            super(itemView);


            imageView = itemView.findViewById(R.id.chat_image);
            frameLayout = itemView.findViewById(R.id.frame_layout);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(chatFragment, ShowFullImageOnClickInChat.class);
                            Entity entity = (Entity) getCurrentList().get(0);
                            Uri uri = Uri.parse(Database.getInstance(context).dao().getdatabyid(entity.getId()).getLocalpath());

                            intent.setData(uri);

                            context.startActivity(intent);
                        }
                    });
                    thread.start();
                }
            });
        }
    }
}