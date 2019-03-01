package com.xmq.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FileConfig extends AbstractFileConfilg {

	private  String file;

	private  Properties properties = new Properties();

	public FileConfig(String file) {
		this.file = file;
		load();
	}

	public void load() {
		try {
			InputStream in = this.getClass().getResourceAsStream(file);
			properties.load(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("读取配置文件失败，服务启动终止");
		}
	}

	@Override
	protected String getProperty(String name) {
		return properties.getProperty(name);
	}


}
