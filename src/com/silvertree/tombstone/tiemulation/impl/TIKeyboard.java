package com.silvertree.tombstone.tiemulation.impl;

import com.silvertree.tombstone.tiemulation.ITIKeyboard;
import com.silvertree.tombstone.tiemulation.TIKeyboardEventListener;
import java.util.logging.Logger ;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class TIKeyboard implements ITIKeyboard {
    final static Logger LOGGER = Logger.getLogger(TIKeyboard.class.getName());

    private TIKeyboardEventListener<? super TIKeyboardEvent> onKeyPressedEventHandler;

    @Override
    public TIKeycode scan() {
        throw new java.lang.UnsupportedOperationException();
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
                LOGGER.info("keyPressed event:"+event.getText());
                currentKey = event.getCode() ;
                if (onKeyPressedEventHandler != null){
                    LOGGER.info("calling event handler ");
                    onKeyPressedEventHandler.handle(new TIKeyboardEvent(translate(event.getCode())));
                }
            }
        });
    }
    private TIKeycode translate(KeyCode keyCode){
        TIKeycode tiKeyCode = TIKeycode.UNMAPPED ;
        if (keyCode.isFunctionKey()){
            tiKeyCode = translateFunctionKey(keyCode);
        } else if (keyCode.isArrowKey()){
            tiKeyCode = translateArrowKey(keyCode);
        } else if (keyCode.isDigitKey()) {
            tiKeyCode = translateDigitKey(keyCode);
        }else if (keyCode.isLetterKey()){
            tiKeyCode = translateLetterKey(keyCode);
        }else if (keyCode.isWhitespaceKey()) {
            tiKeyCode = translateWhitespaceKey(keyCode);
        } else if (keyCode == KeyCode.ESCAPE){
            tiKeyCode = TIKeycode.REDO ;
        }

        LOGGER.info("translate("+keyCode.getName()+" to "+tiKeyCode.toString());
        return tiKeyCode;
    }

    private TIKeycode translateWhitespaceKey(KeyCode keyCode)
    {
        switch(keyCode){
            case SPACE:
                return TIKeycode.SPACE;
        }
        return TIKeycode.UNMAPPED ;
    }
    private TIKeycode translateLetterKey(KeyCode keyCode) {
        switch(keyCode){
            case Q:
                return TIKeycode.Q;
            case SPACE:
                return TIKeycode.SPACE;
        }
        return TIKeycode.UNMAPPED;
    }

    private TIKeycode translateDigitKey(KeyCode keyCode) {
        switch(keyCode){
            case DIGIT0:
                return TIKeycode.DIGIT0 ;
            case DIGIT1:
                return TIKeycode.DIGIT1;
            case DIGIT2:
                return TIKeycode.DIGIT2;
            case DIGIT3:
                return TIKeycode.DIGIT3 ;
            case DIGIT4:
                return TIKeycode.DIGIT4;
        }
        return TIKeycode.UNMAPPED;
    }

    private TIKeycode translateArrowKey(KeyCode keyCode) {
        if (keyCode == KeyCode.RIGHT)
            return TIKeycode.RIGHT;
        else if (keyCode == KeyCode.LEFT)
            return TIKeycode.LEFT ;
        else if (keyCode == KeyCode.UP)
            return TIKeycode.UP;
        else if (keyCode == KeyCode.DOWN)
            return TIKeycode.DOWN;
        return(TIKeycode.UNMAPPED);
    }

    private TIKeycode translateFunctionKey(KeyCode keyCode) {
        switch(keyCode)
        {
            case F1:
                return TIKeycode.DEL ;
            case F2:
                return TIKeycode.INS ;
            case F3:
                return TIKeycode.ERASE;
            case F4:
                return TIKeycode.CLEAR;
            case F5:
                return TIKeycode.BEGIN;
            case F6:
                return TIKeycode.PROCD;
            case F7:
                return TIKeycode.AID;
            case F8:
                return TIKeycode.REDO;
            case F9:
                return TIKeycode.BACK;
            case F11:
                return TIKeycode.QUIT;
        }
        return TIKeycode.UNMAPPED ;
    }
}
