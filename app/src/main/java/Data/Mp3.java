package Data;

import android.app.Activity;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

public class Mp3 {

    private final String[] STAR = {"*"};
    private List<Song> songList;

    public boolean initSongs(Activity activity) {
        songList = new ArrayList<>();
        Cursor cursor = activity.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                STAR,
                MediaStore.Audio.Media.IS_MUSIC + " != 0",
                null,
                null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String songName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                songName = songName.substring(0, songName.lastIndexOf('.'));
                Song song = new Song(
                        cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),
                        songName,
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)),
                        cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                        cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)),
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                );
                songList.add(song);
            }
        }
        return !songList.isEmpty();
    }

    public List<Song> getSongList() {
        return songList;
    }
}
