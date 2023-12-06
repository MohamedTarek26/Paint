package com.paint.paint.events;

public interface Event {
    public void apply();

    public void revert();
}