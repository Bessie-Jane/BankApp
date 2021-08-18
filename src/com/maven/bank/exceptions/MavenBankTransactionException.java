package com.maven.bank.exceptions;

public class MavenBankTransactionException extends MavenBankException{
    public MavenBankTransactionException(String message){
        super(message);
    }
    public MavenBankTransactionException(String message, Throwable ex){
        super(message, ex);
    }
    public MavenBankTransactionException(Throwable ex){
        super(ex);
    }
}
