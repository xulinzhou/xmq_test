package com.xmq.util;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractFileConfilg implements Config {

	public String getString(String name) {
		return getPropertyWithCheck(name);
	}

	public int getInt(String name) {
		String pro =  getPropertyWithCheck(name);
		return Integer.parseInt(pro);
	}
	private String getPropertyWithCheck(String name) {
		String property = getProperty(name);
		if (StringUtils.isBlank(property)) {
			throw new RuntimeException("配置项获取失败，服务启动终止");
		}
		return property;
	}

	protected abstract String getProperty(String name);

}
