package ch.nych.soundtransmitter.receiver.tasks.analyzation.interpreter;

import ch.nych.soundtransmitter.receiver.tasks.Frame;

/**
 * Created by nych on 5/1/16.
 */
public abstract class Interpreter {

    protected Frame frame = null;
    protected double[][] frameData = null;

    public Interpreter(final Frame frame) {
        this.frame = frame;
        this.frameData = this.frame.getProcessedData();
    }

    protected int getMaxInRow(final int index) {
        int maxIndex = 0;
        for(int i = 0; i < this.frameData.length; i++) {
            if(this.frameData[i][index] > this.frameData[maxIndex][index]) {
                maxIndex = i;
            }
        }
        if(this.frameData[maxIndex][index] <= 0) {
            maxIndex = -1;
        }
        return maxIndex;
    }

    public abstract boolean interpretBytes();
}
