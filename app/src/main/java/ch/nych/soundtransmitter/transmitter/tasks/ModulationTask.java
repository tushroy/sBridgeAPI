package ch.nych.soundtransmitter.transmitter.tasks;

import android.util.Log;

import ch.nych.soundtransmitter.transmitter.Transmitter;
import ch.nych.soundtransmitter.transmitter.message.Message;
import ch.nych.soundtransmitter.transmitter.tone.Tone;

/**
 * Created by nych on 4/6/16.
 */
public class ModulationTask extends TransmissionTask {

    public ModulationTask(Transmitter transmitter, Message message) {
        super(transmitter, message, TransmissionTask.MODULATION_TASK);
    }

    protected void fourStateModulation() {
        Log.i("MyTag", "ID:\t" + this.message.getMessageId() + " - prepare tone sequence");
        Tone[] toneSet = this.transmitter.getToneSet();
        byte[] preparedData = this.message.getPreparedData();
        Tone[] toneSequence = new Tone[preparedData.length];
        int tone = 0;

        for(int i = 0; i < toneSequence.length; i++) {
            tone = preparedData[i] % 4;
            tone += i % 2 == 0 ? 5 : 0;
            toneSequence[i] = toneSet[tone];
        }
        this.message.setToneSequence(toneSequence);
    }

    @Override
    protected TransmissionTask getNextTask() {
        return new SendingTask(this.transmitter, this.message);
    }

    @Override
    public void run() {
        if(this.transmitter.transmissionMode == Transmitter.TWO_STATE_TRANSMITTER) {

        } else if(this.transmitter.transmissionMode == Transmitter.FOUR_STATE_TRANSMITTER) {
            this.fourStateModulation();
        } else {
            //Something really went wrong
            System.err.println("Take a look at the run method of the modulationtask");
        }
        this.transmitterCallback();
    }
}
