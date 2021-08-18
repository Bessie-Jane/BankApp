package com.maven.bank.bankingservices;

import com.maven.bank.datastore.TransactionType;
import com.maven.bank.entities.Account;
import com.maven.bank.entities.Customer;
import com.maven.bank.datastore.AccountType;
import com.maven.bank.datastore.CustomerRepo;
import com.maven.bank.exceptions.MavenBankException;
import com.maven.bank.exceptions.MavenBankInsufficientFundsException;
import com.maven.bank.exceptions.MavenBankTransactionException;

import java.math.BigDecimal;

public class AccountServiceImpl implements AccountService{


    @Override
    public long openAccount(Customer theCustomer, AccountType type) throws MavenBankException  {
        if (theCustomer == null){
          throw  new MavenBankException("customer not specified");
        }
        if(type == null){
            throw new MavenBankException("account type required to open account");
        }

        if (accountTypeExists(theCustomer, type)){
            throw  new MavenBankException("customer already has requested account type");
        }

        Account newAccount = new Account();
        newAccount.setAccountNumber(BankService.generateAccountNumber());
        newAccount.setTypeOfAccount(type);
        theCustomer.getAccounts().add(newAccount);

        //add to datastore
        CustomerRepo.getCustomers().put(theCustomer.getBvn(), theCustomer);
        return newAccount.getAccountNumber();
    }

    @Override
    public BigDecimal deposit(BigDecimal amount, long accountNumber) throws MavenBankTransactionException ,MavenBankException {
        Account depositAccount = findAccount (accountNumber);

        TransactionType transactionType = TransactionType.DEPOSIT;
        validateTransaction(amount, depositAccount, transactionType);

        BigDecimal newBalance = BigDecimal.ZERO;
        newBalance = depositAccount.getBalance ().add (amount);
        depositAccount.setBalance (newBalance);
        return newBalance;
    }


    @Override
    public Account findAccount(long accountNumber) throws MavenBankException {
        Account foundAccount = null;
        boolean accountFound = false;

            for(Customer customer : CustomerRepo.getCustomers().values()){
                for (Account anAccount : customer.getAccounts()){
                    if(anAccount.getAccountNumber() == accountNumber){
                        foundAccount = anAccount;
                        accountFound = true;
                        break;
                    }
                }
                if(accountFound){
                    break;
                }
            }

        return foundAccount;
    }

    @Override
    public Account findAccount(Customer customer, long accountNumber) throws MavenBankException {
        return null;
    }

    @Override
    public BigDecimal withdraw(BigDecimal amount, long accountNumber) throws MavenBankException {
        //verify withdrawer

        Account theAccount = findAccount (accountNumber);

        TransactionType transactionType = TransactionType.WITHDRAWAL;
        validateTransaction(amount, theAccount, transactionType);

        try{
            checkForSufficientBalance(amount, theAccount);

        }catch (MavenBankInsufficientFundsException insufficientFundsException){
            //apply for overdraft
            this.applyForOverDraft(theAccount);
        }
        BigDecimal newBalance = debitAccount (amount, accountNumber);

        return newBalance;

    }

    @Override
    public void applyForOverDraft(Account theAccount) {
        //TODO
    }

    public BigDecimal debitAccount(BigDecimal amount, long accountNumber) throws MavenBankException {
        Account theAccount = findAccount (accountNumber);
        BigDecimal newBalance =  theAccount.getBalance ().subtract (amount);
        theAccount.setBalance (newBalance);

        return newBalance;

    }

    private boolean accountTypeExists(Customer aCustomer, AccountType type) {
        boolean accountTypeExists = false;
        for(Account customerAccount: aCustomer.getAccounts()){
            if(customerAccount.getTypeOfAccount() == type){
                accountTypeExists = true;
                break;
            }
        }
        return accountTypeExists;
    }

    public void validateTransaction(BigDecimal amount, Account account, TransactionType typeOfTransaction) throws MavenBankTransactionException {
        switch (typeOfTransaction){
            case DEPOSIT -> {

                if (amount.compareTo (BigDecimal.ZERO) < 0){
                    throw new MavenBankTransactionException ("Deposit amount cannot be negative");
                }

                if (account == null){
                    throw new MavenBankTransactionException ( "Withdrawal account not found" );
                }

            }

            case WITHDRAWAL -> {
                if (account == null){
                    throw new MavenBankTransactionException ( "Withdrawal account not found" );
                }
                if (amount.compareTo (BigDecimal.ZERO) < BigDecimal.ONE.intValue ()){
                    throw new MavenBankTransactionException ( "Withdrawal amount cannot be Negative!!" );
                }
                BigDecimal accountBalance = account.getBalance();

                if (amount.compareTo (accountBalance) > 0){
                    throw new MavenBankInsufficientFundsException ( "Insufficient Funds" );
                }
            }

        }
    }

    private void checkForSufficientBalance(BigDecimal amount, Account account) throws  MavenBankTransactionException{
        if(amount.compareTo(account.getBalance()) > 0){
            throw new MavenBankInsufficientFundsException("Insufficient account balance");
        }
    }
}
