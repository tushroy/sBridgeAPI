package ch.nych.soundtransmitter.transmitter.tasks;

import ch.nych.soundtransmitter.transmitter.Transmitter;
import ch.nych.soundtransmitter.transmitter.message.Message;

/**
 * Created by nych on 4/6/16.
 */
public class ModulationTask extends TransmissionTask {

    public ModulationTask(Transmitter transmitter, Message message) {
        super(transmitter, message, TransmissionTask.MODULATION_TASK);
    }

    @Override
    protected TransmissionTask getNextTask() {
        return null;
    }

    @Override
    public void run() {

    }
}
