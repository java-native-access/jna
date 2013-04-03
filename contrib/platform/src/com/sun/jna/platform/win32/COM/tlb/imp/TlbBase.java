package com.sun.jna.platform.win32.COM.tlb.imp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.jna.platform.win32.COM.ITypeLibUtil;

public abstract class TlbBase {

	public final static String CR = "\n";

	public final static String CRCR = "\n\n";

	public final static String TAB = "\t";

	public final static String TABTAB = "\t\t";

	protected ITypeLibUtil typeLibUtil;

	protected int index;

	protected StringBuffer templateBuffer;

	protected StringBuffer classBuffer;

	protected String content = "";

	public TlbBase(int index, ITypeLibUtil typeLibUtil) {
		this.index = index;
		this.typeLibUtil = typeLibUtil;

		String filename = this.getClassTemplate();
		try {
			this.readTemplateFile(filename);
			this.classBuffer = templateBuffer;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void logError(String msg) {
		this.log("ERROR", msg);
	}

	public void logInfo(String msg) {
		this.log("INFO", msg);
	}

	public StringBuffer getClassBuffer() {
		return classBuffer;
	}

	public void createContent(String content) {
		this.replaceVariable("content", content);
	}
	
	protected void log(String level, String msg) {
		String _msg = level + " " + this.getTime() + " : " + msg;
		System.out.println(_msg);
	}

	private String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return sdf.format(new Date());
	}

	abstract protected String getClassTemplate();

	protected void readTemplateFile(String filename) throws IOException {
		this.templateBuffer = new StringBuffer();
		BufferedReader reader = null;
		try {
			InputStream is = this.getClass().getClassLoader()
					.getResourceAsStream(filename);
			reader = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while ((line = reader.readLine()) != null)
				this.templateBuffer.append(line + "\n");
		} finally {
			if (reader != null)
				reader.close();
		}
	}

	protected void replaceVariable(String name, String value) {
		if(value == null)
			value = "";
		
		Pattern pattern = Pattern.compile("\\$\\{" + name + "\\}");
		Matcher matcher = pattern.matcher(this.classBuffer);
		String replacement = value;
		String result = "";

		while (matcher.find()) {
			result = matcher.replaceAll(replacement);
		}

		this.classBuffer = new StringBuffer(result);
	}

	protected void createClassName(String name) {
		this.replaceVariable("classname", name);
	}
}
