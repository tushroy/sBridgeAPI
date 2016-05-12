package ch.nych;

import ch.nych.soundtransceiver.receiver.tasks.Frame;
import ch.nych.soundtransceiver.transmitter.tasks.Message;

/**
 * Created by nych on 4/24/16.
 */
public interface BridgeListener {
    /**
     *
     * @param message
     */
    public void messageSent(final Message message);

    /**
     *
     * @param frame
     */
    public void frameReceived(final Frame frame);
}
