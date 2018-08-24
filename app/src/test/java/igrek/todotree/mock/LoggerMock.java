package igrek.todotree.mock;


import android.app.Activity;

import igrek.todotree.logger.LogLevel;
import igrek.todotree.logger.Logger;

public class LoggerMock extends Logger {
	
	@Override
	public void fatal(Activity activity, String e) {
		log(e, LogLevel.FATAL, "[FATAL ERROR] ", 4);
	}
	
	@Override
	protected void printInfo(String msg) {
		System.out.println(msg);
	}
	
	@Override
	protected void printError(String msg) {
		System.err.println(msg);
	}
	
}