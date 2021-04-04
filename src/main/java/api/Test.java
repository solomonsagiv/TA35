package api;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Second;

public class Test {

//	VoiceManager freettsVM;
//	Voice freettsVoice;
//
//	public Test(String words) {
//		// Most important part!
//		freettsVM = VoiceManager.getInstance();
//
//		// Simply change to MBROLA voice
//		freettsVoice = freettsVM.getVoices()[0];
//
//		// Allocate your chosen voice
//		freettsVoice.allocate();
//		sayWords(words);
//	}
	
	public void sayWords(String words) {
		// Make her speak!
//		freettsVoice.speak(words);
	}
	
	public static void main(String[] args) throws ParseException {

		Millisecond regularTimePeriod = new Millisecond();

		String string = "Tue Jun 16 20:13:02 IDT 2020";

		DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

		System.out.println(dateFormat.parse(string));
		
		Second second = new Second(dateFormat.parse(string));
		System.out.println(second);
			
		// System.out.println(regularTimePeriod.getStart());
		// System.out.println(regularTimePeriod.next());

	}
}