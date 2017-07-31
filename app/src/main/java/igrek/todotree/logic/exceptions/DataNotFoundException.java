package igrek.todotree.logic.exceptions;

public class DataNotFoundException extends Exception {
	public DataNotFoundException() {
		super();
	}
	
	public DataNotFoundException(String detailMessage) {
		super(detailMessage);
	}
}
