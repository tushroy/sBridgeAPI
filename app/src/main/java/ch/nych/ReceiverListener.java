package ch.nych;

import ch.nych.soundtransceiver.util.Message;

/**
 * Created by nych on 5/13/16.
 */
public interface ReceiverListener {
	/**
	 *
	 * @param message
	 */
	public void messageReceived(final Message message);
}
