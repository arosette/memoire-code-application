package com.example.strongauth;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SetPhoneIdActivity extends Activity {

	public static final int REQUEST_CODE = 1;
	private EditText phoneIdEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_phone_id);

		phoneIdEditText = (EditText) findViewById(R.id.phone_id_text);
	}

	public void setPhoneId(View view) {
		File file = new File(getApplicationContext().getFilesDir(),
				MainActivity.phoneIdFilename);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			String phoneId = phoneIdEditText.getText().toString();
			bw.write(phoneId + "\n");
			bw.close();
			setResult(RESULT_OK, getIntent().putExtra("PHONE_ID", phoneId));
			finish();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
