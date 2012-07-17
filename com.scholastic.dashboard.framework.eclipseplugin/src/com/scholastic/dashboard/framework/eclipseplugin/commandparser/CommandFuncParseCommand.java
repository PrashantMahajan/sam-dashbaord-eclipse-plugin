package com.scholastic.dashboard.framework.eclipseplugin.commandparser;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.scholastic.dashboard.framework.exceptionhandler.ExceptionController;

public class CommandFuncParseCommand extends CommandFunc {

	private Map<String, String> g_Return = new HashMap<String, String>();
	private String g_sCommand = null;
	private String g_sFileName = null;
	private String g_sHtmlTemplate = null;
	private Document g_xmlCommand = null;
	private Node g_objRoot = null;
	private Set<String> g_lAllRequiredIds = new LinkedHashSet<String>();
	private Set<String> g_lAllIds = new LinkedHashSet<String>();
	private Map<String, String> g_hAllLabels = new HashMap<String, String>();

	public Map<String, String> getResult () {
		return this.g_Return;
	}

	public void setCommand (String prm_sCommand) {
		this.g_sCommand = prm_sCommand;
	}

	public void setFileName(String prm_sFileName) {
		this.g_sFileName = prm_sFileName;
	}

	@Override
	public void startFunction() {
		try {
			this.postToGoogleDocsDatabase();
			this.extractKeywordValues();
		} catch (Exception v_exException) {
			ExceptionController.getInstance().handleException(v_exException);
		}
	}

	private void extractKeywordValues() {
		try {
			if(this.loadXMLTree()) {
				this.setFieldIds(this.g_objRoot);//[_FieldIDs_]
				this.setModelType();//[_ModelType_]
				this.setCommand();//[_Command_]
				this.setRequiredFields(this.g_objRoot, 0);//[_RequiredFields_]
				this.setTemplateFileName();//[_HTMLTemplateFileName_]
				this.setHtmlTemplate();//[_HtmlTemplate_]
			}
		} catch (Exception v_exException) {
			ExceptionController.getInstance().handleException(v_exException);
		}
	}

	private void generateHtml() {
		String v_sTempalte;
		String v_sLabel;
		String v_sFieldId;
		StringBuilder v_sb;
		try {
			v_sb = new StringBuilder();
			for (String v_sTag : this.g_lAllIds) {
				v_sFieldId = v_sTag;
				v_sLabel = this.g_hAllLabels.get(v_sTag);
				v_sTempalte =
					"\t\t\t\t<div class=\"form_row\">\n" +
						"\t\t\t\t\t<label for=\"" + v_sFieldId + "\">" + v_sLabel + "</label></input>\n" +
						"\t\t\t\t\t<input type=\"text\" name=\"" + v_sFieldId + "\" id=\"" + v_sFieldId + "\" value=\"<%= model.get('" + v_sFieldId + "') %>\" class=\"text ui-widget-content ui-corner-all\" maxlength=\"50\"></input>\n" +
					"\t\t\t\t</div>\n";
				v_sb.append(v_sTempalte);
			}
			this.g_sHtmlTemplate = v_sb.toString();
		} catch (Exception v_exException) {
			ExceptionController.getInstance().handleException(v_exException);
		}
	}

	private void generateLabels() {
		int v_iI;
		String v_sLabel;
		String v_sLabelPart;
		StringBuilder v_sb;
		String[] v_arrLabelTokens;
		try {
			for (String v_sTags : this.g_lAllIds) {
				v_sLabel = "";
				v_arrLabelTokens = v_sTags.split("_");
				for (v_iI = 0; v_iI < v_arrLabelTokens.length; v_iI++) {
					v_sLabelPart = v_arrLabelTokens[v_iI];
					v_sb = new StringBuilder(v_sLabelPart);
					v_sb.setCharAt(0, (char)(v_sLabelPart.charAt(0) - 32));
					v_arrLabelTokens[v_iI] = v_sb.toString();
					if (v_iI > 0) {
						v_sLabel += " ";
					}
					v_sLabel += v_arrLabelTokens[v_iI];
				}
				if (this.g_lAllRequiredIds.contains(v_sTags)) {
					v_sLabel += "*";
				}
				this.g_hAllLabels.put(v_sTags, v_sLabel);
			}
		} catch (Exception v_exException) {
			ExceptionController.getInstance().handleException(v_exException);
		}
	}

