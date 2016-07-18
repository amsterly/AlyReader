package aly.com.alyreader.eventbus;

import aly.com.alyreader.bean.MusicsListEntity;

/**
 * Created by Administrator on 2016/7/18.
 */
public class URLEvent {
    public URLEvent(MusicsListEntity musicsListEntity) {
        this.musicsListEntity = musicsListEntity;
    }

    public MusicsListEntity getMusicsListEntity() {
        return musicsListEntity;
    }

    public void setMusicsListEntity(MusicsListEntity musicsListEntity) {
        this.musicsListEntity = musicsListEntity;
    }

    private MusicsListEntity musicsListEntity;
}
