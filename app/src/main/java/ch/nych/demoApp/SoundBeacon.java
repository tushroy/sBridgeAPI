package ch.nych.demoApp;

import android.util.Log;

import ch.nych.soundtransceiver.transmitter.Transmitter;

/**
 * Created by nych on 5/19/16.
 */
public class SoundBeacon implements Runnable {

	/**
	 *
	 */
	private final static String LOG_TAG = "SoundBeacon";

	/**
	 *
	 */
	public enum InterMessageGap	{
		SHORT(100), MIDDLE(500), LONG(1000);

		private final long interMessageGap;

		InterMessageGap(final long interMessageGap) {
			this.interMessageGap = interMessageGap;
		}

		public long getInterMessageGap() {
			return this.interMessageGap;
		}
	}

	/**
	 *
	 */
	private Transmitter transmitter = null;

	/**
	 *
	 */
	private boolean initialized = false;

	/**
	 *
	 */
	private byte[] beaconMessage = null;

	/**
	 *
	 */
	private long interMessageGap = 0;

	/**
	 *
	 * @param beaconMessage
	 */
	public void setBeaconMessage(final String beaconMessage) {
		if(beaconMessage == null) {
			this.initialized = false;
			return;
		}
		char[] chars = beaconMessage.toCharArray();
		this.beaconMessage = new byte[chars.length];
		for(int i = 0; i < chars.length; i++) {
			this.beaconMessage[i] = (byte) chars[i];
		}
	}

	/**
	 *
	 */
	public SoundBeacon() {}

	/**
	 *
	 * @param transmitter
	 * @param interMessageGap
	 */
	public void initSoundBeacon(final Transmitter transmitter,
								   final InterMessageGap interMessageGap) {
		if(transmitter == null || interMessageGap == null) {
			Log.d(SoundBeacon.LOG_TAG, "invalid argument on initSoundBeacon()");
			return;
		}
		Log.d(SoundBeacon.LOG_TAG, "Initialize SoundBeacon" +
				"InterMessageGap:\t" + interMessageGap.getInterMessageGap());
		this.transmitter = transmitter;
		this.interMessageGap = interMessageGap.getInterMessageGap();
		this.initialized = true;
	}

	@Override
	public void run() {
		Log.d(SoundBeacon.LOG_TAG, "Start transmission");
		long sleepTime = this.transmitter.getConfiguration()
				.getMessageTransmissionTime();
		sleepTime += this.transmitter.getConfiguration()
				.getByteTransmissionTime() * this.beaconMessage.length;
		if(!this.initialized) {
			Log.d(SoundBeacon.LOG_TAG, "SoundBeacon was not initialized");
			return;
		}

		try {
			while(true) {
				this.transmitter.transmitData(this.beaconMessage);
				Thread.sleep(sleepTime + this.interMessageGap);
			}
		} catch(InterruptedException e) {
			Log.d(SoundBeacon.LOG_TAG, "SoundBeacon thread interrupted");
		}

		Log.d(SoundBeacon.LOG_TAG, "Stop transmission");
	}
}
