package com.silvertree.tombstone;

import java.awt.image.TileObserver;
import java.util.ArrayList;
import java.util.List;

public class SmallMonster extends Monster{
    public final static int MAXSMALLMONSTERCOUNT = 20 ;
    final static int POINTVALUE = 100 ;

    static List<Monster> SMontab ;
    public SmallMonster(int curLocation){
        setCurLocation(curLocation);
    }
    @Override
    public Characters getCharacter(int type) {
        return type == 1? Characters.Small1 : Characters.Small2;
    }

    @Override
    public List<Monster> getMontab() {
        if (SMontab == null){
            SMontab = new ArrayList<>(MAXSMALLMONSTERCOUNT);
        }
        return SMontab;
    }

    @Override
    public Characters replaceCharacter() {
        return Characters.Blank;
    }

    @Override
    public int getPointValue() {
        return POINTVALUE;
    }

    static void createMontab(){
        if (SMontab != null){
            SMontab.clear();
        }
        SMontab = new ArrayList<>(MAXSMALLMONSTERCOUNT);
    }
    static List<Monster> getMonsters(){
        return SMontab ;
    }

    static boolean isEmpty(){
        return SMontab.isEmpty();
    }
    public static boolean isFull(){
        return SMontab.size() >= MAXSMALLMONSTERCOUNT ;
    }
}