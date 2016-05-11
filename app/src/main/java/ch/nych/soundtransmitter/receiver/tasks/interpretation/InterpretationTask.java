package ch.nych.soundtransmitter.receiver.tasks.interpretation;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ch.nych.soundtransmitter.receiver.Receiver;
import ch.nych.soundtransmitter.receiver.tasks.Frame;
import ch.nych.soundtransmitter.receiver.tasks.ReceiverTask;
import ch.nych.soundtransmitter.util.Configuration;

/**
 * Created by nych on 5/1/16.
 */
public abstract class InterpretationTask extends ReceiverTask {

    /**
     *
     */
    protected final String logTag = Configuration.LOG_TAG + ":IntTask";

    /**
     *
     */
    protected Frame frame = null;

    /**
     *
     */
    protected double[][] processedData = null;

    /**
     *
     */
    protected double[] thresholds = null;

    /**
     *
     */
    protected byte[] preamble = null;

    /**
     * @param receiver
     */
    public InterpretationTask(final Receiver receiver, final Frame frame) {
        super(receiver);
        this.frame = frame;
        this.processedData = this.frame.getProcessedData();
        this.thresholds = new double[this.processedData.length];
        this.filterData();
        this.calcThresholds();
        this.preamble = this.configuration.getPreamble();
    }

    @Override
    public boolean initTask() {
        return false;
    }

    /**
     *
     */
    protected void filterData() {
        // TODO: 5/7/16 validation and dynamic
        double[] coefficients = configuration.getFilterCoefficients();
        double[][] originalData = this.frame.getOriginalData();
        int from = (coefficients.length / 2) + 1;
        int to = this.processedData[0].length - from;
        for(int i = 0; i < (this.processedData.length); i++) {
            for(int j = from; j < to; j++) {
                this.processedData[i][j] += originalData[i][j - 2] * coefficients[0];
                this.processedData[i][j] += originalData[i][j - 1] * coefficients[1];
                this.processedData[i][j] *= coefficients[2];
                this.processedData[i][j] += originalData[i][j + 1] * coefficients[3];
                this.processedData[i][j] += originalData[i][j + 2] * coefficients[4];
            }

        }
    }

    /**
     *
     */
    private void calcThresholds() {
        for(int i = 0; i < this.thresholds.length; i++) {
            for(int j = 0; j < this.processedData[i].length; j++) {
                this.thresholds[i] += Math.pow(this.processedData[i][j], 2);
            }
            this.thresholds[i] /= this.processedData[i].length;
            this.thresholds[i] = Math.sqrt(this.thresholds[i]);
        }
    }

    /**
     *
     * @param index
     * @return
     */
    protected int getMaxInRow(final int index) {
        int maxIndex = 0;
        for(int i = 0; i < this.processedData.length; i++) {
            if(this.processedData[i][index] >
					this.processedData[maxIndex][index]) {
                maxIndex = i;
            }
        }
        if(this.processedData[maxIndex][index] <= 0) {
            maxIndex = -1;
        }
        return maxIndex;
    }

    private boolean compareAgainstPreamble(List<Byte> dataBytes) {
        if(this.preamble.length != dataBytes.size()) {
            return false;
        }
        for(int i = 0; i < this.preamble.length; i++) {
            if(dataBytes.get(i) != this.preamble[i]) {
                return false;
            }
        }
        dataBytes.clear();
        return true;
    }

    /**
     *
     * @param list
     * @return
     */
    private byte[] mergeBits(final List<Byte> list) {
        byte[] bytes = new byte[list.size() / 8];
        int temp = 0;
        int j = 0;

        for(int i = 1; i <= list.size(); i++) {
            temp = temp << 1;
            temp = temp | list.get(i - 1);
            if(i % 8 == 0) {
                Log.d(this.logTag, Integer.toHexString(temp));
                bytes[j++] = (byte) temp;
                temp = 0;
            }
        }
        return bytes;
    }

    /**
     *
     * @return
     */
    protected abstract int mapData(final List<Byte> list,
                                   final int from,
                                   final int size);

    @Override
    public void run() {
        List<Byte> dataBytes = new ArrayList<Byte>();
        int index = this.mapData(dataBytes, 0, this.preamble.length);
        if(!this.compareAgainstPreamble(dataBytes)) {
            Log.d(this.logTag, "Frame unequal to preamble");
            return;
        }
        this.mapData(dataBytes, index, Integer.MAX_VALUE);
        this.frame.setDataBytes(this.mergeBits(dataBytes));
        this.frame.setFrameState(Frame.FrameState.INTERPRETED_SUCCESSFULLY);
        this.receiver.callback(this.frame);
    }
}
