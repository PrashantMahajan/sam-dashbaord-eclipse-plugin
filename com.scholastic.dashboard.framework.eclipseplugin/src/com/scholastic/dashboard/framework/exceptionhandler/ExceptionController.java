package com.scholastic.dashboard.framework.exceptionhandler;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.scholastic.dashboard.framework.Controller;
import com.scholastic.dashboard.framework.eclipseplugin.wizards.DashboardViewPage;

public class ExceptionController extends Controller {

	public static ExceptionController getInstance () {
		return new ExceptionController();
	}

	private ExceptionController () {
		
	}
	
	public void handleException (Exception prm_exException) {
		DashboardViewPage.FileGenerateForm.log(getExceptionStackTraceAsString(prm_exException));
		prm_exException.printStackTrace();
	}
	public static String getExceptionStackTraceAsString(Exception exception) {
		StringWriter sw = new StringWriter();
		exception.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
	
}
