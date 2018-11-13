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

    public VirtualTI(Scene scene, Pane pane, double scaleFactor){
        this.pane = pane ;
        createVideo(pane, scaleFactor) ;
        createKeyboard(scene);
    }
    @Override
    public ITIVideo getVideo() {
        return video;
    }

    @Override
    public ITIKeyboard getKeyboard(){
        return keyboard ;
    }

    ITIVideo createVideo(Pane pane, double scaleFactor){
        video = new TIVideo(pane, scaleFactor);
        return video ;
    }
    ITIKeyboard createKeyboard(Scene scene){
        keyboard = new TIKeyboard(scene);
        return keyboard ;
    }
}
