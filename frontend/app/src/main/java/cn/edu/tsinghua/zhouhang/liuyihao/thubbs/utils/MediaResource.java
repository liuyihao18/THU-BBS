package cn.edu.tsinghua.zhouhang.liuyihao.thubbs.utils;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;

import cn.edu.tsinghua.zhouhang.liuyihao.thubbs.R;

public class MediaResource {
    public MediaPlayer mediaPlayer;
    public boolean loaded;
    private MediaResourceListener mediaResourceListener;

    public interface MediaResourceListener {
        void onInit();

        void onReset();
    }

    public void registerMediaResourceListener(MediaResourceListener mediaResourceListener) {
        this.mediaResourceListener = mediaResourceListener;
    }

    public void initPlayer(Context context, String audioUri) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioUri);
            mediaPlayer.setOnCompletionListener(view -> {
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
                if (mediaResourceListener != null) {
                    mediaResourceListener.onReset();
                }
            });
            mediaPlayer.setOnPreparedListener(mediaPlayer -> {
                mediaPlayer.start();
                if (mediaResourceListener != null) {
                    mediaResourceListener.onInit();
                }

            });
            mediaPlayer.prepareAsync();
        } catch (IOException ioe) {
            Alert.error(context, R.string.unknown_error);
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void resetPlayer(Context context) {
        try {
            mediaPlayer.stop();
        } catch (Exception e) {
            Alert.error(context, R.string.unknown_error);
        }
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
        if (mediaResourceListener != null) {
            mediaResourceListener.onReset();
        }
    }
}
