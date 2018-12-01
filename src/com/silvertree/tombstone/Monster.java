package com.silvertree.tombstone;

import java.util.Iterator;
import java.util.List;

/**
 * base class for the Tombstone City monster types
 */
public abstract class Monster {
    public int getCurLocation() {
        return curLocation;
    }

    public void setCurLocation(int curLocation) {
        this.curLocation = curLocation;
    }

    int curLocation ;

    /**
     * return the Tombstone City Character for the monster view
     * @param type 1 or 2
     * @return Character for the given type or view
     */
    public abstract Characters getCharacter(int type);

    /**
     * return the Monster Table for the monster class
     * @return table of currently active monsters.
     */
    public abstract List<Monster> getMontab() ;

    /**
     * return the character that should be placed on the gameboard
     * when the monster is killed.
     * @return
     */
    public abstract Characters replaceCharacter();

    public abstract Characters nextFrameCharacter(Characters currentFrameCharacter);


    /**
     *
     * @return point value for killing this monster type
     */
    public abstract int getPointValue() ;

    public void addToMontab(){
        getMontab().add(this);
    }

    /**
     * remove the monster from its corresponding Monster table.
     *
     * @return true monster found in table
     *         false monster not found in table
     */
    public  boolean removeFromMontab(){
//        Iterator<Monster>  iter = getMontab().iterator();
//        while(iter.hasNext()){
//            Monster monster = iter.next();
//            if (monster.getCurLocation() == getCurLocation()){
//                iter.remove();
//                return true ;
//            }
//        }
        if (!getMontab().remove(this)) {
            GameLogging.error("removeFromMontab: did not find Monster " + this.toString() + " in Montab");
            return false;
        }
        return true ;
    }

    @Override
    public boolean equals(Object obj) {
        return ((Monster)obj).getCurLocation() == this.getCurLocation();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+"@"+hashCode()+"[curLocation="+getCurLocation()+"]";
    }
}
