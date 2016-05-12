package ch.nych.soundtransceiver.transmitter.tasks.notification;

import ch.nych.soundtransceiver.transmitter.Transmitter;
import ch.nych.soundtransceiver.transmitter.tasks.Message;
import ch.nych.soundtransceiver.transmitter.tasks.TransmissionTask;

/**
 * This class represents the final task of the transmission process. So far it is just used for the
 * final callback and has no more functionality.
 * Created by nych on 4/24/16.
 */
public class NotificationTask extends TransmissionTask {

    /**
     * Default constructor
     * @param transmitter    the reference to the calling {@link Transmitter} instance is used for
     *                       the shared resources and the callback.
     * @param message        The {@link Message} instance, containing the data to modulate
     */
    public NotificationTask(Transmitter transmitter, Message message) {
        super(transmitter, message, TaskType.NOTIFICATION);
    }

    @Override
    protected TransmissionTask getNextTask() {
        return null;
    }

    @Override
    public void run() {}
}
