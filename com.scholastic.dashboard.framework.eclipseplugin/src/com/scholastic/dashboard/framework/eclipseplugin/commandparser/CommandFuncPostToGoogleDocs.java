package com.scholastic.dashboard.framework.eclipseplugin.commandparser;

import com.scholastic.dashboard.framework.exceptionhandler.ExceptionController;

public class CommandFuncPostToGoogleDocs extends CommandFunc {

	private String g_sCommand;

	public void setCommand(String prm_sCommand) {
		this.g_sCommand = prm_sCommand;
	}

	@Override
	public void startFunction() {
		try {
			
		} catch (Exception v_exException) {
			ExceptionController.getInstance().handleException(v_exException);
		}
	}

}
