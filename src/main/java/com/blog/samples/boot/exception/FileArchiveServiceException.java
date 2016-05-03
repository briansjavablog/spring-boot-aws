package com.blog.samples.boot.exception;

public class FileArchiveServiceException extends RuntimeException {

	private static final long serialVersionUID = 2468434988680850339L;	
	
	public FileArchiveServiceException(String msg, Throwable throwable){
		super(msg, throwable);
	}
}