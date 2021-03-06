package ch.nych.demoApp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ch.nych.TransmitterListener;
import ch.nych.soundtransceiver.R;
import ch.nych.soundtransceiver.transmitter.Transmitter;
import ch.nych.soundtransceiver.util.Message;
import ch.nych.soundtransceiver.util.Configuration;

public class Transmission extends AppCompatActivity
        implements TransmitterListener {

	private Transmitter transmitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_transmission);
        this.transmitter = new Transmitter();
        this.transmitter.addTransmitterListener(this);

        final Button btn_send =
                (Button) this.findViewById(R.id.btn_send);
        final EditText txt_message =
                (EditText) this.findViewById(R.id.editText);

        if (btn_send != null) {
            btn_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    byte[] message = null;
                    char[] chars =
                            txt_message.getText().toString().toCharArray();
                    message = new byte[chars.length];
                    for (int i = 0; i < message.length; i++) {
                        message[i] = (byte) chars[i];
                    }
                    transmitter.transmitData(message);
                }
            });
        }
    }

	@Override
	protected void onStart() {
		super.onStart();
		if(!this.transmitter.initTransmitter(Configuration
				.newUltrasonicConfiguration())) {
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast toast = Toast.makeText(getApplicationContext(),
							"Couldn't initialize Transmitter",
							Toast.LENGTH_SHORT);
					toast.show();
				}
			});
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		this.transmitter.shutdownAndAwaitTermination();
	}

    @Override
    public void messageSent(final Message message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Transmission terminated with state: " +
                        message.getMessageState(), Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }
}
