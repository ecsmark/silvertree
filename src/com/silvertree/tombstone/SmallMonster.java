package com.silvertree.tombstone;

import java.awt.image.TileObserver;
import java.util.ArrayList;
import java.util.List;

/**
 * instance of a Small Monster (tumbleweed) in Tombstone City.
 */
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
    public Characters nextFrameCharacter(Characters currentFrameCharacter) {
        if (currentFrameCharacter == Characters.Small1)
            return Characters.Small2;
        else if (currentFrameCharacter == Characters.Small2)
            return Characters.Small1 ;
        return null;
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

    /**
     * static method to return the Small monster table.
     * @return small monster table.
     */
    static List<Monster> getMonsters(){
        return SMontab ;
    }

    /**
     * Is the small monster table empty?
     * @return true the table is empty.
     */
    static boolean isEmpty(){
        return SMontab.isEmpty();
    }

    /**
     * Do we have the maximum allowed Small monsters
     * @return true if the monster table contains the maximum small monsters.
     */
    public static boolean isFull(){
        return SMontab.size() >= MAXSMALLMONSTERCOUNT ;
    }
}
