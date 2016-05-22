package ch.nych.demoApp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import ch.nych.TransmitterListener;
import ch.nych.soundtransceiver.R;
import ch.nych.soundtransceiver.transmitter.Transmitter;
import ch.nych.soundtransceiver.util.Configuration;
import ch.nych.soundtransceiver.util.Message;

public class SoundBeaconActivity extends AppCompatActivity
		implements TransmitterListener{

	private final static String LOG_TAG = "sBeaconActivity";

	private Button startButton = null;
	private Button stopButton = null;
	private EditText beaconMessage = null;
	private Spinner interMessageGapSpinner = null;

	private SoundBeacon soundBeacon = null;
	private Transmitter transmitter = null;
	private Thread transmitterThread = null;
	private Configuration configuration = null;

	/**
	 *
	 */
	private void startSoundBeacon() {
		this.soundBeacon.initSoundBeacon(transmitter,
				(SoundBeacon.InterMessageGap)
						interMessageGapSpinner.getSelectedItem());

		this.soundBeacon.setBeaconMessage(beaconMessage.getText().toString());

		this.transmitterThread = new Thread(soundBeacon);
		this.transmitterThread.start();
	}

	/**
	 *
	 * @throws InterruptedException
	 */
	private void stopSoundBeacon() throws InterruptedException {
		transmitterThread.interrupt();
		transmitterThread.join();
	}

	/**
	 *
	 */
	private void initView() {
		this.startButton = (Button) this.findViewById(R.id.start_button);
		this.startButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startSoundBeacon();
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
				interMessageGapSpinner.setEnabled(false);
				beaconMessage.setEnabled(false);
			}
		});

		this.stopButton = (Button) this.findViewById(R.id.stop_button);
		this.stopButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					stopSoundBeacon();
					startButton.setEnabled(true);
					stopButton.setEnabled(false);
					interMessageGapSpinner.setEnabled(true);
					beaconMessage.setEnabled(true);
				} catch(InterruptedException e) {
					Log.d(SoundBeaconActivity.LOG_TAG, "Thread interrupted " +
							"while trying to stop the SoundBeacon");
				}
			}
		});

		this.beaconMessage = (EditText) this.findViewById(R.id.beacon_message);

		this.interMessageGapSpinner =
				(Spinner) this.findViewById(R.id.interMessageGapSpinner);
		this.interMessageGapSpinner.setAdapter(
				new ArrayAdapter<SoundBeacon.InterMessageGap>(this,
						R.layout.support_simple_spinner_dropdown_item,
						SoundBeacon.InterMessageGap.values()));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sound_beacon_ativity);
		this.initView();
		this.soundBeacon = new SoundBeacon();
		this.transmitter = new Transmitter();
		transmitter.addTransmitterListener(this);
		this.configuration =(Configuration)
				this.getIntent().getExtras().getSerializable("Configuration");
	}

	@Override
	protected void onStart() {
		super.onStart();
		if(this.transmitter.initTransmitter(this.configuration)) {
			this.startButton.setEnabled(true);
		} else {
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast toast = Toast.makeText(getApplicationContext(),
							"Could not initialize Transmitter",
							Toast.LENGTH_LONG);
					toast.show();
				}
			});
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		try {
			this.stopSoundBeacon();
		} catch(InterruptedException e) {
			Log.d(SoundBeaconActivity.LOG_TAG, "Thread interrupted while" +
					"trying to stop the SoundBeacon");
		} finally {
			this.transmitter.shutdownAndAwaitTermination();
		}
	}

	@Override
	public void messageSent(Message message) {
		if(message.getMessageState() == Message.MessageState.INVALID_CHECKSUM ||
				message.getMessageState() == Message.MessageState
						.SENDING_ABORT) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Transmission interrupted, shutdown SoundBeacon",
					Toast.LENGTH_LONG);
			toast.show();
			try {
				this.stopSoundBeacon();
			} catch(InterruptedException e) {
				Log.d(SoundBeaconActivity.LOG_TAG, "Thread interrupted while" +
						"trying to stop the SoundBeacon");
			}  finally {
				this.transmitter.shutdownAndAwaitTermination();
			}
		}
	}
}