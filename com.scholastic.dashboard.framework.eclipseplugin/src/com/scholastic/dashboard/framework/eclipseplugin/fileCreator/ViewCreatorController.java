package com.scholastic.dashboard.framework.eclipseplugin.fileCreator;


/**
 * Suggestions:
 * JKhan:
 * 	1. Keep a local Database for all the commands.
 * 	2. Sense the OLD SAM to parse the XML command.
 */
import com.scholastic.dashboard.framework.Controller;
import com.scholastic.dashboard.framework.exceptionhandler.ExceptionController;

public class ViewCreatorController extends Controller {

	public void createModel () {
		ViewCreatorFuncCreateModel v_fn;
		try {
			v_fn = new ViewCreatorFuncCreateModel();
			v_fn.startFunction();
		} catch (Exception v_exException) {
			ExceptionController.getInstance().handleException(v_exException);
		}
	}

	public void createView () {
		ViewCreatorFuncCreateView v_fn;
		try {
			v_fn = new ViewCreatorFuncCreateView();
			v_fn.startFunction();
		} catch (Exception v_exException) {
			ExceptionController.getInstance().handleException(v_exException);
		}
	}
	
	public void createHTMLTemplate() {
		ViewCreatorFuncCreateHTMLTemplate v_fn;
		try {
			v_fn = new ViewCreatorFuncCreateHTMLTemplate();
			v_fn.startFunction();
		} catch (Exception v_exException) {
			ExceptionController.getInstance().handleException(v_exException);
		}
	}

}
