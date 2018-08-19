package com.fnt.sys;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppAuthClientServletContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {

		try {
			Properties props = new Properties();
			InputStream is = servletContextEvent.getServletContext().getResourceAsStream("WEB-INF/authclientsettings.properties");
			props.load(is);

			ServletContext ctx = servletContextEvent.getServletContext();
			props.forEach((key, value) -> {
				String keyStr = String.valueOf(key);
				String valStr = String.valueOf(value);
				ctx.setAttribute(keyStr, valStr);
			});

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {

	}

}
