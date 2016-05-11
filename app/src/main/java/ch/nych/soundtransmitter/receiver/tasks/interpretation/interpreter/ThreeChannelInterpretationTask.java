package ch.nych.soundtransmitter.receiver.tasks.interpretation.interpreter;

import java.util.List;

import ch.nych.soundtransmitter.receiver.Receiver;
import ch.nych.soundtransmitter.receiver.tasks.Frame;
import ch.nych.soundtransmitter.receiver.tasks.interpretation.InterpretationTask;

/**
 * Created by nych on 5/4/16.
 */
public class ThreeChannelInterpretationTask extends InterpretationTask {

    public ThreeChannelInterpretationTask(final Receiver receiver, final Frame frame) {
        super(receiver, frame);
    }

    @Override
    protected int mapData(List<Byte> list, int from, int size) {
        int index = -1;
        int last = -1;

        for (int i = from; i < this.processedData[0].length; i++) {
            if ((index = getMaxInRow(i)) == last ||
                    this.processedData[index][i] < this.thresholds[index]) {
                continue;
            }
            if (index == 1 || index == 3 || index == 5) {
                list.add((byte) 0);
                if(list.size() > size) {
                    list.remove(list.size() - 1);
                    return i;
                }
            } else if(index == 2 || index == 4 || index == 6) {
                list.add((byte) 1);
                if(list.size() > size) {
                    list.remove(list.size() - 1);
                    return i;
                }
            }
            last = index;
        }
        return this.processedData[0].length;
    }
}
