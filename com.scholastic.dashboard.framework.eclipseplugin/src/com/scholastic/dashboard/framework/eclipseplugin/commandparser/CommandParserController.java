package com.scholastic.dashboard.framework.eclipseplugin.commandparser;

import java.util.Map;

import com.scholastic.dashboard.framework.Controller;

public class CommandParserController extends Controller {

	public static CommandParserController getInstane () {
		return new CommandParserController();
	}

	private CommandParserController () {
	}

	public void generateFiles (String prm_sFileName, String prm_sCrudeCommand, String prm_sCurrentDirectory) {
		this.generateFiles(this.parseCommand(prm_sFileName, prm_sCrudeCommand), prm_sCurrentDirectory);
	}

	public void generateFiles (Map<String, String> prm_hKeyWordValues, String prm_sCurrentDirectory) {
		CommandFuncGenerateFiles v_fn;
		v_fn = new CommandFuncGenerateFiles();
		v_fn.setCurrentDirectory(prm_sCurrentDirectory);
		v_fn.setKeywords(prm_hKeyWordValues);
		v_fn.startFunction();
	}

	public Map<String, String> parseCommand (String prm_sFileName, String prm_sCrudeCommand) {
		CommandFuncParseCommand v_fn;
		v_fn = new CommandFuncParseCommand();
		v_fn.setControler(this);
		v_fn.setFileName(prm_sFileName);
		v_fn.setCommand(prm_sCrudeCommand);
		v_fn.startFunction();
		return v_fn.getResult();
	}

	public void postCommandToRemoteDB (String prm_sCommand) {
		CommandFuncPostToGoogleDocs v_fn;
		v_fn = new CommandFuncPostToGoogleDocs();
		v_fn.setControler(this);
		v_fn.setCommand(prm_sCommand);
		v_fn.startFunction();
	}

}
