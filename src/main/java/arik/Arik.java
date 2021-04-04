package arik;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.request.SendMessage;

public class Arik {
	
	private static Arik arik;
	
	public static int sagivID = 365117561;
	public static int yosiID = 516690674;
	
	private TelegramBot bot;

	private int updateId = 0;

	private Arik() {
		bot = TelegramBotAdapter.build("400524449:AAE4dPbl22dfI9lB1r17W4ivqz2lc4C1xUY");
	}

	// Get instance
	public static Arik getInstance() {
		if (arik == null) {
			arik = new Arik();
		}
		return arik;
	}
	
	public void close() {
		arik = null;
		bot = null;
	}

	   public void sendMessage( String text ) {
	        getBot( ).execute( new SendMessage( sagivID, text ) );
	        updateId += 1;
	    }

	    public void sendErrorMessage( Exception e ) {
	        String text = e.getMessage() + "\n" + e.getCause();
	        getBot( ).execute( new SendMessage( sagivID, text ) );
	        updateId += 1;
	    }
	
	
	// Send message
	public void sendMessage(Update update, String text, Keyboard keyBoard) {
		if (keyBoard != null) {
			getBot().execute(new SendMessage(update.message().from().id(), text).replyMarkup(keyBoard));
			updateId = update.updateId() + 1;
		} else {
			getBot().execute(new SendMessage(update.message().from().id(), text));
			updateId = update.updateId() + 1;
		}
	}

	// Send message
	public void sendMessage(int id, String text, Keyboard keyBoard) {
		if (keyBoard != null) {
			getBot().execute(new SendMessage(id, text).replyMarkup(keyBoard));
			updateId += 1;
		} else {
			getBot().execute(new SendMessage(id, text));
			updateId += 1;
		}
	}

	// ----------- Getters and Setters ---------- //

	public int getUpdateId() {
		return updateId;
	}

	public void setUpdateId(int updateId) {
		this.updateId = updateId;
	}

	public TelegramBot getBot() {
		return bot;
	}

	public void setBot(TelegramBot bot) {
		this.bot = bot;
	}

}
