package ch.nych;

import ch.nych.soundtransceiver.util.Message;

/**
 * Created by nych on 5/13/16.
 */
public interface TransmitterListener {
	/**
	 *
	 * @param message
	 */
	public void messageSent(final Message message);
}
