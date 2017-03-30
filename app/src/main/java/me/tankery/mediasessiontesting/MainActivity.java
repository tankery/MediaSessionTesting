package me.tankery.mediasessiontesting;

import android.app.PendingIntent;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.MediaSessionCompat.Callback;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.media.session.PlaybackStateCompat.State;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;

import me.tankery.mediasessiontesting.MainViewModel.Contracts;
import me.tankery.mediasessiontesting.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements Contracts {

    private static final String TAG = "session.test";

    private MainViewModel viewModel;
    private MediaSessionCompat mediaSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = MainViewModel.bind(binding);
        viewModel.setContracts(this);
        setupMediaSession();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaSession();
        viewModel.unbind();
        viewModel = null;
    }

    private void setupMediaSession() {
        mediaSession = new MediaSessionCompat(this, "ink");
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mediaSession.setSessionActivity(pendingIntent);
        mediaSession.setActive(true);

        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "MediaSession testing")
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "Tankery")
                .build());

        mediaSession.setCallback(new Callback() {
            @Override
            public void onPlay() {
                Log.i(TAG, "Got play");
                viewModel.setPlaybackState(PlaybackStateCompat.STATE_PLAYING);
            }

            @Override
            public void onPause() {
                Log.i(TAG, "Got pause");
                viewModel.setPlaybackState(PlaybackStateCompat.STATE_PAUSED);
            }

            @Override
            public boolean onMediaButtonEvent(Intent intent) {
                KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                Log.i(TAG, "Got key event " + keyEvent);
                return super.onMediaButtonEvent(intent);
            }
        });
    }

    private void releaseMediaSession() {
        mediaSession.setCallback(null);
        mediaSession.release();
        mediaSession = null;
    }

    @Override
    public void onPlaybackStateChanged(@State int state) {
        Log.i(TAG, "select " + state);
        mediaSession.setPlaybackState(new PlaybackStateCompat.Builder().setState(state, 0, 0).build());
    }
}
