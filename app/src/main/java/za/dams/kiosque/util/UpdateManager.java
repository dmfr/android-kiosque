package za.dams.kiosque.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UpdateManager {

    /*
    private class DownloadUpdateTask extends AsyncTask<Void, Integer, String> {
        protected void onPreExecute(){
            mProgressDialog = ProgressDialog.show(
                    MainMenuActivity.this,
                    "Checking for update",
                    "Please Wait",
                    true);
        }

        protected String doInBackground(Void... Params ) {
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
            HttpConnectionParams.setSoTimeout(httpParameters, 10000);

            URL url;
            try {
                url = new URL(getString(R.string.apk_update_url));
            } catch (MalformedURLException e1) {
                // TODO Auto-generated catch block
                return null ;
            }
            HttpURLConnection urlConnection;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                return null ;
            }
            byte[] data = new byte[0] ;
            try {
                InputStream inputStream = urlConnection.getInputStream() ;

                ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

                // this is storage overwritten on each iteration with bytes
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];

                // we need to know how may bytes were read to write them to the byteBuffer
                int len = 0;
                while ((len = inputStream.read(buffer)) != -1) {
                    byteBuffer.write(buffer, 0, len);
                }
                data = byteBuffer.toByteArray();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
            }
            finally {
                urlConnection.disconnect();
            }

            if( data.length < 1 ) {
                return null ;
            }


            // write apk to the file system
            FileOutputStream fos ;
            try {
                fos = openFileOutput("paracrm.apk", Context.MODE_WORLD_READABLE);
            } catch (FileNotFoundException e) {
                return null ;
            }
            try {
                fos.write(data);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                return null ;
            }

            String fileAbsPath = "file://" + getFilesDir().getAbsolutePath() + "/" +
                    "paracrm.apk";

            return fileAbsPath ;
        }

        protected void onProgressUpdate(Integer... progress ) {
            // setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String fileAbsPath) {
            mProgressDialog.dismiss() ;
            if( fileAbsPath == null ){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainMenuActivity.this);
                builder.setMessage("Nothing to update")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                return ;
            }

            // showDialog("Downloaded " + result + " bytes");

            Intent intent = new Intent() ;
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(fileAbsPath),
                    "application/vnd.android.package-archive");
            startActivity(intent);
        }
    }
    */

}
