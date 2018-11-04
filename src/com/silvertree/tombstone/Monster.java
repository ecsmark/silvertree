package com.silvertree.tombstone;

import java.util.Iterator;
import java.util.List;

public abstract class Monster {
    public int getCurLocation() {
        return curLocation;
    }

    public void setCurLocation(int curLocation) {
        this.curLocation = curLocation;
    }

    int curLocation ;
    public abstract Characters getCharacter(int type);
    public abstract List<Monster> getMontab() ;
    public abstract Characters replaceCharacter();
    public abstract int getPointValue() ;

    public void addToMontab(){
        getMontab().add(this);
    }
    public  boolean removeFromMontab(){
        Iterator<Monster>  iter = getMontab().iterator();
        while(iter.hasNext()){
            Monster monster = iter.next();
            if (monster.getCurLocation() == getCurLocation()){
                iter.remove();
                return true ;
            }
        }
        return false ;
    }
}
