package ch.nych;

import ch.nych.soundtransmitter.receiver.tasks.Frame;
import ch.nych.soundtransmitter.transmitter.tasks.Message;

/**
 * Created by nych on 4/24/16.
 */
public interface BridgeListener {
    public void messageSent(final Message message);
    public void frameReceived(final Frame frame);
}
