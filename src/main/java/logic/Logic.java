package logic;

import java.awt.Color;
import java.awt.Toolkit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JTextField;

import org.json.JSONException;

import counter.WindowTA35;
import service.MyBaseService;

public class Logic extends MyBaseService {

	public static float SAMPLE_RATE = 8000f;
	
	int min = 0;
	int minutes = 0;

	// local variables
	double hoze_0 = 0.0;
	double stock_0 = 0.0;
	int competition_Number = 0;

	int counter_Hoze = 0;
	int counter_Stock = 0;

	int hoze_up_down;
	int stock_up_down;
	
	// regular count
	int f_up = 0;
	int f_down = 0;
	int index_up = 0;
	int index_down = 0;

	boolean Hoze_Competition = false;
	boolean Stock_Competition = false;
	
	boolean run = true;
	boolean first = false;

	double margin = 0.15;

	public Logic() {
		super();
	}
	
	// logic goes here
	@SuppressWarnings("static-access")
	public void run() {
		runner();
	}

	// Runner
	private void runner() {
		try {
			
			double hoze = apiObject.getExpMonth().getOptions().getContract();
			double stock = apiObject.getIndex();

			// set for the first time the hoze and stock 0
			if (hoze_0 == 0.0 && stock_0 == 0.0 && !first) {
				first = true;
				hoze_0 = hoze;
				stock_0 = stock;
			}
			
			/**
			 * Searching for the first competition
			 **/
			if (competition_Number == 0) {

				// hoze comes up
				if (hoze >= hoze_0 + margin) {
					competition_Number = 1;
					Hoze_Competition = true;
					hoze_up_down = 1;
				}

				// hoze comes down
				if (hoze <= hoze_0 - margin) {
					competition_Number = 1;
					Hoze_Competition = true;
					hoze_up_down = 2;
				}

				// stock comes up
				if (stock >= stock_0 + margin) {
					competition_Number = 1;
					Stock_Competition = true;
					stock_up_down = 1;
				}

				// stock comes down
				if (stock <= stock_0 - margin) {
					competition_Number = 1;
					Stock_Competition = true;
					stock_up_down = 2;
				}
			}
			
			/**
			 * In one competition
			 **/
			if (competition_Number == 1) {

				// hoze start the competition
				if (Hoze_Competition) {

					// hoze is up
					if (hoze_up_down == 1) {

						// Exit 1 : no winners
						if (hoze <= hoze_0) {
							competition_Number = 0;
							Hoze_Competition = false;
							hoze_up_down = 0;
						}

						// Exit 2 : hoze win
						if (stock >= stock_0 + margin) {
							competition_Number = 0;
							Hoze_Competition = false;
							counter_Hoze += 1;
							hoze_up_down = 0;
							noisy(WindowTA35.conUpField);
							f_up = 1;

							hoze_0 = hoze;
							stock_0 = stock;

						}
						
						// new Competition
						if (stock < stock_0 - margin && !Stock_Competition) {
							competition_Number = 2;
							Stock_Competition = true;
							stock_up_down = 2;
						}
					}

					// hoze is down
					if (hoze_up_down == 2) {

						// Exit 1 : no winners
						if (hoze >= hoze_0) {
							competition_Number = 0;
							Hoze_Competition = false;
							hoze_up_down = 0;
						}

						// Exit 2 : hoze win
						if (stock <= stock_0 - margin) {
							competition_Number = 0;
							Hoze_Competition = false;
							counter_Hoze += 1;
							hoze_up_down = 0;

							f_down = 1;
							noisy(WindowTA35.conDownField);

							hoze_0 = hoze;
							stock_0 = stock;
						}

						// Exit 3 : new Competition
						if (stock > stock_0 + margin && !Stock_Competition) {
							competition_Number = 2;
							Stock_Competition = true;
							stock_up_down = 1;
						}

					}

				}

				// stock start the competition
				if (Stock_Competition) {

					// stock is up
					if (stock_up_down == 1) {

						// Exit 1 : no winners
						if (stock <= stock_0) {
							competition_Number = 0;
							Stock_Competition = false;
							stock_up_down = 0;
						}

						// Exit 2 : stock win
						if (hoze >= hoze_0 + margin) {
							competition_Number = 0;
							Stock_Competition = false;
							counter_Stock += 1;
							stock_up_down = 0;

							index_up = 1;
							noisy(WindowTA35.indUpField);
							hoze_0 = hoze;
							stock_0 = stock;
						}

						// Exit 3 : new competition
						if (hoze < hoze_0 - margin && !Hoze_Competition) {
							competition_Number = 2;
							Hoze_Competition = true;
							hoze_up_down = 2;
						}

					}

					// stock is down
					if (stock_up_down == 2) {

						// Exit 1 : no winners
						if (stock >= stock_0) {
							competition_Number = 0;
							Stock_Competition = false;
							stock_up_down = 0;
						}
						
						// Exit 2 : stock win
						if (hoze <= hoze_0 - margin) {
							competition_Number = 0;
							Stock_Competition = false;
							counter_Stock += 1;
							stock_up_down = 0;

							index_down = 1;
							noisy(WindowTA35.indDownField);
							hoze_0 = hoze;
							stock_0 = stock;

						}

						// Exit 3 : new competition
						if (hoze > hoze_0 + margin && !Hoze_Competition) {
							competition_Number = 2;
							Hoze_Competition = true;
							hoze_up_down = 1;
						}
					}
				}
			}

			/**
			 * In two competitions
			 **/
			if (competition_Number == 2) {

				// hoze up stock down
				if (hoze_up_down == 1 && stock_up_down == 2) {

					// Exit 3 : hoze close his competition
					if (hoze <= hoze_0) {
						competition_Number = 1;
						Hoze_Competition = false;
						hoze_up_down = 0;
					}

					// Exit 4 : stock close his competition
					if (stock >= stock_0) {
						competition_Number = 1;
						Stock_Competition = false;
						stock_up_down = 0;
					}
				}

				// stock up hoze down
				if (stock_up_down == 1 && hoze_up_down == 2) {

					// Exit 1 : hoze close his competition
					if (hoze >= hoze_0) {
						competition_Number = 1;
						Hoze_Competition = false;
						hoze_up_down = 0;
					}

					// Exit 2 : stock close his competition
					if (stock <= stock_0) {
						competition_Number = 1;
						Stock_Competition = false;
						stock_up_down = 0;
					}
				}
			}

			// fix 1
			if (competition_Number == 2) {
				if (!Hoze_Competition || !Stock_Competition) {
					Hoze_Competition = false;
					Stock_Competition = false;
					competition_Number = 0;
					hoze_0 = hoze;
					stock_0 = stock;
					hoze_up_down = 0;
					stock_up_down = 0;
				}
			}

			// fix 2
			if (Hoze_Competition && !Stock_Competition) {
				if (competition_Number == 2) {
					Hoze_Competition = false;
					Stock_Competition = false;
					competition_Number = 0;
					hoze_0 = hoze;
					stock_0 = stock;
					hoze_up_down = 0;
					stock_up_down = 0;
				}
			}

			// fix 3
			if (Stock_Competition && !Hoze_Competition) {
				if (competition_Number == 2) {
					Hoze_Competition = false;
					Stock_Competition = false;
					competition_Number = 0;
					hoze_0 = hoze;
					stock_0 = stock;
					hoze_up_down = 0;
					stock_up_down = 0;
				}
			}

			// fix 4
			if (!Hoze_Competition && !Stock_Competition && competition_Number == 1) {
				Hoze_Competition = false;
				Stock_Competition = false;
				competition_Number = 0;
				hoze_0 = hoze;
				stock_0 = stock;
				hoze_up_down = 0;
				stock_up_down = 0;
			}

			// coloring when competition start
			Color grey_light = new Color(219, 243, 255);

			if (hoze_up_down == 1) {
				WindowTA35.conUpField.setBackground(grey_light);
			}

			if (hoze_up_down == 2) {
				WindowTA35.conDownField.setBackground(grey_light);
			}

			if (stock_up_down == 1) {
				WindowTA35.indUpField.setBackground(grey_light);
			}

			if (stock_up_down == 2) {
				WindowTA35.indDownField.setBackground(grey_light);
			}

			// back to white
			if (hoze_up_down == 0) {
				WindowTA35.conUpField.setBackground(Color.white);
				WindowTA35.conDownField.setBackground(Color.white);
			}

			if (stock_up_down == 0) {
				WindowTA35.indUpField.setBackground(Color.white);
				WindowTA35.indDownField.setBackground(Color.white);
			}

			if (competition_Number == 0) {
				WindowTA35.indDownField.setBackground(Color.WHITE);
				WindowTA35.indUpField.setBackground(Color.WHITE);
				WindowTA35.conUpField.setBackground(Color.WHITE);
				WindowTA35.conDownField.setBackground(Color.WHITE);
			}

			// SET TO LOCALS
			apiObject.setConUp(apiObject.getConUp() + f_up);
			apiObject.setConDown(apiObject.getConDown() + f_down);
			apiObject.setIndUp(apiObject.getIndUp() + index_up);
			apiObject.setIndDown(apiObject.getIndDown() + index_down);

			f_up = 0;
			f_down = 0;
			index_up = 0;
			index_down = 0;

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			run = false;
			System.out.println("Logic is stopped ");
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// noisy
	public static void noisy(JTextField textField) throws InterruptedException, LineUnavailableException {

		Runnable thread = () -> {
			try {
				Toolkit.getDefaultToolkit().beep();
				for (int i = 0; i < 30; i++) {
					textField.setBackground(Color.YELLOW);
					Thread.sleep(100);
				}
				textField.setBackground(Color.WHITE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		};
		thread.run();
	}

	public void close() {
		run = false;
	}

	// floor
	public static double floor(double d) {
		return Math.floor(d * 100) / 100;
	}

	// set color present
	public static void setColor(int i, JTextField textField) {
		if (i >= 0) {
			textField.setText(String.valueOf(i));
			textField.setForeground(Color.BLUE);
		} else {
			textField.setText(String.valueOf(i));
			textField.setForeground(Color.RED);
		}
	}

	// tone
	public static void tone(int hz, int msecs, double vol) throws LineUnavailableException {
		byte[] buf = new byte[1];
		AudioFormat af = new AudioFormat(SAMPLE_RATE, // sampleRate
				8, // sampleSizeInBits
				1, // channels
				true, // signed
				false); // bigEndian
		SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
		sdl.open(af);
		sdl.start();
		for (int i = 0; i < msecs * 8; i++) {
			double angle = i / (SAMPLE_RATE / hz) * 2.0 * Math.PI;
			buf[0] = (byte) (Math.sin(angle) * 127.0 * vol);
			sdl.write(buf, 0, 1);
		}
		sdl.drain();
		sdl.stop();
		sdl.close();
	}

	@Override
	public void go() {
		runner();
	}

	@Override
	public String getName() {
		return "Logic";
	}

	@Override
	public int getSleep() {
		return 500;
	}

}
