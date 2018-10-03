package com.silvertree.tombstone.tiemulation.impl;

import com.silvertree.tombstone.tiemulation.ITIVideo;
import com.silvertree.tombstone.tiemulation.IVirtualTI;
import javafx.scene.Scene;


public class VirtualTI implements IVirtualTI {

    ITIVideo video ;
    Scene scene ;

    public VirtualTI(Scene scene){
        this.scene = scene ;
        createVideo(scene) ;
    }
    @Override
    public ITIVideo getVideo() {
        return null;
    }
    ITIVideo createVideo(Scene scene){
        video = new TIVideo(scene);
        return video ;
    }
}
