package com.ccc.raj.beats;

/**
 * Created by Raj on 1/29/2018.
 */

public interface MusicServicePublisher {
    public void subscribeForService(MusicServiceSubscriber obj);
    public void unsubscribeForService(MusicServiceSubscriber obj);
    public void notifyAllSubscribers();
}
