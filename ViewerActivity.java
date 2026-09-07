package com.tanzeb.gallery;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;

public class ViewerActivity extends Activity {
    Uri uri;
    boolean video;
    ImageView image;
    VideoView player;
    Button fav, del;

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.activity_viewer);
        image=findViewById(R.id.image);
        player=findViewById(R.id.video);
        fav=findViewById(R.id.favorite);
        del=findViewById(R.id.delete);

        uri=getIntent().getData();
        video=getIntent().getBooleanExtra("video",false);

        if(video){
            image.setVisibility(View.GONE);
            player.setVisibility(View.VISIBLE);
            player.setVideoURI(uri);
            player.setMediaController(new MediaController(this));
            player.start();
        }else{
            player.setVisibility(View.GONE);
            image.setVisibility(View.VISIBLE);
            image.setImageURI(uri);
        }

        updateFav();
        fav.setOnClickListener(v->{Favorites.toggle(this,uri.toString());updateFav();});
        del.setOnClickListener(v->deleteMedia());
    }

    void updateFav(){
        fav.setText(Favorites.has(this,uri.toString()) ? "♥ Unfavorite" : "♥ Favorite");
    }

    void deleteMedia(){
        try{
            if(Build.VERSION.SDK_INT>=30){
                PendingIntentSender.deleteWithRequest(this,uri);
            }else{
                getContentResolver().delete(uri,null,null);
                finish();
            }
        }catch(Exception e){
            Toast.makeText(this,"Delete not available",Toast.LENGTH_SHORT).show();
        }
    }
}

class PendingIntentSender{
    static void deleteWithRequest(Activity a, Uri uri){
        android.app.PendingIntent pi = MediaStore.createDeleteRequest(
                a.getContentResolver(),
                java.util.Collections.singletonList(uri)
        );
        try{
            a.startIntentSenderForResult(pi.getIntentSender(),99,null,0,0,0);
        }catch(Exception e){
            Toast.makeText(a,"Delete cancelled",Toast.LENGTH_SHORT).show();
        }
    }
}
