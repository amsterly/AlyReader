package aly.com.alyreader.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by aly on 16/7/17.
 */
public  class MusicsListEntity implements Parcelable {
    private String filename;
    private String extname;
    private int m4afilesize;
    private int filesize;
    private int bitrate;
    private int isnew;
    private int duration;
    private String album_name;
    private String singername;
    private String hash;
    private String url;//音乐列表没有这个 后期请求再赋值

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }



    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getExtname() {
        return extname;
    }

    public void setExtname(String extname) {
        this.extname = extname;
    }

    public int getM4afilesize() {
        return m4afilesize;
    }

    public void setM4afilesize(int m4afilesize) {
        this.m4afilesize = m4afilesize;
    }

    public int getFilesize() {
        return filesize;
    }

    public void setFilesize(int filesize) {
        this.filesize = filesize;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public int getIsnew() {
        return isnew;
    }

    public void setIsnew(int isnew) {
        this.isnew = isnew;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getAlbum_name() {
        return album_name;
    }

    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
    }

    public String getSingername() {
        return singername;
    }

    public void setSingername(String singername) {
        this.singername = singername;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.filename);
        dest.writeString(this.extname);
        dest.writeInt(this.m4afilesize);
        dest.writeInt(this.filesize);
        dest.writeInt(this.bitrate);
        dest.writeInt(this.isnew);
        dest.writeInt(this.duration);
        dest.writeString(this.album_name);
        dest.writeString(this.singername);
        dest.writeString(this.hash);
        dest.writeString(this.url);
    }

    public MusicsListEntity() {
    }

    protected MusicsListEntity(Parcel in) {
        this.filename = in.readString();
        this.extname = in.readString();
        this.m4afilesize = in.readInt();
        this.filesize = in.readInt();
        this.bitrate = in.readInt();
        this.isnew = in.readInt();
        this.duration = in.readInt();
        this.album_name = in.readString();
        this.singername = in.readString();
        this.hash = in.readString();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<MusicsListEntity> CREATOR = new Parcelable.Creator<MusicsListEntity>() {
        @Override
        public MusicsListEntity createFromParcel(Parcel source) {
            return new MusicsListEntity(source);
        }

        @Override
        public MusicsListEntity[] newArray(int size) {
            return new MusicsListEntity[size];
        }
    };
}
