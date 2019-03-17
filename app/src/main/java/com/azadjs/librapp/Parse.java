package com.azadjs.librapp;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Parse extends AsyncTask<Void,Void,Void> {
    static String eName,eImage;

    public String geteName() {
        return eName;
    }

    public void seteName(String eName) {
        this.eName = eName;
    }

    public String geteImage() {
        return eImage;
    }

    public void seteImage(String eImage) {
        this.eImage = eImage;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Document document = Jsoup.connect(AddAppDialog.appUrl.getText().toString()).get();
            Element eNameElement = document.select("h1.AHFaub>span").first();
            String eName = eNameElement.text();
            seteName(eName);

            System.out.println(eName);

            Elements eImageElement = document.select("div.dQrBL img[src]");
            String eImage = eImageElement.attr("src");
            seteImage(eImage);
            System.out.println(eImage);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
