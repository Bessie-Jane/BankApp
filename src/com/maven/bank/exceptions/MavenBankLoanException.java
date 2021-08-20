package com.maven.bank.exceptions;

public class MavenBankLoanException extends MavenBankException{

    public MavenBankLoanException(String message){
        super(message);
    }
    public MavenBankLoanException(String message, Throwable ex){
        super(message, ex);
    }

    public MavenBankLoanException(Throwable ex){
        super(ex);
    }

}
