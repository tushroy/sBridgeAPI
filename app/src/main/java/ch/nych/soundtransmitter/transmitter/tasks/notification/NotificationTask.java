package ch.nych.soundtransmitter.transmitter.tasks.notification;

import ch.nych.soundtransmitter.transmitter.Transmitter;
import ch.nych.soundtransmitter.transmitter.tasks.Message;
import ch.nych.soundtransmitter.transmitter.tasks.TransmissionTask;

/**
 * Created by nych on 4/24/16.
 */
public class NotificationTask extends TransmissionTask {

    public NotificationTask(Transmitter transmitter, Message message) {
        super(transmitter, message, TransmissionTask.NOTIFICATION_TASK);
    }

    @Override
    protected TransmissionTask getNextTask() {
        return null;
    }

    @Override
    public void run() {}
}
