package org.neframework.jpa.exception;

/**
 * 数据库通用操作异常类
 * 
 * @author 冯晓东
 * 
 * @date : Dec 20, 2010 12:15:19 PM
 */
public class ServiceException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ServiceException() {
	}

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}
}
