package Fragments;

public class MusicFiles {
    private String path;
    private String title;
    private String artist;
    private String album;
    private String duration;
    private String id;
    private String favStatus;



    public MusicFiles(String path, String title, String artist, String album, String duration, String id, String favStatus) {
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.id = id;
        this.favStatus = favStatus;

    }
    public MusicFiles(){

    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
    public String getId() {
        return id;
    }

    public void setId(String id)
    {
        this.id=id;
    }


    public String getFavStatus() {
        return favStatus;
    }

    public void setFavStatus(String favStatus) {
        this.favStatus = favStatus;
    }
}
