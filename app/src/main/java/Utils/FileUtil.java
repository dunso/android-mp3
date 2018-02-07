package Utils;

import android.os.Environment;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileUtil {

    /**
     * Load songs from SD card
     *
     * @return A map contains song path and song name
     */
    public static Map<String, String> loadSongList() {
        // Whether SD card can be used
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // SD card can be used
            String path = Environment.getExternalStorageDirectory().getPath();
            Map<String, String> results = new HashMap<>();
            getFiles(path, results);
            return results;
        } else {
            // SD card can't be used
            return null;
        }
    }

    /**
     * Recursive find songs in path
     *
     * @param path:    In which path to find songs
     * @param results: A map contains songs path and name
     */
    private static void getFiles(String path, Map<String, String> results) {
        File file = new File(path);
        if(!file.canRead()){
            return;
        }

        File[] files = file.listFiles();

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                getFiles(files[i].getAbsolutePath(), results);
            } else {
                String fileName = files[i].getName();
                if (fileName.endsWith(".mp3") || fileName.endsWith(".wma")) {
                    fileName = fileName.substring(0, fileName.lastIndexOf("."));
                    results.put(files[i].getAbsolutePath(), fileName);
                }
            }
        }
    }
}
