package TEST;

import java.util.Arrays;


public class TextToSpeechConvertor {

	public static void main( String[] args ) {
		TextToSpeechConvertor  text = new TextToSpeechConvertor();
		text.speak( " hedson barboza" );
	}

	// Some available voices are (kevin, kevin16, alan)
//	private static final String VOICE_NAME_KEVIN = "kevin16";
//	private final Voice voice;
//
//	public TextToSpeechConvertor() {
//
//		VoiceManager vm = VoiceManager.getInstance();
//		voice = vm.getVoice(VOICE_NAME_KEVIN);
//
//		System.out.println(Arrays.deepToString(vm.getVoices()));
//
//		System.out.println(vm.getVoices().length);
//
//		voice.allocate();
//
//
//
//	}

	public void speak(String inputText) {

		if(inputText != null && !inputText.isEmpty()) {
			
//			voice.speak(inputText);
		}
	}

}

