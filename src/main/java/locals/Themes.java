package locals;

import java.awt.*;
import java.util.Random;

public class Themes {


	// Colors 
	public static Color BINANCE_ORANGE = new Color(255, 204, 0);
    public static Color BINANCE_ORANGE_2 = new Color(241, 206, 125);
	public static Color BINANCE_GREY = new Color(38, 45, 51);
	public static Color BINANCE_GREEN = new Color(111,207,31);
	public static Color BINANCE_RED = new Color(255, 53, 157);
    public static Color BINANCE_RED_2 = new Color(197, 136, 196);
	
	// Races
	public static Color OPEN_RACE = new Color(148, 201, 246);
	
	public static Color BLUE = new Color(0, 51, 102);

    public static Color BLUE_4 = new Color(0, 0, 51);
	
	public static Color BLUE2 = new Color(66, 135, 245);
    public static Color BLUE4 = new Color(136, 166, 232);
	public static Color BLUE3 = new Color(32, 92, 189);

	
	public static Color LIGHT_BLUE = new Color(176, 196, 222);
	public static Color GREEN = new Color(0, 128, 0);
    public static Color GREEN_5 = new Color(2, 103, 2);
    public static Color GREEN_6 = new Color(155, 190, 155);
	public static Color RED = new Color(229, 19, 0);
	public static Color LIGHT_BLUE_3 = new Color(45, 130, 250);
	public static Color LIGHT_BLUE_4 = new Color(34, 76, 135);
    public static Color GREY_2 = new Color(160, 160, 160);

	public static Color RED_2 = new Color(171, 56, 75);

	// Fonts
	public static Font ARIEL_15 = new Font("Ariel", Font.BOLD, 15);
	public static Font ARIEL_BOLD_15 = new Font("Ariel", Font.BOLD, 15);
	
	 // Colors
    public static Color ORANGE = new Color(219, 158, 47);
    public static Color LIGHT_BLUE_2 = new Color( 0, 237, 255 );
    public static Color BLUE_STRIKE = new Color( 48, 82, 181 );
    public static Color BLUE_DARK = new Color( 0, 24, 49 );
    public static Color GREY_VERY_LIGHT = new Color( 246, 241, 246 );
    public static Color GREY_LIGHT = new Color( 234, 229, 234 );
    public static Color GREY = new Color( 203, 225, 222 );
    public static Color PURPLE = new Color( 130, 3, 194 );

    public static Color BLUE_2 = new Color( 0, 65, 171 );
    public static Color BLUE_LIGHT_2 = new Color( 79, 229, 255, 255 );

    public static Color VERY_LIGHT_BLUE = new Color(235, 228, 235);
    public static Color GREEN_LIGHT = new Color( 181, 217, 171);
    public static Color PINK_LIGHT = new Color( 255, 124, 176);
    public static Color PINK_LIGHT_2 = new Color(191, 115, 154);

//	public static Color GREEN = new Color( 0 , 128 , 0 );

    public static Font ARIEL_BOLD_14 = new Font( "Ariel", Font.BOLD, 14 );
    public static Font ARIEL_BOLD_12 = new Font( "Ariel", Font.BOLD, 12 );
    public static Font VEDANA_12 = new Font( "Verdana", Font.PLAIN, 12 );


    public static Color getRamdomColor() {
        Random random = new Random();
        return new Color( random.nextInt( 256 ), random.nextInt( 256 ), random.nextInt( 256 ) );
    }

	
}
