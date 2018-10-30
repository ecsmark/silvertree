package com.silvertree.tombstone.tiemulation.impl;

import com.silvertree.tombstone.tiemulation.ITIKeyboard;
import com.silvertree.tombstone.tiemulation.ITIVideo;
import com.silvertree.tombstone.tiemulation.IVirtualTI;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;


public class VirtualTI implements IVirtualTI {

    ITIVideo video ;
    ITIKeyboard keyboard ;
    Pane pane ;

    public VirtualTI(Scene scene, Pane pane){
        this.pane = pane ;
        createVideo(pane) ;
        createKeyboard(scene);
    }
    @Override
    public ITIVideo getVideo() {
        return video;
    }
    public ITIKeyboard getKeyboard(){
        return keyboard ;
    }
    ITIVideo createVideo(Pane pane){
        video = new TIVideo(pane);
        return video ;
    }
    ITIKeyboard createKeyboard(Scene scene){
        keyboard = new TIKeyboard(scene);
        return keyboard ;
    }
}
