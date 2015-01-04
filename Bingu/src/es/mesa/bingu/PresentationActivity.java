package es.mesa.bingu;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import es.mesa.bingu.config.Constants;

public class PresentationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_presentation);

		Button btn_instructions = (Button) findViewById(R.id.presentation_btn_instructions);
		btn_instructions.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// custom dialog
				final Dialog dialog = new Dialog(PresentationActivity.this);
				dialog.setContentView(R.layout.dialog_instructions);
				dialog.setTitle(R.string.dialog_instructions_title);

				((Button) dialog.findViewById(R.id.dialog_btn_ok))
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
							}
						});
				dialog.show();
			}
		});

		findViewById(R.id.presentation_btn_play).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent newGame = new Intent(getApplicationContext(),
								MainActivity.class);
						startActivity(newGame);
					}
				});

		findViewById(R.id.presentation_btn_exit).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						System.exit(0);
					}
				});

		findViewById(R.id.presentation_btn_email).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						sendEmail();
					}
				});
	}

	protected void sendEmail() {

		String[] recipients = { Constants.CONTACT_EMAIL };
		Intent email = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
		// prompts email clients only
		email.setType("message/rfc822");

		email.putExtra(Intent.EXTRA_EMAIL, recipients);
		email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.presentation_activity_email_title));
//		email.putExtra(Intent.EXTRA_TEXT, "");

		try {
			// the user can choose the email client
			startActivity(Intent.createChooser(email,
					getString(R.string.presentation_activity_email_client)));

		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(PresentationActivity.this, getString(R.string.presentation_activity_email_client_fail),
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

}
