package com.silvertree.tombstone.tiemulation.impl;

public class VDPRam {
    public final static int COLUMNS = 32 ;
    public final static int ROWS = 24;
    public final static int MAXSPRITE	= 31 ;
    public final static int SPRITEUNUSED = 0xD0;

    byte[] ScreenImage = new byte [COLUMNS*ROWS] ;
    SPAB SpriteAttr= new SPAB();
    byte[] ColorTab = new byte[32] ;
    byte[] Unused1 = new byte[96] ;
    byte[]SpriteDesc = new byte[32*8] ;
    byte[] Unused2 = new byte[640] ;
    byte[] SpriteMotion= new byte[32*4] ;
    byte[] PatternTab= new byte [256*8] ;

}
