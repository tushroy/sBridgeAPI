package ch.nych.soundtransmitter.receiver.tasks.analyzation.interpreter;

import java.util.ArrayList;
import java.util.List;

import ch.nych.soundtransmitter.receiver.tasks.Frame;
import ch.nych.soundtransmitter.util.Configuration;

/**
 * Created by nych on 5/4/16.
 */
public class ThreeChannelInterpreter extends Interpreter {

    public ThreeChannelInterpreter(Frame frame, Configuration configuration) {
        super(frame, configuration);
    }

    @Override
    protected List<Byte> mapData() {
        List<Byte> list = new ArrayList<Byte>();
        int index = 0;

        for(int i = 0; i < this.frameData[0].length; i++) {
            index = getMaxInRow(i);
            if (index == 1 || index == 3 || index == 5) {
                list.add((byte) 0);
            } else if (index == 2 || index == 4 || index == 6) {
                list.add((byte) 1);
            }
        }
        return list;
    }
}