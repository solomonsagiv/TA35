package gemini;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CheckModels {
    public static void main(String[] args) {
        // --- שים כאן את המפתח שלך ---
        String apiKey = "AIzaSyDgKTGkMuhgRqD_je4-bcJEqUuu_LrovrQ"; 
        
        String url = "https://generativelanguage.googleapis.com/v1beta/models?key=" + apiKey;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            System.out.println("בודק רשימת מודלים זמינים...");
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                System.out.println("--- הצלחה! הנה המודלים הזמינים עבורך: ---");
                System.out.println(response.body());
                System.out.println("----------------------------------------");
                System.out.println("חפש ברשימה למעלה שמות כמו 'name': 'models/gemini-1.5-flash'");
                System.out.println("העתק את השם המדויק (בלי ה-models/) לקוד הראשי שלך.");
            } else {
                System.out.println("שגיאה בקבלת הרשימה: " + response.statusCode());
                System.out.println(response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
