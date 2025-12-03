package locals;

import java.awt.*;
import java.util.Random;

public class Themes {


    // Colors
    public static final Color BLACK = new Color(0, 0, 0);
    public static Color BINANCE_ORANGE = new Color(255, 204, 0);
    public static Color BINANCE_ORANGE_2 = new Color(241, 206, 125);
    public static Color BINANCE_GREY = new Color(38, 45, 51);
    public static Color BINANCE_GREEN = new Color(111, 207, 31);
    public static Color BINANCE_RED = new Color(255, 182, 182);
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
    public static Color GREEN_7 = new Color(117, 182, 117);
    public static Color RED = new Color(229, 19, 0);
    public static Color LIGHT_BLUE_3 = new Color(45, 130, 250);
    public static Color LIGHT_BLUE_4 = new Color(34, 76, 135);
    public static Color GREY_2 = new Color(160, 160, 160);

    public static Color RED_2 = new Color(171, 56, 75);

    // Fonts
    public static Font ARIEL_17 = new Font("Ariel", Font.PLAIN, 17);
    public static Font ARIEL_14 = new Font("Ariel", Font.PLAIN, 14);
    public static Font ARIEL_BOLD_15 = new Font("Ariel", Font.BOLD, 15);
    public static Font VERDANA_PLAIN_12 = new Font("Verdana", Font.PLAIN, 12);

    // Colors
    public static Color ORANGE = new Color(219, 158, 47);
    public static Color ORANGE_2 = new Color(154, 91, 0);
    public static Color LIGHT_BLUE_2 = new Color(0, 237, 255);
    public static Color BLUE_STRIKE = new Color(48, 82, 181);
    public static Color BLUE_DARK = new Color(0, 24, 49);
    public static Color GREY_VERY_LIGHT = new Color(246, 241, 246);
    public static Color GREY_LIGHT = new Color(234, 229, 234);
    public static Color GREY = new Color(203, 225, 222);
    public static Color PURPLE = new Color(130, 3, 194);

    public static Color BLUE_2 = new Color(0, 65, 171);
    public static Color BLUE_LIGHT_2 = new Color(79, 229, 255, 255);

    public static Color VERY_LIGHT_BLUE = new Color(235, 228, 235);
    public static Color GREEN_LIGHT = new Color(202, 226, 197);
    public static Color PINK_LIGHT = new Color(255, 124, 176);
    public static Color PINK_LIGHT_2 = new Color(191, 115, 154);



    // Position Tracker Colors
    public static Color DARK_BLUE_BG = new Color(45, 55, 72);        // רקע כהה
    public static Color LIGHT_BLUE_BG = new Color(66, 82, 110);      // רקע פאנלים
    public static Color ACCENT_BLUE = new Color(79, 172, 254);       // כחול אקסנט
    public static Color WHITE_TEXT = new Color(255, 255, 255);       // טקסט לבן
    public static Color LIGHT_GRAY_TEXT = new Color(200, 200, 200);  // טקסט אפור בהיר
    
    // P&L Colors
    public static Color PROFIT_GREEN = new Color(76, 175, 80);       // ירוק לרווח
    public static Color LOSS_RED = new Color(244, 67, 54);           // אדום להפסד
    public static Color NEUTRAL_GRAY = new Color(158, 158, 158);     // אפור לאפס
    
    // Dark Mode
    private static boolean isDarkMode = false;
    
    // Bright/Glowing colors for dark mode charts
    public static Color DARK_MODE_BRIGHT_WHITE = new Color(255, 255, 255);        // לבן זוהר
    public static Color DARK_MODE_BRIGHT_CYAN = new Color(0, 255, 255);            // ציאן זוהר
    public static Color DARK_MODE_BRIGHT_GREEN = new Color(0, 255, 128);          // ירוק זוהר
    public static Color DARK_MODE_BRIGHT_YELLOW = new Color(255, 255, 0);         // צהוב זוהר
    public static Color DARK_MODE_BRIGHT_ORANGE = new Color(255, 165, 0);         // כתום זוהר
    public static Color DARK_MODE_BRIGHT_RED = new Color(255, 64, 64);            // אדום זוהר
    public static Color DARK_MODE_BRIGHT_PURPLE = new Color(200, 100, 255);       // סגול זוהר
    public static Color DARK_MODE_BRIGHT_BLUE = new Color(100, 150, 255);         // כחול זוהר
    public static Color DARK_MODE_BRIGHT_PINK = new Color(255, 100, 200);         // ורוד זוהר
    
    public static boolean isDarkMode() {
        return isDarkMode;
    }
    
    public static void toggleDarkMode() {
        isDarkMode = !isDarkMode;
    }
    
    public static void setDarkMode(boolean dark) {
        isDarkMode = dark;
    }
    
    /**
     * Converts a color to a bright/glowing version for dark mode
     * If dark mode is off, returns the original color
     */
    public static Color getBrightColorForDarkMode(Color originalColor) {
        if (!isDarkMode) {
            return originalColor;
        }
        
        // Map common colors to bright versions
        if (originalColor.equals(Color.BLACK)) {
            return DARK_MODE_BRIGHT_WHITE;
        } else if (originalColor.equals(Themes.RED) || originalColor.equals(Color.RED)) {
            return DARK_MODE_BRIGHT_RED;
        } else if (originalColor.equals(Themes.GREEN) || originalColor.equals(Color.GREEN)) {
            return DARK_MODE_BRIGHT_GREEN;
        } else if (originalColor.equals(Themes.BLUE) || originalColor.equals(Color.BLUE)) {
            return DARK_MODE_BRIGHT_BLUE;
        } else if (originalColor.equals(Themes.ORANGE)) {
            return DARK_MODE_BRIGHT_ORANGE;
        } else if (originalColor.equals(Themes.PURPLE)) {
            return DARK_MODE_BRIGHT_PURPLE;
        } else {
            // For other colors, brighten them
            int r = Math.min(255, originalColor.getRed() + 100);
            int g = Math.min(255, originalColor.getGreen() + 100);
            int b = Math.min(255, originalColor.getBlue() + 100);
            return new Color(r, g, b);
        }
    }





//	public static Color GREEN = new Color( 0 , 128 , 0 );

    public static Font ARIEL_BOLD_14 = new Font("Ariel", Font.BOLD, 14);
    public static Font ARIEL_BOLD_12 = new Font("Ariel", Font.BOLD, 12);
    public static Font VEDANA_12 = new Font("Verdana", Font.PLAIN, 12);


    public static Color getRamdomColor() {
        Random random = new Random();
        return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }


}
