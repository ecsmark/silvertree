package com.silvertree.tombstone.tiemulation;

public interface ITIVideo {
    void refresh() ;
    boolean vmbw(TIAddress addr, char bytes[], short count);
    char vsbr(TIAddress addr);

    void wrChar(int nRow, int nCol, int val);
    void displayAt(int row, int col, int[] value);
    void displayAt(int row, int col, String value);

    void setColor(int charSet, byte color);
    void setChar(int charno, byte[] pattern);
}
