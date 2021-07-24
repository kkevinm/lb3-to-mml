package com.github.kevinm.lb3tomml.spc;

public class SpcException extends Exception {
    
    private static final long serialVersionUID = -5753145617555067215L;

    public SpcException() {
        super();
    }

    public SpcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public SpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpcException(String message) {
        super(message);
    }

    public SpcException(Throwable cause) {
        super(cause);
    }
    
}
