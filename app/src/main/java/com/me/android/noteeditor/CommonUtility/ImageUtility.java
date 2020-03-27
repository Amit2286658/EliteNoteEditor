package com.me.android.noteeditor.CommonUtility;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;

@Deprecated
public class ImageUtility {

    public static final String IMG_TAG = "img";
    public static final String SRC_ATTRIBUTE = "src";

    /*public String htmlContent;*/

    private onSuccessfulImageLoadListener mOnSuccessfulImageLoadListener;

    final String KEY = "key";

    HashMap<String, Bitmap> hashMap = new HashMap<>();

    /*private ImageView imageView;
    private Context context;*/

    public ImageUtility(/*Context context, String htmlContent*/){
        /*this.context = context;
        this.htmlContent = htmlContent;*/
    }

    public String getFirstImageSource(String htmlContent){
        Document document = Jsoup.parse(htmlContent);
        Element imageElement = document.select(IMG_TAG).first();
        if (htmlContent.contains("<"+IMG_TAG+" "+SRC_ATTRIBUTE)) {
            return imageElement.attr(SRC_ATTRIBUTE);
            //imageElement.absUrl(SRC_ATTRIBUTE);
        } else
            return "";
    }

    public static String getFirstImage(String htmlContent){
        Document document = Jsoup.parse(htmlContent);
        Element element = document.select("img").first();
        if (htmlContent.contains("<img src")) {
            return element.attr("src");
        }else return "";
    }

    /*public String getFirstLink() {
        return utilityClass.getStorage_link();
    }

    public void putImage(ImageView imageView){
        this.imageView = imageView;
    }

    public ImageView getImage(){
        return this.imageView;
    }*/

    public void loadInBackground(Context context, String htmlContent, ImageView imageView, ImageUtility imageUtility){
        if (context == null) throw new IllegalStateException("Please provide a context");
        if (htmlContent == null) throw new IllegalStateException("Please pass in the html content");

        String uri_or_url = getFirstImageSource(htmlContent);
        Object[] objects = new Object[]{context, uri_or_url, imageUtility, imageView};

        if (!uri_or_url.isEmpty()) {
            loadImageInBackground loadImageInBackground = new loadImageInBackground();
            loadImageInBackground.execute(objects);
        }else return;
    }

    public void confirmSuccessfulImageLoad(Bitmap bitmap, String link){
        mOnSuccessfulImageLoadListener.onSuccessfulImageLoad(bitmap, link);
    }

    public void setOnSuccessfulImageLoadListener(onSuccessfulImageLoadListener onSuccessfulImageLoadListener){
        this.mOnSuccessfulImageLoadListener =  onSuccessfulImageLoadListener;
    }

    public interface onSuccessfulImageLoadListener{
        void onSuccessfulImageLoad(Bitmap bitmap, String uri_or_url);
    }
}

@Deprecated
class loadImageInBackground extends AsyncTask<Object, Integer, Object[]>{


    /**
     * Runs on the UI thread before {@link #doInBackground}.
     *
     * @see #onPostExecute
     * @see #doInBackground
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param objects The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected Object[] doInBackground(Object... objects) {
        Context context = (Context) objects[0];
        String link = (String) objects[1];
        ImageUtility imageUtility = (ImageUtility) objects[2];
        ImageView imageView = (ImageView) objects[3];

        Object[] objects1 = new Object[4];

        //int Pix = utilityClass.getDipValue(context, 48);

        if (link.startsWith("http")){
            try {
                //URL url = new URL(link);
                Bitmap bitmap = com.me.android.noteeditor.CommonUtility.utilityClass.getImageThumbnail(context, link, 48);
                /*if (imageUtility.hashMap.get(imageUtility.KEY) == null) {
                    bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    bitmap = ThumbnailUtils.extractThumbnail(bitmap, Pix, Pix);
                    imageUtility.hashMap.put(imageUtility.KEY, bitmap);
                }else {
                    bitmap = imageUtility.hashMap.get(imageUtility.KEY);
                }*/
                objects1[0] = bitmap;
                objects1[1] = link;
                objects1[2] = imageUtility;
                objects1[3] = imageView;
            } catch (IOException e){
                e.printStackTrace();
            }
        }else {
            //Uri uri = Uri.parse(link);
            try {
                Bitmap bitmap = com.me.android.noteeditor.CommonUtility.utilityClass.getImageThumbnail(context, link, 48);
                /*if (imageUtility.hashMap.get(imageUtility.KEY) == null) {
                    InputStream inputStream = context.getContentResolver().openInputStream(uri);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    bitmap = ThumbnailUtils.extractThumbnail(bitmap, Pix, Pix);
                    imageUtility.hashMap.put(imageUtility.KEY, bitmap);
                }else {
                    bitmap = imageUtility.hashMap.get(imageUtility.KEY);
                }*/
                objects1[0] = bitmap;
                objects1[1] = link;
                objects1[2] = imageUtility;
                objects1[3] = imageView;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return objects1;
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.</p>
     *
     * <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param objects The result of the operation computed by {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @Override
    protected void onPostExecute(Object[] objects) {
        super.onPostExecute(objects);
        Bitmap bitmap = (Bitmap) objects[0];
        String link = (String) objects[1];
        ImageUtility imageUtility = (ImageUtility) objects[2];
        ImageView imageView = (ImageView) objects[3];

        imageUtility.confirmSuccessfulImageLoad(bitmap, link);
        /*imageUtility.confirmSuccessfulImageLoad(bitmap, link);*/

        /*if (imageUtility.getImage() != null)
            imageUtility.getImage().setImageBitmap(bitmap);
        else*/
            /*imageUtility.confirmSuccessfulImageLoad(bitmap, link);*/



        /*imageView.setImageBitmap(bitmap);

        imageView.setVisibility(View.VISIBLE);*/
    }
}
