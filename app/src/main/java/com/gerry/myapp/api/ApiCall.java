package com.gerry.myapp.api;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gerry.myapp.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by gerry on 19/5/16.
 */

    public class ApiCall extends AsyncTask<String,Void, String> {

     ApiCallBack callback;
        Context ctx;
        private TransparentProgressDialog pd;
        boolean success = false;
    InputStream is;

     public ApiCall(Context ctx, ApiCallBack callback) {
            this.callback = callback;
            this.ctx = ctx;
        }


        @Override
        protected String doInBackground(String... params) {
            String json = null;
            String targetURL = params[0];
            String urlParameters = params.length > 1 ? params[1] : null;

            try {


                URL url = new URL(targetURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);



                if (urlParameters != null && urlParameters.length() > 0) {
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
                    conn.setRequestProperty("Content-Language", "en-US");

                    conn.setUseCaches(false);
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    //Send request
                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                    wr.writeBytes(urlParameters);
                    wr.flush();
                    wr.close();

                } else {
                    // Starts the query
                    conn.connect();
                    int response = conn.getResponseCode();

                    if(response >= HttpURLConnection.HTTP_BAD_REQUEST)
                        is = conn.getErrorStream();
                    else
                        is = conn.getInputStream();
                }

                json = readAsString(is);
                success = true;

            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                success = false;
            } catch (Exception e) {
                e.printStackTrace();

            }
            return json;
        }


            @Override
            protected void onPostExecute(String json) {

                //close the spinner
                if (pd.isShowing()) {
                    pd.dismiss();
                }


                if (!success) {
                    //show dialog no net connection
                    showSuccessDialog(ctx, "No network connection", "This application requires an internet connection.").show();
                }

                if (json != null) {
                    callback.processJson(json);
                }

            }



            @Override
            protected void onPreExecute() {
                pd = new TransparentProgressDialog(ctx, R.mipmap.spinner);
                pd.show();
            }



            // Helper method to read an inputstream as text
            public String readAsString(InputStream stream) throws IOException {
                InputStreamReader is = new InputStreamReader(stream);
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(is);
                String read = br.readLine();
                while (read != null) {
                    sb.append(read);
                    read = br.readLine();
                }
                return sb.toString();
            }



            private class TransparentProgressDialog extends Dialog {

                private ImageView iv;

                public TransparentProgressDialog(Context context, int resourceIdOfImage) {
                    super(context, R.style.TransparentProgressDialog);
                    WindowManager.LayoutParams wlmp = getWindow().getAttributes();
                    wlmp.gravity = Gravity.CENTER_HORIZONTAL;
                    getWindow().setAttributes(wlmp);
                    setTitle(null);
                    setCancelable(true);
                    LinearLayout layout = new LinearLayout(context);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    iv = new ImageView(context);
                    iv.setImageResource(resourceIdOfImage);
                    layout.addView(iv, params);
                    addContentView(layout, params);
                }

                @Override
                public void show() {
                    super.show();
                    RotateAnimation anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
                    anim.setInterpolator(new LinearInterpolator());
                    anim.setRepeatCount(Animation.INFINITE);
                    anim.setDuration(2000);
                    iv.setAnimation(anim);
                    iv.startAnimation(anim);
                }
            }


        private AlertDialog showSuccessDialog(final Context context, CharSequence title, CharSequence message) {
            // Creates a popup dialog
            AlertDialog.Builder downloadDialog = new AlertDialog.Builder(context);
            downloadDialog.setTitle(title);
            downloadDialog.setMessage(message);
            downloadDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                // Method below is the click handler for the YES button on the popup
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            return downloadDialog.show();
        }

    }