	private boolean loadXMLTree() {
		boolean v_Return = true;
		DocumentBuilder v_objDocBuilder;
		try {
			v_objDocBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			this.g_xmlCommand = v_objDocBuilder.parse(new InputSource(new StringReader(this.g_sCommand)));
			this.g_objRoot = this.g_xmlCommand.getFirstChild();
		} catch (Exception v_exException) {
			ExceptionController.getInstance().handleException(v_exException);
			v_Return = false;
		}
		return v_Return;
	}

	private void postToGoogleDocsDatabase() {
		this.getController().postCommandToRemoteDB (this.g_sCommand);
	}

	private void setCommand() {
		try {
			this.g_Return.put("_Command_", "\t *" + this.g_sCommand.replaceAll("\\n", "\n\t *"));
		} catch (Exception v_exException) {
			ExceptionController.getInstance().handleException(v_exException);
		}
	}

	private void setFieldIds(Node prm_objElement) {
		int v_iI = 0;
		String  v_sFieldIs;
		Node v_objChild;
		NodeList v_lTopLevelElements;
		try {
			if (null == prm_objElement) {
			} else {
				v_lTopLevelElements = prm_objElement.getChildNodes();
				for (v_iI = 0; v_iI < v_lTopLevelElements.getLength(); v_iI++) {
					v_objChild = v_lTopLevelElements.item(v_iI);
					if (v_objChild.getNodeType() == Node.TEXT_NODE) {
					} else if (v_objChild.getNodeName().indexOf("#") == 0) {
					} else if (1 == v_objChild.getChildNodes().getLength() && v_objChild.getFirstChild().getNodeType() == Node.TEXT_NODE) {
						this.g_lAllIds.add(v_objChild.getNodeName());
					}
					if (v_objChild.getNodeType() == Node.ELEMENT_NODE) {
						this.setFieldIds(v_objChild);
					}
				}
			}
			v_sFieldIs = "";
			for (String v_sField : this.g_lAllIds) {
				if (v_sFieldIs.length() > 0) {
					v_sFieldIs += ", ";
				}
				v_sFieldIs += "'" + v_sField + "'";
			}
			this.g_Return.put("_FieldIDs_", v_sFieldIs);
		} catch (Exception v_exException) {
			ExceptionController.getInstance().handleException(v_exException);
		}
	}

	private void setHtmlTemplate() {
		try {
			this.generateLabels();
			this.generateHtml();
			this.g_Return.put("_HtmlTemplate_", this.g_sHtmlTemplate);
		} catch (Exception v_exException) {
			ExceptionController.getInstance().handleException(v_exException);
		}
	}

	private void setModelType() {
		this.g_Return.put("_ModelType_", this.g_sFileName);
	}

	private void setRequiredFields(Node prm_objElement, int prm_iLevel) {
		int v_iI = 0;
		String v_sFieldIs;
		Node v_objChild;
		NodeList v_lTopLevelElements;
		try {
			/*
			 * Rules:
			 * 1. Make all the Top level tags as required.
			 * 2. Make every tag ending with id as required.
			 */
			if (null == prm_objElement) {
			} else {
				v_lTopLevelElements = prm_objElement.getChildNodes();
				for (v_iI = 0; v_iI < v_lTopLevelElements.getLength(); v_iI++) {
					v_objChild = v_lTopLevelElements.item(v_iI);
					if (0 == prm_iLevel && 1 == v_objChild.getChildNodes().getLength() && v_objChild.getFirstChild().getNodeType() == Node.TEXT_NODE) {
						this.g_lAllRequiredIds.add(v_objChild.getNodeName());
					} else if (v_objChild.getNodeName().toLowerCase().indexOf("id") > 0) {
						this.g_lAllRequiredIds.add(v_objChild.getNodeName());
					}
					this.setRequiredFields(v_objChild, prm_iLevel + 1);
				}
			}
			
			v_sFieldIs = "";
			for (String v_sField : this.g_lAllRequiredIds) {
				if (v_sFieldIs.length() > 0) {
					v_sFieldIs += ", ";
				}
				v_sFieldIs += "'" + v_sField + "'";
			}
			this.g_Return.put("_RequiredFields_", v_sFieldIs);
		} catch (Exception v_exException) {
			ExceptionController.getInstance().handleException(v_exException);
		}
	}

	private void setTemplateFileName() {
		this.g_Return.put("_HTMLTemplateFileName_", this.g_sFileName + "_template");
	}
}
