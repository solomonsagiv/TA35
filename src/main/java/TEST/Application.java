package TEST;
import java.util.Scanner;

public class Application {

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();

		long endTime = System.currentTimeMillis();

		long duration = (endTime - startTime);
		
		System.out.println(duration);
		
	}

	private static void TextToSpeech() {
		// Defining Scanner Object to read data from console
		Scanner inputScanner = new Scanner(System.in);
		
		TextToSpeechConvertor ttsc = new TextToSpeechConvertor();

		System.out.println("Enter the Text : (type 'exit' to terminate)");

		// Reading the text
		String inputText = inputScanner.nextLine();

		while (true) {
		

			if("exit".equalsIgnoreCase(inputText)) {
				
				inputText = "Bye, See you later";
				ttsc.speak(inputText);
				break;
			}
			
			ttsc.speak(inputText);
			
			System.out.println("Enter the Text : (type 'exit' to terminate)");
			inputText = inputScanner.nextLine();
		}

		inputScanner.close();
	}
}