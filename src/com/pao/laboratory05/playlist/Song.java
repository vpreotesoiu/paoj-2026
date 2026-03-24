package com.pao.laboratory05.playlist;


public record Song(String title, String artist, int durationSeconds)
        implements Comparable<Song> {
    public int compareTo(Song o) {
        return this.title.compareTo(o.title);
    }
}

