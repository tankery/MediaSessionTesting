package me.tankery.mediasessiontesting;

import android.annotation.SuppressLint;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.IdRes;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.media.session.PlaybackStateCompat.State;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import me.tankery.mediasessiontesting.databinding.ActivityMainBinding;

/**
 * ViewModel for main page.
 *
 * Created by tankery on 17/03/2017.
 */

public class MainViewModel extends BaseObservable implements OnCheckedChangeListener {

    private final ActivityMainBinding binding;

    private Contracts contracts;

    @IdRes
    private int selectedStateRadio;
    @State
    private int playbackState;

    private MainViewModel(ActivityMainBinding binding) {
        this.binding = binding;
        this.binding.setViewModel(this);
        this.binding.radiosState.setOnCheckedChangeListener(this);
        setPlaybackState(PlaybackStateCompat.STATE_NONE);
    }

    public static MainViewModel bind(ActivityMainBinding binding) {
        return new MainViewModel(binding);
    }

    public void unbind() {
        binding.radiosState.setOnCheckedChangeListener(null);
        binding.unbind();
    }

    public void setContracts(Contracts contracts) {
        this.contracts = contracts;
    }

    @SuppressLint("SwitchIntDef")
    public void setPlaybackState(@State int state) {
        playbackState = state;

        switch (state) {
            case PlaybackStateCompat.STATE_PAUSED:
                selectedStateRadio = R.id.radio_pause;
                break;
            case PlaybackStateCompat.STATE_PLAYING:
                selectedStateRadio = R.id.radio_play;
                break;
            case PlaybackStateCompat.STATE_STOPPED:
                // pass through
            default:
                selectedStateRadio = R.id.radio_stop;
                break;
        }

        notifyPropertyChanged(me.tankery.mediasessiontesting.BR.selectedStateRadio);
    }

    @Bindable
    public int getSelectedStateRadio() {
        return selectedStateRadio;
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int id) {
        selectedStateRadio = id;
        int state;
        switch (id) {
            case R.id.radio_play:
                state = PlaybackStateCompat.STATE_PLAYING;
                break;
            case R.id.radio_pause:
                state = PlaybackStateCompat.STATE_PAUSED;
                break;
            case R.id.radio_stop:
                state = PlaybackStateCompat.STATE_STOPPED;
                break;
            default:
                state = PlaybackStateCompat.STATE_NONE;
                break;
        }

        if (state == playbackState) {
            return;
        }

        playbackState = state;

        if (contracts != null) {
            contracts.onPlaybackStateChanged(playbackState);
        }
    }

    public interface Contracts {
        void onPlaybackStateChanged(@State int state);
    }

}
