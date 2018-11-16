package com.silvertree.tombstone;

/**
 * This enum represents all the character patterns created specifically for the Tombstone City gameboard.
 */
public enum Characters {
    Blank(32),
    /**
     * Grave (appears a saguero cactus.
     */
    Grave(93),
    BottomEdge(95),
    Explode(97),
    ShipRight(104),
    ShipUp(105),
    ShipDown(106),
    ShipLeft(107),
    BulletHorizontal(108),
    BulletVertical(109),
    Large1(112),
    Large2(113),
    SafeAreaBlank(134),
    Small1(144),
    Small2(145),
    SafeAreaColumn(152)

    ;

    int chrIndex ;
    Characters(int value) {
        this.chrIndex =  value ;
    }
    public int getChrIndex(){
        return chrIndex ;
    }

}
