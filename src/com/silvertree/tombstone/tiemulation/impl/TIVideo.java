package com.silvertree.tombstone.tiemulation.impl;

import com.silvertree.tombstone.tiemulation.ITIVideo;
import com.silvertree.tombstone.tiemulation.TIAddress;
import javafx.scene.Scene;

import java.io.InputStream;

public class TIVideo implements ITIVideo {
    final static int SPRITEWIDTH = 8 ;
    final static int SPRITEHEIGHT = 8 ;

    VDPRam vdpRam = new VDPRam() ;

    public TIVideo(Scene scene ){



    }
    @Override
    public boolean vmbw(TIAddress addr, char[] bytes, short count) {
        return false;
    }

    @Override
    public char vsbr(TIAddress addr) {
        return 0;
    }

    public void initSpriteTable(String bitmap){

    }
}
