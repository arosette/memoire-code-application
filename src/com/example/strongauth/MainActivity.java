package com.example.strongauth;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.util.Base64;

public class MainActivity extends Activity {

	private Button scan;
	private static String phoneId = null;
	public final static String phoneIdFilename = "phoneid.txt";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		scan = (Button) findViewById(R.id.button_scan);
		final Activity act = this;
		scan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IntentIntegrator scanIntegrator = new IntentIntegrator(act);
				scanIntegrator.initiateScan();
			}
		});

		Context context = this;

		if (phoneId == null) {
			File phoneIDFile = new File(context.getFilesDir(), phoneIdFilename);

			if (phoneIDFile.exists()) {
				System.out.println("File exists");
				BufferedReader br;
				try {
					br = new BufferedReader(new FileReader(phoneIDFile));
					phoneId = br.readLine();
					System.out.println(phoneId);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				System.out.println("File does not exist");
				Intent intent = new Intent(this, SetPhoneIdActivity.class);
				startActivityForResult(intent, SetPhoneIdActivity.REQUEST_CODE);
			}
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == IntentIntegrator.REQUEST_CODE) {
			IntentResult scanningResult =
					IntentIntegrator.parseActivityResult(
							requestCode, resultCode, intent);
			if (scanningResult != null) {

				String contents = scanningResult.getContents();

				// Separate packed data
				String challengeBase64 = contents.substring(0, 88);

				System.out.println(challengeBase64);

				// Decode data from base64
				byte[] rawChallenge = Base64.decode(challengeBase64, Base64.DEFAULT);

				String serialNumber = phoneId;
				System.out.println("Serial Number : " + serialNumber);

				try {
					Mac mac = Mac.getInstance("HmacSHA256");
					SecretKeySpec secretKey = new SecretKeySpec(
							serialNumber.getBytes("UTF-8"), "HmacSHA256");
					mac.init(secretKey);

					String hash = Base64.encodeToString(
							mac.doFinal(rawChallenge), Base64.DEFAULT);

					System.out.println("Hash : " + hash);

					SendHMAC sendHMAC = new SendHMAC();
					System.out.println(
							sendHMAC.execute("authserver.network.lan", hash,
									challengeBase64).get());
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidKeyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				System.out.println("No scan data received !");
			}
		}

		else if (requestCode == SetPhoneIdActivity.REQUEST_CODE) {
			phoneId = intent.getExtras().getString("PHONE_ID");
			System.out.println(phoneId);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
