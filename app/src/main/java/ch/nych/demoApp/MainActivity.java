package ch.nych.demoApp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import ch.nych.soundtransceiver.R;
import ch.nych.soundtransceiver.util.Configuration;

public class MainActivity extends AppCompatActivity {

	private Button receivingActivityButton = null;
	private Button broadcastActivityButton = null;
	private Button soundBeaconActivityButton = null;
	private Spinner configurationSpinner = null;

	private void setConfiguration(final Intent intent) {
		if(this.configurationSpinner.getSelectedItem().equals("Inaudible")) {
			intent.putExtra("Configuration",
					Configuration.newInaudibleConfiguration());
		} else {
			intent.putExtra("Configuration",
					Configuration.newAudibleConfiguration());
		}
	}

	private void initView() {
		this.receivingActivityButton =
				(Button) this.findViewById(R.id.receiverActivityButton);
		if(this.receivingActivityButton != null) {
			this.receivingActivityButton.setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(v.getContext(),
									ReceivingActivity.class);
							setConfiguration(intent);
							v.getContext().startActivity(intent);
						}
					});
		}

		this.broadcastActivityButton =
				(Button) this.findViewById(R.id.broadcastActivityButton);
		if(this.broadcastActivityButton != null) {
			this.broadcastActivityButton.setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(v.getContext(),
									BroadcastActivity.class);
							setConfiguration(intent);
							v.getContext().startActivity(intent);
						}
					});
		}

		this.soundBeaconActivityButton =
				(Button) this.findViewById(R.id.soundBeaconButton);
		if(this.soundBeaconActivityButton != null) {
			this.soundBeaconActivityButton.setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(v.getContext(),
									SoundBeaconActivity.class);
							setConfiguration(intent);
							v.getContext().startActivity(intent);
						}
					});
		}

		this.configurationSpinner =
				(Spinner) this.findViewById(R.id.configurationSpinner);
		this.configurationSpinner.setAdapter(
				ArrayAdapter.createFromResource(this,
						R.array.configuration_array,
						R.layout.support_simple_spinner_dropdown_item));
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		this.initView();
    }
}
