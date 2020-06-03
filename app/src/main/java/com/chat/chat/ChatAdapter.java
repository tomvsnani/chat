package com.chat.chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.FileOutputStream;
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
    RecyclerView recyclerView;

    protected ChatAdapter(Context context, GoogleSignInClient googleSignInClient, String username, RecyclerView recyclerView) {
        super(Entity.diffcall);

        this.googleSignInClient = googleSignInClient;

        this.context = context;
        this.chatFragment = (com.chat.chat.chatFragment) context;
        f = new SimpleDateFormat("yyyy-MMM-dd hh:mm a");
        f.setTimeZone(TimeZone.getDefault());
        this.username = username;
        this.recyclerView = recyclerView;


    }


    @Override
    public void submitList(@Nullable List list) {
        Log.d("inchatsize", String.valueOf(list.size()));
       // recyclerView.setItemViewCacheSize(getItemCount());
        super.submitList(list != null ? new ArrayList<Entity>(list) : null);
    }

    public void submit(List list, final RecyclerView recyclerView) {
        ChatAdapter.list = list;

        submitList(list);
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(getCurrentList().size()>0)
                recyclerView.scrollToPosition(getCurrentList().size() - 1);
            }
        }, 100);
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

        Rect rect=new Rect(0,sourceHeight/2,(int)sourceWidth,(int)sourceHeight+sourceHeight/2);
       // Bitmap b=Bitmap.createScaledBitmap(source,(int)scaledWidth,(int)scaledHeight,false);
        FileOutputStream out = null;
