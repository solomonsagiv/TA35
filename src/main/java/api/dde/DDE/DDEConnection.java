package api.dde.DDE;
/*
 * Copyright 2009 www.pretty-tools.com. All rights reserved.
 */

import api.ApiObject;
import com.pretty_tools.dde.DDEException;
import com.pretty_tools.dde.client.DDEClientConversation;

import javax.swing.*;
/**
 * Excel Example.
 *
 * @author Alexander Kozlov
 */
public class DDEConnection {

	ApiObject apiObject;

	// Get conversation instance
	public DDEClientConversation createNewConversation(String excelPath) {
		DDEClientConversation conversation = new DDEClientConversation();
		conversation.setTimeout(3000);
		// Connect to the excel
		try {
			System.out.println(excelPath);
			conversation.connect("Excel", excelPath);
		} catch (DDEException e) {
			JOptionPane.showConfirmDialog(null, e.getMessage() + "\n" + e.getCause());
			e.printStackTrace();
		} catch (Exception e) {
			JOptionPane.showConfirmDialog(null, e.getMessage() + "\n" + e.getCause());
		}
		return conversation;
	}
	
	// Empty Constructor
	public DDEConnection(ApiObject apiObject) {
		this.apiObject = apiObject;
	}
	
	public double dbl(String s) {
		return Double.parseDouble(s.replace(",", ""));
	}
	
	

}