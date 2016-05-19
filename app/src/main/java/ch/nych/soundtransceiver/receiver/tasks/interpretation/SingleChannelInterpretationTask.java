package ch.nych.soundtransceiver.receiver.tasks.interpretation;

import android.util.Log;

import java.util.List;

import ch.nych.soundtransceiver.receiver.Receiver;
import ch.nych.soundtransceiver.util.Message;

/**
 * Created by nych on 5/4/16.
 */
public class SingleChannelInterpretationTask extends InterpretationTask {

    public SingleChannelInterpretationTask(final Receiver receiver,
                                           final Message message) {
        super(receiver, message);
    }

    @Override
    protected void interpretMessage(final List<Byte> list) {
        if(list == null) {
            Log.d(InterpretationTask.LOG_TAG, "list passed to " +
                    "interpretMessage was null");
            return;
        }
        Log.d(InterpretationTask.LOG_TAG, "interpretMessage");

        int index = -1;
        int last = -1;

        for (int i = 0; i < this.frequencyDomainData[0].length; i++) {
            if ((index = getMaxInRow(i)) == last ||
                    this.frequencyDomainData[index][i]
                            < this.thresholds[index]) {
                continue;
            }
            if (index == 1) {
                list.add((byte) 0);
            } else if(index == 2) {
                list.add((byte) 1);
            }
            last = index;
        }
    }
}
