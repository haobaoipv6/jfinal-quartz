package com.jfinal.plugin;

import com.jfinal.plugin.quartz.IQuartzPlugin;
import com.jfinal.plugin.quartz.impl.QuartzPluginImpl;

public class QuartzPluginTest {

	public static void main(String[] args) {
		IQuartzPlugin quartzPlugin = new QuartzPluginImpl();
		quartzPlugin.start();
	}
}
