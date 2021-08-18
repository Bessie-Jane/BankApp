package com.maven.bank.exceptions;

public class MavenBankInsufficientFundsException extends MavenBankTransactionException{
    public MavenBankInsufficientFundsException(String message){
        super (message);
    }

    public MavenBankInsufficientFundsException(String message, Throwable ex){
        super (message, ex);
    }

    public MavenBankInsufficientFundsException (Throwable ex){
        super(ex);
    }
}
