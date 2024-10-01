package com.project.ecommerce_backend.Exceptions;

public class UserNoVerifiedException extends Exception{

    private boolean NewEmailSent;

    public UserNoVerifiedException(boolean newEmailSent){
        this.NewEmailSent = newEmailSent;
    }

    public boolean isNewEmailSent() {
        return NewEmailSent;
    }
}
