package ch.nych.soundtransmitter.receiver.tasks.analyzation;

import ch.nych.soundtransmitter.receiver.Receiver;
import ch.nych.soundtransmitter.receiver.tasks.ReceiverTask;

/**
 * Created by nych on 4/14/16.
 */
public class AnalyzationTask extends ReceiverTask {

    public AnalyzationTask(Receiver receiver) {
        super(receiver);
    }

    @Override
    public boolean initTask() {
        return false;
    }

    @Override
    public void run() {

    }
}
