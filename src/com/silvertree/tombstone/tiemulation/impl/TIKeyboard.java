package com.silvertree.tombstone.tiemulation.impl;

import com.silvertree.tombstone.tiemulation.ITIKeyboard;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class TIKeyboard implements ITIKeyboard {
    @Override
    public TIKeycode scan() {
        return null;
    }

    @Override
    public boolean checkkey(TIKeycode keycode) {
        return currentKey != null;
    }

    KeyCode currentKey = null ;
    public TIKeyboard(Scene scene){
        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                currentKey = null ;

            }
        });

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                System.out.println("keyPressed event:"+event.getText());
                currentKey = event.getCode() ;
            }
        });
    }
}
