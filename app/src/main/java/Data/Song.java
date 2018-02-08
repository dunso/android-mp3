package Data;

public class Song {
    private int songId;
    private String songName;
    private String songPath;
    private int albumId;
    private String albumName;
    private int artistId;
    private String artistName;

    public Song() {
    }

    public Song(int songId, String songName, String songPath, int albumId, String albumName, int artistId, String artistName) {
        this.songId = songId;
        this.songName = songName;
        this.songPath = songPath;
        this.albumId = albumId;
        this.albumName = albumName;
        this.artistId = artistId;
        this.artistName = artistName;
    }

    public String getSongName() {
        return songName;
    }

    public String getSongPath() {
        return songPath;
    }
}
