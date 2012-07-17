package com.scholastic.dashboard.framework.eclipseplugin.commandparser;

import com.scholastic.dashboard.framework.Functioncall;

public abstract class CommandFunc extends Functioncall {

	private CommandParserController g_objController;

	protected CommandParserController getController () {
		if (null == this.g_objController) {
			this.g_objController = CommandParserController.getInstane();
		}
		return this.g_objController;
	}
	
	protected void setControler (CommandParserController prm_objController) {
		this.g_objController = prm_objController;
	}
	
}
