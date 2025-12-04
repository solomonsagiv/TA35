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
    public static Color DARKER_BLUE_BG = new Color(30, 38, 50);      // רקע כהה יותר
    public static Color LIGHT_BLUE_BG = new Color(66, 82, 110);      // רקע פאנלים
    public static Color DARK_TEXT_FIELD_BG = new Color(30, 40, 55);  // רקע כהה מאוד ל-TextFields
    public static Color ACCENT_BLUE = new Color(79, 172, 254);       // כחול אקסנט
    public static Color WHITE_TEXT = new Color(255, 255, 255);       // טקסט לבן
    public static Color BRIGHT_WHITE_TEXT = new Color(255, 255, 255); // טקסט לבן בהיר מאוד
    public static Color LIGHT_GRAY_TEXT = new Color(200, 200, 200);  // טקסט אפור בהיר
    
    // P&L Colors
    public static Color PROFIT_GREEN = new Color(76, 175, 80);       // ירוק לרווח
    public static Color LOSS_RED = new Color(244, 67, 54);           // אדום להפסד
    public static Color NEUTRAL_GRAY = new Color(158, 158, 158);     // אפור לאפס
    
    // Dark Mode
    private static boolean isDarkMode = false;
    
    // Standard colors - Light mode
    public static final Color WHITE = Color.WHITE;
    public static final Color STANDARD_BLACK = Color.BLACK;
    
    // Light mode backgrounds
    public static Color LIGHT_BG_WHITE = Color.WHITE;
    public static Color LIGHT_BG_STRIPE = new Color(0xFAFAFA);
    public static Color LIGHT_BG_GRID = new Color(0xEEEEEE);
    public static Color LIGHT_BG_HEADER = new Color(0xF5F7FA);
    public static Color LIGHT_BG_SELECTION = new Color(0xE3F2FD);
    public static Color LIGHT_BG_STRIKE = new Color(0xEEF3FF);
    
    // Light mode text colors
    public static Color LIGHT_TEXT_HEADER = new Color(0x263238);
    public static Color LIGHT_TEXT_SELECTION = new Color(0x000000);
    public static Color LIGHT_TEXT_GREEN = new Color(0x1B5E20);
    public static Color LIGHT_TEXT_RED = new Color(0xB71C1C);
    
    // Light mode highlight colors
    public static Color LIGHT_HL_GREEN = new Color(0xC8E6C9);
    public static Color LIGHT_HL_RED = new Color(0xFFCDD2);
    
    // Light mode button colors
    public static Color LIGHT_BUTTON_BG = new Color(211, 211, 211);
    public static Color LIGHT_BUTTON_FG = new Color(0, 0, 51);
    public static Color LIGHT_LOG_BG = new Color(176, 196, 222);
    
    // Light mode status colors
    public static Color LIGHT_GREEN_STATUS = new Color(12, 135, 0);
    public static Color LIGHT_RED_STATUS = new Color(229, 19, 0);
    
    // Dark mode backgrounds
    public static Color DARK_BG_MAIN = new Color(30, 38, 50);           // רקע ראשי כהה
    public static Color DARK_BG_PANEL = new Color(45, 55, 72);          // רקע פאנלים
    public static Color DARK_BG_DARKER = new Color(20, 26, 35);         // רקע כהה יותר
    public static Color DARK_BG_STRIPE = new Color(40, 48, 60);         // רקע פסים
    public static Color DARK_BG_GRID = new Color(35, 43, 55);           // רקע רשת
    public static Color DARK_BG_HEADER = new Color(50, 60, 75);         // רקע כותרת
    public static Color DARK_BG_SELECTION = new Color(60, 80, 110);      // רקע בחירה
    public static Color DARK_BG_STRIKE = new Color(40, 55, 80);         // רקע Strike
    
    // Dark mode text colors
    public static Color DARK_TEXT_MAIN = new Color(255, 255, 255);       // טקסט ראשי
    public static Color DARK_TEXT_SECONDARY = new Color(200, 200, 200);  // טקסט משני
    public static Color DARK_TEXT_HEADER = new Color(220, 220, 220);    // טקסט כותרת
    public static Color DARK_TEXT_SELECTION = new Color(255, 255, 255); // טקסט בחירה
    public static Color DARK_TEXT_GREEN = new Color(100, 255, 150);     // טקסט ירוק
    public static Color DARK_TEXT_RED = new Color(255, 100, 100);       // טקסט אדום
    
    // Dark mode highlight colors
    public static Color DARK_HL_GREEN = new Color(50, 150, 80);         // הדגשה ירוקה
    public static Color DARK_HL_RED = new Color(200, 60, 70);           // הדגשה אדומה
    
    // Dark mode button colors
    public static Color DARK_BUTTON_BG = new Color(60, 70, 85);
    public static Color DARK_BUTTON_FG = new Color(255, 255, 255);
    public static Color DARK_LOG_BG = new Color(40, 50, 65);
    
    // Dark mode status colors (bright for visibility)
    public static Color DARK_GREEN_STATUS = new Color(76, 255, 76);     // ירוק בהיר
    public static Color DARK_RED_STATUS = new Color(255, 76, 76);       // אדום בהיר
    
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
    
    // Helper methods to get colors based on dark mode
    public static Color getBackgroundColor() {
        return isDarkMode() ? DARK_BG_MAIN : LIGHT_BG_WHITE;
    }
    
    public static Color getPanelBackgroundColor() {
        return isDarkMode() ? DARK_BG_PANEL : GREY_LIGHT;
    }
    
    public static Color getTextFieldBackgroundColor() {
        return isDarkMode() ? DARK_TEXT_FIELD_BG : GREY_VERY_LIGHT;
    }
    
    public static Color getTextFieldForegroundColor() {
        return isDarkMode() ? BRIGHT_WHITE_TEXT : STANDARD_BLACK;
    }
    
    public static Color getTextColor() {
        return isDarkMode() ? DARK_TEXT_MAIN : STANDARD_BLACK;
    }
    
    public static Color getHeaderBackgroundColor() {
        return isDarkMode() ? DARK_BG_HEADER : LIGHT_BG_HEADER;
    }
    
    public static Color getHeaderTextColor() {
        return isDarkMode() ? DARK_TEXT_HEADER : LIGHT_TEXT_HEADER;
    }
    
    public static Color getSelectionBackgroundColor() {
        return isDarkMode() ? DARK_BG_SELECTION : LIGHT_BG_SELECTION;
    }
    
    public static Color getSelectionTextColor() {
        return isDarkMode() ? DARK_TEXT_SELECTION : LIGHT_TEXT_SELECTION;
    }
    
    public static Color getStripeBackgroundColor() {
        return isDarkMode() ? DARK_BG_STRIPE : LIGHT_BG_STRIPE;
    }
    
    public static Color getGridColor() {
        return isDarkMode() ? DARK_BG_GRID : LIGHT_BG_GRID;
    }
    
    public static Color getStrikeBackgroundColor() {
        return isDarkMode() ? DARK_BG_STRIKE : LIGHT_BG_STRIKE;
    }
    
    public static Color getGreenTextColor() {
        return isDarkMode() ? DARK_TEXT_GREEN : LIGHT_TEXT_GREEN;
    }
    
    public static Color getRedTextColor() {
        return isDarkMode() ? DARK_TEXT_RED : LIGHT_TEXT_RED;
    }
    
    public static Color getGreenHighlightColor() {
        return isDarkMode() ? DARK_HL_GREEN : LIGHT_HL_GREEN;
    }
    
    public static Color getRedHighlightColor() {
        return isDarkMode() ? DARK_HL_RED : LIGHT_HL_RED;
    }
    
    public static Color getButtonBackgroundColor() {
        return isDarkMode() ? DARK_BUTTON_BG : LIGHT_BUTTON_BG;
    }
    
    public static Color getButtonForegroundColor() {
        return isDarkMode() ? DARK_BUTTON_FG : LIGHT_BUTTON_FG;
    }
    
    public static Color getLogBackgroundColor() {
        return isDarkMode() ? DARK_LOG_BG : LIGHT_LOG_BG;
    }
    
    public static Color getGreenStatusColor() {
        return isDarkMode() ? DARK_GREEN_STATUS : LIGHT_GREEN_STATUS;
    }
    
    public static Color getRedStatusColor() {
        return isDarkMode() ? DARK_RED_STATUS : LIGHT_RED_STATUS;
    }
    
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
