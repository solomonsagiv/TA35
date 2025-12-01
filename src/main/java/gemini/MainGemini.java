package gemini;

public class MainGemini {
    public static void main(String[] args) {
        // החלף את זה במפתח ה-API האמיתי שלך מ-Google AI Studio
        String myApiKey = "AIzaSyDgKTGkMuhgRqD_je4-bcJEqUuu_LrovrQ"; 
        
        // בחירת המודל (כרגע 1.5 הוא העדכני, בעתיד תוכל לשנות ל-gemini-3.0-pro)
        String model = "models/gemini-1.5-flash";

        GeminiClient gemini = new GeminiClient(myApiKey, model);

        try {
            System.out.println("שולח בקשה ל-Gemini...");
            
            String question = "תכתוב לי פונקציה ב-Java שמחשבת סדרת פיבונאצ'י";
            String answer = gemini.chat(question);

            System.out.println("--- תשובת המודל ---");
            System.out.println(answer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
