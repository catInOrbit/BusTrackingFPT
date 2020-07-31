package com.bustracking.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

public class AsyncTaskDialog  extends AsyncTask<Void, Void, Void>
{
    private ProgressDialog progressDialog;

    public AsyncTaskDialog(Activity activity) {
        this.progressDialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage("Saving info");
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
