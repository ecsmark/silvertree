package com.silvertree.tombstone;

import java.util.ArrayList;
import java.util.List;

/**
 * instance of a large monster (MORG) in Tombstone City.
 */
public class LargeMonster extends Monster {
    static List<Monster> LMontab ;
    public static int MAXLARGEMONSTERCOUNT = 9 ;
    static int POINTVALUE = 150 ;

    public LargeMonster(int curLoc){
        setCurLocation(curLoc);
    }

    @Override
    public Characters getCharacter(int type) {
        return type == 1 ? Characters.Large1 : Characters.Large2;
    }

    @Override
    public int getPointValue() {
        return  POINTVALUE;
    }

    @Override
    public Characters replaceCharacter() {
        return Characters.Grave;
    }

    @Override
    public Characters nextFrameCharacter(Characters currentFrameCharacter) {
        if (currentFrameCharacter == Characters.Large1)
            return Characters.Large2 ;
        else if (currentFrameCharacter == Characters.Large2)
            return Characters.Large1;
        return null ;
    }

    static void createMontab(){
        if (LMontab != null){
            LMontab.clear();
        }
        LMontab = new ArrayList<>(MAXLARGEMONSTERCOUNT);
    }

    @Override
    public List<Monster> getMontab() {
        return LMontab;
    }

    static List<Monster> getMonsters(){
        return LMontab ;
    }
    static boolean isEmpty(){
        return LMontab.isEmpty();
    }
    public static boolean isFull(){
        return LMontab.size() >= MAXLARGEMONSTERCOUNT ;
    }

}
