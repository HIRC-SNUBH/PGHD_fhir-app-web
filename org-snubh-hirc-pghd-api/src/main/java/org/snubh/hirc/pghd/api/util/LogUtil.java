package org.snubh.hirc.pghd.api.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogUtil {

	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger("framework");

	public void write(Throwable e) {
		LOGGER.error(getStackTraceStr(e));
	}

	public void writeInfo(String msg) {
		LOGGER.info(msg);
	}

	public void writeDebug(String msg) {
		LOGGER.debug(msg);
	}

	public String getStackTraceStr(Throwable throwable) {

		if (throwable == null)
			return "";

		try {
			StringBuilder sb = new StringBuilder();
			List<Throwable> exceptionList = ExceptionUtils.getThrowableList(throwable);

			for (int i = 0; i < exceptionList.size(); i++) {
				sb.append(exceptionList.get(i).toString().replace("\n", "").replace("\r", ""));
				if (i != exceptionList.size() - 1)
					sb.append("\r\n");
			}
			return String.format("%s/r/n%s", sb.toString(), getStackTrace(throwable));
		} catch (Exception ex) {
			return ex.toString();
		}
	}

	public static String getStackTrace(Throwable throwable) {
		if (throwable == null)
			return "";

		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			throwable.printStackTrace(new PrintStream(stream));
			stream.flush();
			String error = new String(stream.toByteArray());
			return error;
		} catch (Exception ex) {
			return ex.toString();
		}
	}
}
