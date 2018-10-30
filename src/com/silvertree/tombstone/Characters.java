package com.silvertree.tombstone;

public enum Characters {
    Grave(93),
    BottomEdge(95),
    Explode(97),
    ShipRight(104),
    Large1(112),
    Large2(113),

    ;

    int chrIndex ;
    Characters(int value) {
        this.chrIndex = value ;
    }
    public int getChrIndex(){
        return chrIndex ;
    }
}
