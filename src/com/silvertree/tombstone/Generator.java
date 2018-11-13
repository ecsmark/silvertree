package com.silvertree.tombstone;

public class Generator {
    private int generatorLocation ;
    private int releasePoint ;

    public Generator(int generatorLocation, int releasePoint){
        this.generatorLocation = generatorLocation ;
        this.releasePoint = releasePoint ;

    }

    public int getGeneratorLocation() {
        return generatorLocation;
    }

    public void setGeneratorLocation(int generatorLocation) {
        this.generatorLocation = generatorLocation;
    }

    public int getReleasePoint() {
        return releasePoint;
    }

    public void setReleasePoint(int releasePoint) {
        this.releasePoint = releasePoint;
    }
}
