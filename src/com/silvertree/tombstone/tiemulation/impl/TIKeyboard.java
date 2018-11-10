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
        }else if (keyCode.isWhitespaceKey())
            tiKeyCode = translateWhitespaceKey(keyCode);
        System.out.println("translate("+keyCode.getName()+" to "+tiKeyCode.toString());
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
                return TIKeycode.AID ;
            case F8:
                return TIKeycode.REDO;
        }
        return TIKeycode.UNMAPPED ;
    }
}
