package ch.nych.soundtransmitter.transmitter.tasks;

import android.util.Log;

import ch.nych.soundtransmitter.transmitter.Transmitter;
import ch.nych.soundtransmitter.transmitter.message.Message;
import ch.nych.soundtransmitter.transmitter.tone.Tone;

/**
 * Created by nych on 4/6/16.
 */
public class SendingTask extends TransmissionTask {

    public SendingTask(Transmitter transmitter, Message message) {
        super(transmitter, message, TransmissionTask.SENDING_TASK);
    }

    @Override
    protected TransmissionTask getNextTask() {
        return null;
    }

    @Override
    public void run() {
        Log.i("MyTag", "ID:\t" + this.message.getMessageId() + " - sending tone sequence");
        this.transmitter.getAudioTrack().play();
        for(Tone tone : this.message.getToneSequence()) {
            this.transmitter.getAudioTrack().write(tone.getSamples(), 0, tone.getLength());
        }
        Log.i("MyTag", "ID:\t" + this.message.getMessageId() + " - sent tone sequence");
    }
}
