package cn.itcast.erp.exception;

/**
 * ERP自定义异常：对已知不符合业务逻辑操作，终止程序的继续
 *
 */
public class ErpException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ErpException(String message){
		super(message);
	}
}
