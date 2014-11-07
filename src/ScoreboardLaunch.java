/**
 * Created by Andrew Shapiro on 12.08.14.
 * Score Board Launcher
 */

import processing.core.PApplet;
import ddf.minim.*;

public class ScoreboardLaunch extends PApplet {
    Board sb;
    Stopwatch timer;
    AudioPlayer player; // audio player
    Minim minim;        //audio context


    public void setup() {
        size(1280, 720);
        background(0);
        frameRate(25);

        // scoreboard launch
        sb = new Board(this, new String[] {"100/101","VIVE       ", "LAS", "FLORES"});
        //           text         line  start end
        //                              seconds multiplied by frameRate
        sb.addEvent("101/101",
                                    0,   0,  17);
        sb.addEvent("KISSKISS",
                                    1,   0,  41);
        sb.addEvent("LOVELOVE",
                                    2,   0,  65);
        sb.addEvent(" ",
                                    3,   0,  20);
        sb.addEvent("FAREWELL",
                                    1,  65,  115);
        sb.addEvent("DEARS",
                                    2,  89,  120);
        // start play sound
        minim = new Minim(this);
        player = minim.loadFile("scores-sound.wav", 2048);
        player.play();

    }
    public void draw() {

        sb.nextFrame();

/*      Stopwatch timer = new Stopwatch();
        String str = timer.elapsedTime() + " ";
        System.out.println(str);
*/
        // Video capture
        // http://rsb.info.nih.gov/ij/plugins/movie-writer.html
    }

    public void stop() {
        // stop play sound
        player.close();
        minim.stop();
        super.stop();
    }

    // necessary for Processing sketch launch
    static public void main(String args[]) { PApplet.main(new String[] {"ScoreboardLaunch"}); }
}
