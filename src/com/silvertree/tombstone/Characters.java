package com.silvertree.tombstone;

public enum Characters {
    Grave(93),
    BottomEdge(95),
    Explode(97),
    ShipRight(104),
    ShipUp(105),
    ShipDown(106),
    ShipLeft(107),
    Large1(112),
    Large2(113),
    SafeAreaBL(134)

    ;

    int chrIndex ;
    Characters(int value) {
        this.chrIndex =  value ;
    }
    public int getChrIndex(){
        return chrIndex ;
    }
}
