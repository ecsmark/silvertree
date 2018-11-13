package com.silvertree.tombstone.tiemulation.impl;

import com.silvertree.tombstone.tiemulation.ITIVideo;
import com.silvertree.tombstone.tiemulation.TIAddress;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TIVideo implements ITIVideo {
    final static int COLUMNS = 32;
    final static int ROWS =	 24;
    final static int XSCREENOFFSET = 0;
    final static int YSCREENOFFSET	= 0 ;
    final static int PIXELCOLUMNS = COLUMNS * 8 ;
    final static int PIXELROWS = ROWS * 8 ;
    final static int MAX_SPRITES = 16 ;
    final static int NUM_COLORS = 16 ;

    VDPRam vdpRam = new VDPRam() ;
    WritableImage frameBuffer ;

    private double scaleFactor = 1.0 ;

   Sprite[] sprites = new Sprite[MAX_SPRITES] ;

    final int[] TI_PALETTE = {	0x00ffffff,		// 0 = transparent
                                0xff000000,	    // 1= Black
                                0xff00c000,  	// 2 = Medium green
                                0xff00ff00,		// 3 = Light green
                                0xff000080,		// 4 = Dark blue
                                0xff0000ff,		// 5 = Light blue
                                0xff000080,		// 6 = Dark Red
                                0xff00ffff,		// 7 = Cyan
                                0xffc00000,		// 8 = Medium red
                                0xffff0000,		// 9 = Light red
                                0xff808000,		// A = Dark yellow
                                0xffffff00,	    // B = light yellow
                                0xff008000,		// C = Dark green
                                0xffFF00ff,		// D = Magenta
                                0xff808080,		// E = Grey
                                0xffffffff		// F = White
             };

    Pane pane ;
    GraphicsContext gc ;
    Timeline refreshLoop ;

    public TIVideo(Pane pane , double scaleFactor){
        this.pane = pane ;
        this.scaleFactor = scaleFactor ;

        Canvas canvas = new Canvas(PIXELCOLUMNS*scaleFactor, PIXELROWS*scaleFactor);
        pane.getChildren().add(canvas);
        gc=  canvas.getGraphicsContext2D();
        initPATTAB();
        initColorTable();
        initSpriteTable();

        frameBuffer = createScreenBuffer() ;
        final Duration oneFrameAmt = Duration.millis(1000 / (float) getFramesPerSecond());
        refreshLoop = new Timeline(new KeyFrame(oneFrameAmt,  new EventHandler<ActionEvent>() {

            @Override
            public void handle(javafx.event.ActionEvent event) {
                refresh();
            }
        }));
        refreshLoop.setCycleCount(Animation.INDEFINITE);
        refreshLoop.play() ;


    }

    private int getFramesPerSecond() {
        return 60 ;
    }

    @Override
    public void setColor(int charSet, byte color) {
        vdpRam.ColorTab[charSet] = color ;
    }
    private WritableImage createScreenBuffer(){
        byte[] screenBytes = new byte[(PIXELCOLUMNS*PIXELROWS)/2];
        for (int i=0; i < screenBytes.length; i++){
            screenBytes[i] = (byte)0x77 ;
        }
        Image screenImage = imageFromByteArray(screenBytes, PIXELCOLUMNS, PIXELROWS);
        return new WritableImage(screenImage.getPixelReader(), PIXELCOLUMNS, PIXELROWS);

    }
    private Image imageFromByteArray(byte[] rawPixels, int width, int height){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write((RenderedImage) createBufferedImage(rawPixels, width, height), "png", out);
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        return new javafx.scene.image.Image(in);
    }
    private IndexColorModel getDefaultColorModel() {
        final int BITS_PER_COLOR  = 4;
        byte[] r = new byte[NUM_COLORS];
        byte[] g = new byte[NUM_COLORS] ;
        byte[] b = new byte[NUM_COLORS] ;
        for (int i=0 ; i < r.length; i++){
            r[i] = (byte) ((TI_PALETTE[i] >> 16) & 0xff) ;
            g[i] = (byte) ((TI_PALETTE[i] >> 8) & 0xff) ;
            b[i] = (byte) (TI_PALETTE[i]  & 0xff);
        }
        return new IndexColorModel(BITS_PER_COLOR, NUM_COLORS, r, g, b);
    }

    private SampleModel getIndexSampleModel(int width, int height) {
        IndexColorModel icm = getDefaultColorModel();
        WritableRaster wr = icm.createCompatibleWritableRaster(1, 1);
        SampleModel sampleModel = wr.getSampleModel();
        sampleModel = sampleModel.createCompatibleSampleModel(width, height);
        return sampleModel;
    }

    private BufferedImage createBufferedImage(byte[] pixels, int width, int height) {
        SampleModel sm = getIndexSampleModel(width, height);
        DataBuffer db = new DataBufferByte(pixels, width*height, 0);
        WritableRaster raster = Raster.createWritableRaster(sm, db, null);
        IndexColorModel cm = getDefaultColorModel();
        BufferedImage image = new BufferedImage(cm, raster, false, null);
        return image;
    }

    public void initSpriteTable(){
        vdpRam.initSpriteAttributeBlocks();
    }

    @Override
    public void refresh(){
        Rectangle rect = new Rectangle(0,0, PIXELCOLUMNS, PIXELROWS) ;

        redrawScreen(rect);
        drawSprites() ;
    }

    private void drawSprites() {
        for (Sprite sprite : sprites){
            if (sprite != null) {
                sprite.move();
                sprite.display(gc, scaleFactor);
            }
        }
    }

    public void redrawScreen(Rectangle rect){
        updateScreen(rect);
//        moveAllSprites() ;
    }

    private void updateScreen(Rectangle rect) {
        gc.drawImage(frameBuffer, 0,0, PIXELCOLUMNS*scaleFactor, PIXELROWS*scaleFactor);

    }

    @Override
    public void sprite(int spritenum, int patternNum, int color, int y, int x, int yvelocity, int xvelocity) {
        int foreGroundColor = TI_PALETTE[color];
        int backGroundColor = 0 ;       // transparent
        WritableImage wrImage = new WritableImage(Sprite.SPRITEWIDTH, Sprite.SPRITEHEIGHT);
        Sprite sprite = new Sprite(writePatternToImage(wrImage, 0, 0, patternNum,  foreGroundColor, backGroundColor), x, y, xvelocity, yvelocity);
        sprites[spritenum] =  sprite;
    }

    @Override
    public void locateSprite(int spriteNum, int y, int x) {
        Sprite sprite = sprites[spriteNum];
        if (sprite != null){
            sprite.locate(x, y);
        }
    }

    private void initColorTable(){
        for (int i=0; i < vdpRam.ColorTab.length; i++ )  {
            setColor(i, (byte)0x17) ;
        }
    }
    private void initPATTAB() {
        //	***** STANDARD-CHARACTERS

        final char patDefault[] =
                {
                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,   //
                        0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x00, 0x20,   // !
                        0x48, 0x48, 0x48, 0x00, 0x00, 0x00, 0x00, 0x00,   // "
                        0x00, 0x48, 0xFC, 0x48, 0x48, 0xFC, 0x48, 0x00,   // #
                        0x10, 0x3C, 0x50, 0x38, 0x14, 0x78, 0x10, 0x00,   // $
                        0xC0, 0xC4, 0x08, 0x10, 0x20, 0x40, 0x8C, 0x0C,   // %
                        0x60, 0x90, 0x90, 0x60, 0x60, 0x94, 0x88, 0x74,   // &
                        0x08, 0x10, 0x20, 0x00, 0x00, 0x00, 0x00, 0x00,   // '
                        0x08, 0x10, 0x20, 0x20, 0x20, 0x20, 0x10, 0x08,   // (
                        0x40, 0x20, 0x10, 0x10, 0x10, 0x10, 0x20, 0x40,   // )
                        0x00, 0x00, 0x48, 0x30, 0xCC, 0x30, 0x48, 0x00,   // *
                        0x00, 0x00, 0x10, 0x10, 0x7C, 0x10, 0x10, 0x00,   // +
                        0x00, 0x00, 0x00, 0x00, 0x00, 0x30, 0x10, 0x20,   // ,
                        0x00, 0x00, 0x00, 0x00, 0x7C, 0x00, 0x00, 0x00,   // -
                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x30, 0x30,   // .
                        0x00, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x00,   // /
                        0x38, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0x38,   // 0
                        0x10, 0x30, 0x50, 0x10, 0x10, 0x10, 0x10, 0x7C,   // 1
                        0x78, 0x84, 0x04, 0x08, 0x10, 0x20, 0x40, 0xFC,   // 2
                        0x78, 0x84, 0x04, 0x38, 0x04, 0x04, 0x84, 0x78,   // 3
                        0x0C, 0x14, 0x24, 0x44, 0x84, 0xFC, 0x04, 0x04,   // 4
                        0xF8, 0x80, 0x80, 0xF8, 0x04, 0x04, 0x84, 0x78,   // 5
                        0x78, 0x80, 0x80, 0xF8, 0x84, 0x84, 0x84, 0x78,   // 6
                        0xFC, 0x04, 0x04, 0x08, 0x10, 0x20, 0x40, 0x40,   // 7
                        0x78, 0x84, 0x84, 0x78, 0x84, 0x84, 0x84, 0x78,   // 8
                        0x78, 0x84, 0x84, 0x84, 0x7C, 0x04, 0x04, 0x78,   // 9
                        0x00, 0x30, 0x30, 0x00, 0x00, 0x30, 0x30, 0x00,   // :
                        0x00, 0x30, 0x30, 0x00, 0x00, 0x30, 0x10, 0x20,   // ;
                        0x00, 0x08, 0x10, 0x20, 0x40, 0x20, 0x10, 0x08,   // <
                        0x00, 0x00, 0x00, 0x7C, 0x00, 0x7C, 0x00, 0x00,   // =
                        0x00, 0x40, 0x20, 0x10, 0x08, 0x10, 0x20, 0x40,   // >
                        0x38, 0x44, 0x04, 0x08, 0x10, 0x10, 0x00, 0x10,   // ?
                        0x00, 0x78, 0x84, 0x9C, 0xA4, 0x98, 0x80, 0x7C,   // @
                        0x78, 0x84, 0x84, 0x84, 0xFC, 0x84, 0x84, 0x84,   // A
                        0xF8, 0x44, 0x44, 0x78, 0x44, 0x44, 0x44, 0xF8,   // B
                        0x78, 0x84, 0x80, 0x80, 0x80, 0x80, 0x84, 0x78,   // C
                        0xF8, 0x44, 0x44, 0x44, 0x44, 0x44, 0x44, 0xF8,   // D
                        0xFC, 0x80, 0x80, 0xF0, 0x80, 0x80, 0x80, 0xFC,   // E
                        0xFC, 0x80, 0x80, 0xF0, 0x80, 0x80, 0x80, 0x80,   // F
                        0x78, 0x84, 0x80, 0x80, 0x9C, 0x84, 0x84, 0x78,   // G
                        0x84, 0x84, 0x84, 0xFC, 0x84, 0x84, 0x84, 0x84,   // H
                        0x7C, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x7C,   // I
                        0x04, 0x04, 0x04, 0x04, 0x04, 0x84, 0x84, 0x78,   // J
                        0x88, 0x90, 0xA0, 0xC0, 0xA0, 0x90, 0x88, 0x84,   // K
                        0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x7C,   // L
                        0x84, 0xCC, 0xB4, 0x84, 0x84, 0x84, 0x84, 0x84,   // M
                        0x84, 0xC4, 0xA4, 0x94, 0x8C, 0x84, 0x84, 0x84,   // N
                        0xFC, 0x84, 0x84, 0x84, 0x84, 0x84, 0x84, 0xFC,   // O
                        0xF8, 0x84, 0x84, 0x84, 0xF8, 0x80, 0x80, 0x80,   // P
                        0x78, 0x84, 0x84, 0x84, 0x84, 0x94, 0x88, 0x74,   // Q
                        0xF8, 0x84, 0x84, 0x84, 0xF8, 0x90, 0x88, 0x84,   // R
                        0x78, 0x84, 0x80, 0x78, 0x04, 0x04, 0x84, 0x78,   // S
                        0x7C, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10,   // T
                        0x84, 0x84, 0x84, 0x84, 0x84, 0x84, 0x84, 0x78,   // U
                        0x44, 0x44, 0x44, 0x44, 0x28, 0x28, 0x10, 0x10,   // V
                        0x84, 0x84, 0x84, 0x84, 0x84, 0xB4, 0xCC, 0x84,   // W
                        0x84, 0x84, 0x48, 0x30, 0x30, 0x48, 0x84, 0x84,   // X
                        0x44, 0x44, 0x44, 0x28, 0x10, 0x10, 0x10, 0x10,   // Y
                        0xFC, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0xFC,   // Z
                        0x38, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x38,   // [
                        0x00, 0x80, 0x40, 0x20, 0x10, 0x08, 0x04, 0x00,   // \'
                        0x70, 0x10, 0x10, 0x10, 0x10, 0x10, 0x10, 0x70,   // ]
                        0x10, 0x28, 0x44, 0x82, 0x00, 0x00, 0x00, 0x00,   // ^
                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xFC,   // _

                };

        for (int i=0; i < 64; i++)
        {
            byte[] pattern = new byte[8] ;
            for (int j =0; j < 8; j++)   {
                pattern[j] = (byte) patDefault[i*8 + j];
            }
            setChar(i+32, pattern) ;
        }
    }

    @Override
    public byte getChar(int screenLocation) {
        return vdpRam.ScreenImage[screenLocation];
    }

    @Override
    public void setChar(int charno, byte[] pat)
    {
        for (int i=0; i < pat.length ; ++i)
        {
            vdpRam.PatternTab[charno*8+i] =  pat[i]  ;
        }
    }

    @Override
    public void wrChar(int nRow, int nCol, int  val) {

        if (nRow < ROWS && nCol < COLUMNS) {
            vdpRam.ScreenImage[nRow * COLUMNS + nCol] = (byte) val;
            byte bColor = vdpRam.ColorTab[val/8];
            int foreGroundColor = (bColor >> 4) & 0x0f;
            int backGroundColor = bColor & 0x0f ;

            writePatternToImage(frameBuffer, nRow, nCol, val, TI_PALETTE[foreGroundColor], TI_PALETTE[backGroundColor]);
        }
    }

    private WritableImage writePatternToImage(WritableImage wrImage, int nRow, int nCol, int patternNum, int foreGroundRGB, int backgroundRGB) {
        byte[] pattern = new byte[8] ;
        for (int i =0 ; i < pattern.length; i++){
            pattern[i] = vdpRam.PatternTab[patternNum*8+i];

        }
        PixelWriter writer = wrImage.getPixelWriter() ;
        for (int i=0; i < 8; i++){
            int y = nRow * 8 + i;
            byte patMask = pattern[i];
            for (int j =0; j < 8; j++) {
                int x = nCol * 8 +j;
                int rgb =backgroundRGB ;
                if ((patMask & (byte)0x80)==(byte) 0x80 ){
                    rgb = foreGroundRGB ;
                }
                patMask = (byte) (patMask << 1) ;
                writer.setArgb(x, y, rgb);
            }
        }
        return wrImage;
    }

    @Override
    public void displayAt(int row, int col, int[] value) {
        for (int i=0; i < value.length; i++){
            wrChar(row, col + i, value[i]);
        }
    }

    @Override
    public void displayAt(int row, int col, String value) {
        int[] chrs = new int[value.length()];
        for (int i=0; i < chrs.length; i++)
            chrs[i] = value.charAt(i);
        displayAt(row, col, chrs);
    }

    @Override
    public void displaySprite(int spriteNum) {
        if (sprites[spriteNum] != null){
            sprites[spriteNum].display(gc, scaleFactor);
        }

    }
}
