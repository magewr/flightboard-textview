package com.magewr.sample.lib.textswitcher;

import android.os.Handler;

public class Switcher
{
    private AdvTextSwitcher advTsView;
    private boolean isPaused;
    private int mDuration=1000;

    public Switcher()
    {}

    public Switcher(AdvTextSwitcher view, int duration)
    {
        this.advTsView = view;
        this.mDuration = duration;
    }

    public Switcher setDuration(int duration)
    {
        this.mDuration = duration;
        return this;
    }

    public Switcher attach(AdvTextSwitcher view)
    {
        this.pause();
        this.advTsView = view;
        return this;
    }

    public void start()
    {
        isPaused = false;
        if (this.advTsView != null)
        {
            this.advTsView.onSpinStarted();
            hlUpdt.postDelayed(rbUpdt, mDuration);
        }
    }

    public void pause()
    {
        isPaused = true;
        if (this.advTsView != null)
            this.advTsView.onSpinStopped();
    }

    public Handler hlUpdt = new Handler();

    public Runnable rbUpdt = new Runnable(){
        @Override
        public void run()
        {
            if (!isPaused && advTsView != null)
            {
                advTsView.next();
                hlUpdt.postDelayed(this, mDuration);
            }
        }
    };
}