package zfani.assaf.radiokahollavan.model;

public class Song implements Comparable<Song> {

    private final String title;
    private final long date;

    public Song(String title, long date) {
        this.title = title;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public long getDate() {
        return date;
    }

    @Override
    public int compareTo(Song other) {
        return (int) (other.getDate() - getDate());
    }
}