//        RectF targetRect = new RectF(left,top, (int)scaledWidth,(int)scaledHeight+top);
        RectF targetRect = new RectF(0,0, (int)scaledWidth,(int)scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap((int)newWidth,(int)newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
       // canvas.drawColor(Color.RED);
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        canvas.drawBitmap(source, rect,targetRect, paint);



//
//
//
//        Log.d("pixelsize", "scaledheight " + scaledHeight);
//        Log.d("pixelsize", "scaledwidth " + scaledWidth);
//        Log.d("pixelsize", "h  " + newHeight);
//        Log.d("pixelsize", "w " + newWidth);
//        Log.d("pixelsize", "sw " + sourceWidth);
//        Log.d("pixelsize", "sh " + sourceHeight);
//        Log.d("pixelsize", "defaultdisplay " + left + "  " + top);

        return dest;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        if (getCurrentList().size() > 0) {

            final Entity s = (Entity) getCurrentList().get(position);

            if (getItemViewType(position) == 1 || getItemViewType(position) == 0) {
                ((Holder) holder).messageText.setVisibility(View.VISIBLE);
                ((Holder) holder).messageText.setText(s.getMessage());


                ((Holder) holder).fromtext.setText(f.format(s.getTimeofMessage()).split(" ")[1]);


                if (GoogleSignIn.getLastSignedInAccount(context).getDisplayName() != null
                        && s.getFrom().equals(GoogleSignIn.getLastSignedInAccount(context).getDisplayName())) {
                    ((Holder) holder).frameLayout.setGravity(Gravity.END);
                    ((Holder) holder).imageButton.setVisibility(View.VISIBLE);
                    if(s.getSent_status()!=null && !s.getSent_status().equals("")) {
                        if (s.getSent_status().equals("sent")) {
                            ((Holder) holder).imageButton.setImageResource(R.drawable.sent);
                        }
                        if (s.getSent_status().equals("seen"))
                            ((Holder) holder).imageButton.setImageResource(R.drawable.delivered);
                    }
//                    ViewGroup.LayoutParams marginLayoutParams= ((Holder) holder).linearLayout.getLayoutParams();
//                    ((ViewGroup.MarginLayoutParams) marginLayoutParams).leftMargin= (int) (chatFragment.getResources().getDisplayMetrics().widthPixels*((float)1/4));
//                    ((Holder) holder).linearLayout.setLayoutParams(marginLayoutParams);

                } else {
                    ((Holder) holder).frameLayout.setGravity(Gravity.START);
                    ((Holder) holder).imageButton.setVisibility(View.INVISIBLE);
//                    ViewGroup.LayoutParams marginLayoutParams= ((Holder) holder).linearLayout.getLayoutParams();
//                    ((ViewGroup.MarginLayoutParams) marginLayoutParams).rightMargin= (int) (chatFragment.getResources().getDisplayMetrics().widthPixels*((float)1/4));
//                    ((Holder) holder).linearLayout.setLayoutParams(marginLayoutParams);
                }
            } else if (getItemViewType(position) == 2) {
                if (s.getUri() != null) {
                    ((ImageviewHolder) holder).progressBar.setVisibility(View.VISIBLE);
                    if (GoogleSignIn.getLastSignedInAccount(context).getDisplayName() != null
                            && s.getFrom().equals(GoogleSignIn.getLastSignedInAccount(context).getDisplayName())) {
                        ((ImageviewHolder) holder).frameLayout.setGravity(Gravity.END);

                    } else {
                        ((ImageviewHolder) holder).frameLayout.setGravity(Gravity.START);
                    }
                    if (s.getNew_message()) {
                        final StorageReference storageReference = FirebaseStorage.getInstance().getReference("images").child(s.getUri());
                        final File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM), s.getUri());
                        try {
                            if (s.getNew_message() && !file.exists())
                                file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (file.exists()) {

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
                                                s.setNew_message(false);
                                                list.add(s);
                                                Database.getInstance(context).dao().update(s);
                                            }
                                        });
                                        thread.start();
                                        InputStream inputStream = chatFragment.getContentResolver().openInputStream(Uri.fromFile(file));

//                                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(
//                                            BitmapFactory.decodeStream(inputStream), (int) convertDpToPx(context,300), (int) convertDpToPx(context,350), false);

                                        ((ImageviewHolder) (holder)).imageView.setImageBitmap(scaleCenterCrop(BitmapFactory.decodeStream(inputStream), (int) convertDpToPx(context, 300), (int) convertDpToPx(context, 300)));
                                        ((ImageviewHolder) holder).progressBar.setVisibility(View.GONE);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });


                        }
                    } else {
                        access_image_from_local_db(holder, s);

                    }
                }

            }


        }
    }

    private void access_image_from_local_db(@NonNull final RecyclerView.ViewHolder holder, final Entity s) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Uri uri = Uri.parse(Database.getInstance(context).dao().getdatabyid(s.getId()).getLocalpath());
                InputStream inputStream = null;
                try {
                    inputStream = chatFragment.getContentResolver().openInputStream(uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                final InputStream finalInputStream = inputStream;
                chatFragment.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (finalInputStream != null) {
                            ((ImageviewHolder) (holder)).imageView.setImageBitmap(scaleCenterCrop(BitmapFactory.decodeStream(finalInputStream), (int) convertDpToPx(context, 300), (int) convertDpToPx(context, 300)));
                            ((ImageviewHolder) (holder)).progressBar.setVisibility(View.GONE);
                        } else
                            Toast.makeText(context, "could not load image", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        thread.start();
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
        ImageButton imageButton;

        Holder(@NonNull View itemView) {
            super(itemView);
            Log.d("viewTypeentity", "inviewholder");
            fromtext = itemView.findViewById(R.id.fromwhom);
            messageText = itemView.findViewById(R.id.messagereceived);
            linearLayout = itemView.findViewById(R.id.chat_linear_layout);
            frameLayout = itemView.findViewById(R.id.chat_frame_layout);
            imageButton=itemView.findViewById(R.id.deliveryReport);
            Log.d("viewTypeentity", "declareditems");
        }


    }

    class ImageviewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        LinearLayout frameLayout;
        ProgressBar progressBar;

        public ImageviewHolder(@NonNull View itemView) {

            super(itemView);


            imageView = itemView.findViewById(R.id.chat_image);
            frameLayout = itemView.findViewById(R.id.frame_layout);
            progressBar = itemView.findViewById(R.id.imageloadingprogress);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
//                            try {
//                              //  Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
                            Intent intent = new Intent(chatFragment, ShowFullImageOnClickInChat.class);
                            Entity entity = (Entity) list.get(getAdapterPosition());
                            //  Uri uri = Uri.parse(Database.getInstance(context).dao().getdatabyid(entity.getId()).getLocalpath());

                            intent.setData(Uri.parse(entity.getLocalpath()));

                            context.startActivity(intent);
                        }
                    });
                    thread.start();
                }
            });
        }
    }
}