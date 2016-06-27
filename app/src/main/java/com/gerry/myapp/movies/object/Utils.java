package com.gerry.myapp.movies.object;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.gerry.myapp.R;


public class Utils {

    public static AlertDialog showSuccessDialog(final Context context, int title, int message) {
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
