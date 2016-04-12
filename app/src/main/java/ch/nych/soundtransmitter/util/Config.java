package ch.nych.soundtransmitter.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by nych on 4/10/16.
 */
public class Config {

    private static final String CONFIG_FILE = "config.properties";
    private static Properties properties = null;

    private static void loadProperties() {

        Config.properties = new Properties();
        try {
            Log.i("MyTag", System.getProperty("user.dir"));
            File file = new File(Config.CONFIG_FILE);
            file.exists();
            FileInputStream fileInputStream = new FileInputStream(Config.CONFIG_FILE);
            Config.properties.load(fileInputStream);
        } catch (FileNotFoundException e) {
            //// TODO: 4/10/16 
            Log.e("MyTag", e.getMessage());
        } catch (IOException e) {
            //// TODO: 4/10/16
            Log.e("MyTag", e.getMessage());
        }
    }
    public static Properties getProperties() {
        if(Config.properties == null) {
            Config.loadProperties();
        }
        return Config.properties;
    }
}
