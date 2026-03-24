package com.pao.laboratory05.playlist;

import java.util.ArrayList;
import java.util.Arrays;

public class Playlist {
    private String name;
    private Song[] songs;

    public Playlist(String name) {
        this.name = name;
        songs = new Song[0];
    }

    void addSong(Song s) {
        Song[] tmp = new Song[songs.length + 1];
        System.arraycopy(this.songs, 0, tmp, 0, songs.length);
        tmp[songs.length] = s;
        this.songs = tmp;
    }

    int getTotalDuration() {
        int d = 0;
        for (Song s : songs) {
            d += s.durationSeconds();
        }
        return d;
    }

    void printSortedByTitle() {
        Song[] copy = new Song[songs.length];
        System.arraycopy(this.songs, 0, copy, 0, songs.length);
        Arrays.sort(copy);
        for (Song s : copy) {
            System.out.println("Song[title=" + s.title() + ", artist=" + s.artist() + ", durationInSeconds=" + s.durationSeconds());
        }
    }

    void printSortedByDuration() {
        Song[] copy = new Song[songs.length];
        System.arraycopy(this.songs, 0, copy, 0, songs.length);
        Arrays.sort(copy, new SongDurationComparator());
        for (Song s : copy) {
            System.out.println("Song[title=" + s.title() + ", artist=" + s.artist() + ", durationInSeconds=" + s.durationSeconds());
        }
    }

    public String getName() {
        return this.name;
    }

    public Song[] getContent() {
        return this.songs;
    }
}
