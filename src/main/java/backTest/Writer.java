package backTest;

import api.TA35;
import api.dde.DDE.DDEConnection;
import locals.L;
import options.Options;
import options.Strike;

import java.util.List;

import com.pretty_tools.dde.DDEException;
import com.pretty_tools.dde.client.DDEClientConversation;

public class Writer {
	
    private TA35 ta35;
    private final String excel_path = "C:/Users/yosef/OneDrive/Desktop/[Vega trading.xlsx]IV Import";
    private DDEClientConversation conversation;

	int strike_col = 2;
	int iv_col = 3;
	int mid_col = 4;
	int interest_col = 5;
	int days_to_exp_col = 6;
	int contract_col = 7;

	// Write to JText field
	public Writer(TA35 ta35) {
		this.ta35 = ta35;
		this.conversation = new DDEConnection().createNewConversation(excel_path);
	}

	public void write_iv() {
		int start_row = 2;
		List<Strike> strikes;

		// // Week
		// strikes = ta35.getExps().getWeek().getOptions().getStrikes();
		// for (Strike strike : strikes) {
		// 	try {
		// 		conversation.poke(L.cell(start_row, strike_col), String.valueOf(strike.getStrike()));
		// 		conversation.poke(L.cell(start_row, iv_col), String.valueOf(strike.getVolatility()));
		// 		start_row++;
		// 	} catch (DDEException e) {
		// 		e.printStackTrace();
		// 	}
		// }

		// start_row++;

		// Month
		strikes = ta35.getExps().getMonth().getOptions().getStrikes();

		Options options = ta35.getExps().getMonth().getOptions();
		for (Strike strike : strikes) {
			try {
				conversation.poke(L.cell(start_row, strike_col), String.valueOf(strike.getStrike()));
				conversation.poke(L.cell(start_row, iv_col), String.valueOf(strike.getIv()));
				conversation.poke(L.cell(start_row, mid_col), String.valueOf(ta35.getMid()));
				conversation.poke(L.cell(start_row, interest_col), String.valueOf(options.getInterest_rate()));
				conversation.poke(L.cell(start_row, days_to_exp_col), String.valueOf(options.getDays_to_exp()));
				conversation.poke(L.cell(start_row, contract_col), String.valueOf(options.getContract()));
				start_row++;
			} catch (DDEException e) {
				e.printStackTrace();
			}
		}

	}

}
