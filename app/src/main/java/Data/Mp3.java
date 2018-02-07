package Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Utils.FileUtil;

public class Mp3 {

    // Song name
    private List<String> songList;
    // Song path
    private List<String> songPath;

    public boolean initSongs() {
        Map<String, String> songMap = FileUtil.loadSongList();
        if (songMap == null || songMap.isEmpty()) {
            return false;
        }
        songList = new ArrayList<>(songMap.values());
        songPath = new ArrayList<>(songMap.keySet());
        return true;
    }

    public List<String> getSongList() {
        return songList;
    }

    public List<String> getSongPath() {
        return songPath;
    }
}
