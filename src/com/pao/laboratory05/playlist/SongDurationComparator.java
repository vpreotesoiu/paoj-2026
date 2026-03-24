package com.pao.laboratory05.playlist;

import java.util.Comparator;

class SongDurationComparator implements Comparator<Song> {
    public int compare(Song s1, Song s2) {
        return s1.durationSeconds() - s2.durationSeconds();
    }
}