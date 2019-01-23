package com.example.cyberogg.samsungproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

public class LoadGame extends AsyncTask<Void, Void, Boolean> {
    private Activity activity;
    private ProgressDialog pd;
    private Class cl;
    private int type = 0;

    public LoadGame(Activity activity, Class cl, int type) {
        this.activity = activity;
        this.cl = cl;
        this.type = type;
    }

    @Override
    protected void onPreExecute() {
        pd = ProgressDialog.show(activity, "Loading",
                "Please wait", true, false);
        pd.show();
    }

    @Override
    protected Boolean doInBackground(Void... arg0) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (pd.isShowing())
            pd.dismiss();
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Intent i = new Intent(activity, cl);
        i.putExtra("Game type", type);
        activity.startActivity(i);
        activity.finish();
        //Toast.makeText(activity, Boolean.toString(result), Toast.LENGTH_LONG).show();
    }
}