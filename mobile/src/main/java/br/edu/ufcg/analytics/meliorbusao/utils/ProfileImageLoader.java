package br.edu.ufcg.analytics.meliorbusao.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

public class ProfileImageLoader extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public ProfileImageLoader(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    /**
     * Coloca a imagem no perfil do usuario
     * @param urls
     * @return
     */
    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}