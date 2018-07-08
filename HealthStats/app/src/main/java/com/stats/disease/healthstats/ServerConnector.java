package com.stats.disease.healthstats;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import java.net.HttpURLConnection;

/**
 * Created by parjanya on 23/10/16.
 */

abstract class ServerConnector extends AsyncTask<String, Void, String>{

    private String getQuery(List<Pair<String, String>> params) throws UnsupportedEncodingException {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Pair<String, String> pair : params) {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(pair.first, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.second, "UTF-8"));
        }

        return result.toString();
    }

    @Override
    protected String doInBackground(String... params) {
        try {

            URL serverURL = new URL(params[0]);
            String queryString = params[1];

            if (queryString.equals("") || params[0].equals(""))
                return null;

            Log.i("ServerHandler","inputquery"+queryString);

            List<Pair<String, String>> queryParams = new ArrayList<>();
            queryParams.add(new Pair<>(Constants.SERVER_QUERY_POST_KEY,queryString));

            HttpURLConnection serverConnection = (HttpURLConnection)serverURL.openConnection();
            serverConnection.setRequestMethod("POST");
            serverConnection.setDoInput(true);
            serverConnection.setDoOutput(true);

            OutputStream serverOutputStream = serverConnection.getOutputStream();
            BufferedWriter serverWriter = new BufferedWriter(new OutputStreamWriter(serverOutputStream, "UTF-8"));
            serverWriter.write(getQuery(queryParams));
            serverWriter.flush();
            serverWriter.close();
            serverOutputStream.close();

            serverConnection.connect();

            BufferedReader serverReader = new BufferedReader(new InputStreamReader(serverConnection.getInputStream()));
            StringBuilder serverReply = new StringBuilder("");
            String line = "";

            while ((line = serverReader.readLine())!=null){
                serverReply.append(line);
            }

            Log.i("ServerHandler","inputquery"+serverReply.toString());

            serverReader.close();
            return serverReply.toString();
        } catch (Exception e){
            Log.i("ServerHandler","Exception"+e.getMessage());
            return null;
        }
    }
}
