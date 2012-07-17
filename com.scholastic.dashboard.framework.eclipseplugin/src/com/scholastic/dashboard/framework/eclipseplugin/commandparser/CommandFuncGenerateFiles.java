package com.scholastic.dashboard.framework.eclipseplugin.commandparser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import com.scholastic.dashboard.framework.exceptionhandler.ExceptionController;

public class CommandFuncGenerateFiles extends CommandFunc {

	private Map<String, String> g_hKeywordValues = null;
	private String g_sModelName = null;
	private String g_sHtmlTemplateFileName = null;
	private String g_sCurrentDirectory = null;

	public void setCurrentDirectory(String prm_sCurrentDirectory) {
		this.g_sCurrentDirectory = prm_sCurrentDirectory;
	}

	public void setKeywords(Map<String, String> prm_hKeyWordValues) {
		this.g_hKeywordValues = prm_hKeyWordValues;
	}

	public void startFunction () {
		try {
			if (null == this.g_hKeywordValues) {
			} else {
				this.g_sModelName = this.g_hKeywordValues.get("_ModelType_");
				this.g_sHtmlTemplateFileName = this.g_hKeywordValues.get("_HTMLTemplateFileName_");
				this.printCurrentDirectory();
				this.generateView();
				this.generateModel();
				this.generateHtml();
			}
		} catch (Exception v_exException) {
			ExceptionController.getInstance().handleException(v_exException);
		}
	}

	private String applyKeywords(String prm_sTemplate) {
		String v_Return = "";
		String v_sKey;
		String v_sValue;
		Iterator<String> v_itrKeys;
		try {
			v_Return = prm_sTemplate;
			v_itrKeys = this.g_hKeywordValues.keySet().iterator();
			while (v_itrKeys.hasNext()) {
				v_sKey = v_itrKeys.next();
				v_sValue = this.g_hKeywordValues.get(v_sKey);
				v_Return = v_Return.replaceAll("\\[" + v_sKey + "\\]", v_sValue);
			}
		} catch (Exception v_exException) {
			ExceptionController.getInstance().handleException(v_exException);
		}
		return v_Return;
	}

	private void generateHtml() {
		String v_sTemplate;
		String v_sFileContent;
		try {
			v_sTemplate = this.readFile("html.template");
			v_sFileContent = this.applyKeywords(v_sTemplate);
			this.writeFile("template/" + this.g_sHtmlTemplateFileName + ".html", v_sFileContent);
		} catch (Exception v_exException) {
			ExceptionController.getInstance().handleException(v_exException);
		}
	}

	private void generateModel() {
		String v_sTemplate;
		String v_sFileContent;
		try {
			v_sTemplate = this.readFile("model.template");
			v_sFileContent = this.applyKeywords(v_sTemplate);
			this.writeFile("js/models/" + this.g_sModelName + ".js", v_sFileContent);
		} catch (Exception v_exException) {
			ExceptionController.getInstance().handleException(v_exException);
		}
	}

	private void generateView() {
		String v_sTemplate;
		String v_sFileContent;
		try {
			v_sTemplate = this.readFile("view.template");
			v_sFileContent = this.applyKeywords(v_sTemplate);
			this.writeFile("js/views/" + this.g_sModelName + "ProfileForm.js", v_sFileContent);
		} catch (Exception v_exException) {
			ExceptionController.getInstance().handleException(v_exException);
		}
	}

	private void printCurrentDirectory() {
		try {
			System.out.println(new File(".").getAbsolutePath());
		} catch (Exception v_exException) {
			ExceptionController.getInstance().handleException(v_exException);
		}
	}
	
	private String readFile(String prm_sFileName) {
		StringBuilder v_Return = null;
		InputStream v_objInputStream;
		byte v_bByte[];
		try {
			v_Return = new StringBuilder();
			v_objInputStream = this.getClass().getResourceAsStream("/" + prm_sFileName);
			v_bByte = new byte[1];
			while (v_objInputStream.read(v_bByte) != -1 ) {
				v_Return.append(new String(v_bByte));
			}
			v_objInputStream.close();
			System.out.println("Resource file Read : " + v_Return);
		} catch (Exception v_exException) {
			ExceptionController.getInstance().handleException(v_exException);
		}
		return v_Return.toString();
	}

	private void writeFile (String prm_sFileName, String prm_sFileContent) {
		FileWriter v_objFileStream;
		BufferedWriter v_objFileOut;
		try {
			if (new File(this.g_sCurrentDirectory + "/WebContent/js/" + prm_sFileName).exists()) {
			} else {
				v_objFileStream = new FileWriter(this.g_sCurrentDirectory + "/WebContent/" + prm_sFileName);
				v_objFileOut = new BufferedWriter(v_objFileStream);
				v_objFileOut.write(prm_sFileContent);
				v_objFileOut.close();
			}
		} catch (Exception v_exException) {
			ExceptionController.getInstance().handleException(v_exException);
		}
	}
}
