package br.feevale.bytechat.client.console;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class ColorFullPrintStream extends PrintStream {

	private static final String NORMAL = "\033[0m";
	
	private Map<String, String> colorTags = new HashMap<String, String>();
	
	public ColorFullPrintStream(OutputStream out) {
		super(out);
		defaultColors();
	}

	protected void defaultColors() {
		colorTags.put("error", "\033[31m");
		colorTags.put("warn", "\033[33m");
		colorTags.put("info", "\033[36m");
		colorTags.put("success", "\033[32m");
		colorTags.put("question", "\033[35m");
		colorTags.put("notice", "\033[1;36m");
	}
	
	public void setColor(String colorTag, String ansiEscapeString) {
		this.colorTags.put(colorTag, ansiEscapeString);
	}
	
	@Override
	public void print(String s) {
		super.print(replaceClors(s));
	}
	
	@Override
	public void println(String s) {
		super.println(replaceClors(s));
	}
	
	protected String replaceClors(String s) {
		for (String tag : colorTags.keySet()) {
			s = s.replaceAll("<" + tag + ">", colorTags.get(tag));
			s = s.replaceAll("</" + tag + ">", NORMAL);
		}
		
		return s;
	}
}
