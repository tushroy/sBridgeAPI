package ch.nych.soundtransmitter.transmitter.tasks.modulation;

import ch.nych.soundtransmitter.transmitter.Transmitter;
import ch.nych.soundtransmitter.transmitter.tasks.Message;
import ch.nych.soundtransmitter.transmitter.tasks.sending.SendingTask;
import ch.nych.soundtransmitter.transmitter.tasks.TransmissionTask;
import ch.nych.soundtransmitter.transmitter.tasks.modulation.tone.Tone;
import ch.nych.soundtransmitter.util.Configuration;

/**
 * Created by nych on 4/6/16.
 */
public class ModulationTask extends TransmissionTask {

    /**
     *
     * @param transmitter
     * @param message
     */
    public ModulationTask(final Transmitter transmitter, final Message message) {
        super(transmitter, message, TransmissionTask.MODULATION_TASK);
    }

    /**
     *
     */
    protected void fourStateModulation() {
        Tone[] toneSet = this.transmitter.getToneSet();
        byte[] preparedData = this.message.getPreparedData();
        Tone[] toneSequence = new Tone[preparedData.length + 4];
        int tone = 0;
        // TODO: 4/19/16 this will be changed to a chirped tone from 19000-19300
        toneSequence[0] = toneSet[4];
        toneSequence[1] = toneSet[4];
        for(int i = 0; i < preparedData.length; i++) {
            tone = preparedData[i] % 4;
            tone += i % 2 == 0 ? 5 : 0;
            toneSequence[i + 2] = toneSet[tone];
        }
        toneSequence[toneSequence.length - 2] = toneSet[4];
        toneSequence[toneSequence.length - 1] = toneSet[4];
        this.message.setToneSequence(toneSequence);
    }

    @Override
    protected TransmissionTask getNextTask() {
        return new SendingTask(this.transmitter, this.message);
    }

    @Override
    public void run() {
        if(this.transmitter.getConfiguration().getTransmissionMode() ==
                Configuration.TWO_STATE_TRANSMISSION) {

        } else if(this.transmitter.getConfiguration().getTransmissionMode() ==
                Configuration.FOUR_STATE_TRANSMISSION) {
            this.fourStateModulation();
        } else {
            // TODO: 4/12/16
        }
        this.transmitterCallback();
    }
}
