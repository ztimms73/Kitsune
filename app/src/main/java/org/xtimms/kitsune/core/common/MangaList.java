package org.xtimms.kitsune.core.common;

import androidx.annotation.Nullable;

import org.xtimms.kitsune.core.models.MangaHeader;

public class MangaList extends PagedList<MangaHeader> {

    public static MangaList empty() {
        return new MangaList();
    }

    public MangaList first(int size) {
        final MangaList result = new MangaList();
        for (int i=0;i<size;i++) {
            result.add(get(i));
        }
        return result;
    }

    public int indexOf(int id) {
        for (int i=0;i<size();i++) {
            if (get(i).id == id) {
                return i;
            }
        }
        return -1;
    }

    public boolean inRange(int pos) {
        return pos >= 0 && pos < size();
    }

    @Nullable
    public MangaHeader getById(int id) {
        for (MangaHeader o : this) {
            if (o.id == id) {
                return o;
            }
        }
        return null;
    }
}