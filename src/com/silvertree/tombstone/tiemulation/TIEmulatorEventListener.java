package com.silvertree.tombstone.tiemulation;

import java.util.EventListener;
@FunctionalInterface
public interface TIEmulatorEventListener<T extends TIEmulatorEvent> extends EventListener {
    void handle(T event);
}
