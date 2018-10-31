package com.silvertree.tombstone.tiemulation.impl;

import com.silvertree.tombstone.tiemulation.ITIKeyboard;
import com.silvertree.tombstone.tiemulation.TIKeyboardEventListener;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class TIKeyboard implements ITIKeyboard {

    private TIKeyboardEventListener<? super TIKeyboardEvent> onKeyPressedEventHandler;

    @Override
    public TIKeycode scan() {
        while (currentKey == null){
            synchronized(this) {
                try {
                    this.wait(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return  TIKeycode.BEGIN ;
    }

    @Override
    public boolean checkkey(TIKeycode keycode) {
        return currentKey != null;
    }

    KeyCode currentKey = null ;

    @Override
    public void onKeyPressed(TIKeyboardEventListener<? super TIKeyboardEvent> eventHandler) {
        onKeyPressedEventHandler = eventHandler ;
    }

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
                if (onKeyPressedEventHandler != null){
                    System.out.println("calling event handler ");
                    onKeyPressedEventHandler.handle(new TIKeyboardEvent(TIKeycode.AID));
                }
            }
        });
    }
}
