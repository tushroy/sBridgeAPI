package ch.nych.soundtransmitter.receiver.tasks;

import android.util.Log;

import java.util.Arrays;

import ch.nych.soundtransmitter.util.Configuration;

/**
 * Created by nych on 4/20/16.
 */
public class Frame {

    /**
     *
     */
    public final static int IN_PROGRESS = 0;

    /**
     *
     */
    public final static int ANALYZED_SUCCESSFULLY = 1;

    /**
     *
     */
    public final static int FRAME_CORRUPTED = -1;

    /**
     *
     */
    private final String logTag = Configuration.LOG_TAG + ":Frame";

    /**
     *
     */
    private int state = 0;

    /**
     * Number of rows in the original data. This index is used to for the frame sealing.
     */
    private int index = 0;

    /**
     * The raw unprocessed data (magnitudes of the different frequencies). These data should never
     * be changed.
     */
    private double[][] originalData = null;

    /**
     * The processed data are reduced on the frequency peaks.
     */
    private double[][] processedData = null;

    /**
     *
     */
    private byte[] dataBytes = null;

    /**
     *
     * @return
     */
    public int getState() {
        return this.state;
    }

    /**
     *
     * @param state
     */
    public void setState(final int state) {
        this.state = state;
    }

    /**
     *
     * @return
     */
    public byte[] getDataBytes() {
        return this.dataBytes;
    }

    /**
     *
     * @param dataBytes
     */
    public void setDataBytes(final byte[] dataBytes) {
        this.dataBytes = dataBytes;
    }

    /**
     * The frame class represent a container class for the recorded frame data. It contains the
     * original magnitude values, the data reduced on its peaks and the mapped bytes.
     * @param configuration
     */
    public Frame(final Configuration configuration) {
        this.originalData =
                new double[configuration.getTransmissionMode()][configuration.getMaxFrameSize()];
    }

    /**
     * Getter for the original data set. This data are intended as read only and shouldn't be
     * changed.
     * @return The raw magnitudes of the different frequencies. Each column represents a frequency,
     * whereas each raw represents the magnitude over time. orginialData[Frequency][Magnitudes]
     */
    public double[][] getOriginalData() {
        return this.originalData;
    }

    /**
     * Getter for the processed data set. This data are intended as read only and shouldn't be
     * changed. This data are reduced to the frequency peaks.
     * @return The peak magnitudes of the different frequencies. Each column represents a frequency,
     * whereas each raw contains the magnitude peaks over time. orginialData[Frequency][Magnitudes]
     */
    public double[][] getProcessedData() {
        return this.processedData;
    }

    /**
     * This method adds a set of magnitudes to the original data set. As the frame has a maximum
     * size (Can be set in the {@link Configuration} class), the set is only added if there is
     * space left.
     * @param dataSet a double array of magnitudes. The length of this array needs to be equal to
     *                the configured number of frequencies (see {@link Configuration}
     *                transmissionMode).
     */
    public void addDataSet(final double[] dataSet) {
        for(int i = 0; i < dataSet.length; i++) {
            this.originalData[i][index] = dataSet[i];
        }
        index++;
    }

    /**
     * The orginalData are reduced to the added magnitudes. From this reduced data a copy is done,
     * so the processing of the data in processedData don't affect the originalData.
     */
    public void sealFrame() {
        this.processedData = new double[originalData.length][];
        if(this.index > 0) {
            for(int i = 0; i < this.originalData.length; i++) {
                this.originalData[i] = Arrays.copyOf(this.originalData[i], index);
                this.processedData[i] = Arrays.copyOf(this.originalData[i], this.originalData[i].length);
            }
        }
    }

    // TODO: 5/1/16 Remove when not longer necessary
    public void printFrame(boolean original) {
        double[][] data = null;
        if(original) {
            data = this.originalData;
        } else {
            data = this.processedData;
        }
        for(int i = 0; i < data[0].length; i++) {
            System.out.print(i);
            for(int j = 0; j < data.length; j++) {
                System.out.print(", " + data[j][i]);
            }
            System.out.println();
        }
    }
}
