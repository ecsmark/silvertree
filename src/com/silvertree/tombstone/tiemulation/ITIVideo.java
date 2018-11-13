package com.silvertree.tombstone.tiemulation;

import com.silvertree.tombstone.Characters;

/**
 *  Interface defining the emulation of the TI Video system.
 */
public interface ITIVideo {
    /**
     * refresh the TI framebuffer to the screen.
     */
    void refresh() ;

    /**
     * write the character to the TI video RAM at the given location.
     * This updates the framebuffer and will appear on the next refresh().
     * @param nRow character row number (0-23)
     * @param nCol character column number (0-31)
     * @param val character value ;
     */
    void wrChar(int nRow, int nCol, int val);

    /**
     * write a sequence of character values to the TI video RAM at the given location.
     * This updates the framebuffer and will appear on the next refresh().
     * @param row character row number (0-23) for the first character of the sequence
     * @param col character column number (0-31) for the first character of the sequence
     * @param value array of character values, each character will be written to the video RAM.
     */
    void displayAt(int row, int col, int[] value);
    /**
     * write a Java String to the TI video RAM at the given location.
     * This updates the framebuffer and will appear on the next refresh().
     * @param row character row number (0-23) for the first character of the sequence
     * @param col character column number (0-31) for the first character of the sequence
     * @param value string to write to video RAM.
     */
    void displayAt(int row, int col, String value);

    /**
     * get the character currently at the given screen location in the TI video RAM.
     * @param screenLocation absolute screen position in characters.
     * @return TI character number
     */
    byte getChar(int screenLocation);

    /**
     *  set the color of a group of characters
     * @param charSet 8 character set which will use the color
     * @param color  TI Color number (0 - 15)
     */
    void setColor(int charSet, byte color);

    /**
     * set the pattern for a given character
     * @param charno
     * @param pattern 8 byte array defining the character.
     */
    void setChar(int charno, byte[] pattern);

    /**
     * create a sprite
     * @param spritenum
     * @param pattern
     * @param color
     * @param y screen location where the sprite will first appear
     * @param x screen location where the sprite will first appear
     * @param yvelocity
     * @param xvelocity
     */
    public void sprite(int spritenum, int pattern, int color, int y, int x,int yvelocity, int xvelocity );
    public void displaySprite(int spriteNum);

    /**
     *  move the sprite to the given location
     * @param spriteNum
     * @param y
     * @param x
     */
    public void locateSprite(int spriteNum, int y, int x);

    public void deleteSprite(int spriteNum);

    final static int NumRows = 24 ;
    final static int NumColums = 32 ;
}
