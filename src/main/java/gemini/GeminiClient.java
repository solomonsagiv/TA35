package gemini;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class GeminiClient {

    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";
    private final String apiKey;
    private final String modelName;
    private final HttpClient httpClient;
    private final Gson gson;

    // בנאי (Constructor) שמקבל את המפתח ושם המודל (למשל "gemini-1.5-flash")
    public GeminiClient(String apiKey, String modelName) {
        this.apiKey = apiKey;
        this.modelName = modelName;
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    // פונקציה ראשית לשליחת הודעה וקבלת תשובה
    public String chat(String prompt) throws Exception {
        // 1. בניית ה-URL
        String endpoint = BASE_URL + modelName + ":generateContent?key=" + apiKey;

        // 2. יצירת גוף הבקשה (JSON)
        // המבנה הוא: { "contents": [{ "parts": [{ "text": "YOUR PROMPT" }] }] }
        JsonObject textPart = new JsonObject();
        textPart.addProperty("text", prompt);

        JsonArray parts = new JsonArray();
        parts.add(textPart);

        JsonObject content = new JsonObject();
        content.add("parts", parts);

        JsonArray contents = new JsonArray();
        contents.add(content);

        JsonObject requestBody = new JsonObject();
        requestBody.add("contents", contents);

        String jsonInputString = gson.toJson(requestBody);

        // 3. יצירת בקשת HTTP POST
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonInputString, StandardCharsets.UTF_8))
                .build();

        // 4. שליחת הבקשה
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error: " + response.statusCode() + " Body: " + response.body());
        }

        // 5. פענוח התשובה (Parsing)
        return extractTextFromResponse(response.body());
    }

    // פונקציית עזר לחילוץ הטקסט מתוך ה-JSON המורכב שחוזר מגוגל
    private String extractTextFromResponse(String jsonResponse) {
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonArray candidates = jsonObject.getAsJsonArray("candidates");
            
            if (candidates != null && candidates.size() > 0) {
                JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
                JsonObject content = firstCandidate.getAsJsonObject("content");
                JsonArray parts = content.getAsJsonArray("parts");
                
                if (parts != null && parts.size() > 0) {
                    return parts.get(0).getAsJsonObject().get("text").getAsString();
                }
            }
        } catch (Exception e) {
            return "Error parsing response: " + e.getMessage();
        }
        return "No response text found.";
    }
}