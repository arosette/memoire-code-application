package com.example.strongauth;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import android.os.AsyncTask;

public class SendHMAC extends AsyncTask<String, Void, String> {

	@Override
	protected String doInBackground(String... params) {
		String serverAddress = params[0];
		String hash = params[1];
		String challengeBase64 = params[2];

		String responseString = null;
		try {
			URL url = new URL("https://" + serverAddress + "/authmobile");

			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

			con.setRequestMethod("POST");
			con.setDoOutput(true);
			String encodedHash = URLEncoder.encode(hash, "UTF-8");
			String encodedChallengeBase64 = URLEncoder.encode(challengeBase64, "UTF-8");
			String content = "mac=" + encodedHash + "&challenge=" +
					encodedChallengeBase64;

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(content);
			wr.flush();
			wr.close();

			// Receive response from server
			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			responseString = response.toString();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseString;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
	}
}
