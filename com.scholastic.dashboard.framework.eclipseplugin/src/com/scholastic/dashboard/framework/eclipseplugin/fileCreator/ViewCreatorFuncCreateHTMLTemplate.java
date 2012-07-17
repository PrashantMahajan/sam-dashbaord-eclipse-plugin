package com.scholastic.dashboard.framework.eclipseplugin.fileCreator;

import com.scholastic.dashboard.framework.exceptionhandler.ExceptionController;

public class ViewCreatorFuncCreateHTMLTemplate extends ViewCreatorFunc {

	@Override
	public void startFunction() {
		try {
			
		} catch (Exception v_exException) {
			ExceptionController.getInstance().handleException(v_exException);
		}
	}

}
