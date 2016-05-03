package ch.nych.soundtransmitter.receiver.tasks.transformation;

import ch.nych.soundtransmitter.util.Configuration;

/**
 * Created by nych on 4/9/16.
 */
public class WindowFunction {

    /**
     *
     */
    private WindowFunction() {}

    /**
     *
     * @param configuration
     * @return
     */
    public static double[] getWindowFunction(final Configuration configuration) {
        if(configuration.getWindowFunction() == Configuration.HAMMING_WINDOW) {
            return WindowFunction.getHammingWindow(configuration.getWindowSize());
        } else if(configuration.getWindowFunction() == Configuration.HANN_WINDOW) {
            return WindowFunction.getHanningWindow(configuration.getWindowSize());
        } else {
            // TODO: 4/14/16
            return null;
        }
    }

    /**
     *
     * @param size
     * @return
     */
    private static double[] getHammingWindow(final int size) {
        double[] window = new double[size];
        double alpha = 0.54;
        double beta = 0.46;
        for(int i = 0; i < size; i++) {
            window[i] = alpha;
            window[i] -= beta * Math.cos((2 * Math.PI * i)/(size - 1));
        }
        return window;
    }

    /**
     *
     * @param size
     * @return
     */
    private static double[] getHanningWindow(final int size) {
        double[] window = new double[size];
        for(int i = 0; i < size; i++) {
            window[i] = 1;
            window[i] -= Math.cos((2 * Math.PI * i)/(size - 1));
            window[i] *= 0.5;
        }
        return window;
    }
}
