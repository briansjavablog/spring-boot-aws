package com.blog.samples.boot.exception;

public class FileArchiveSuerviceException extends RuntimeException {

	private static final long serialVersionUID = 2468434988680850339L;	
	
	public FileArchiveSuerviceException(String msg, Throwable throwable){
		super(msg, throwable);
	}
}