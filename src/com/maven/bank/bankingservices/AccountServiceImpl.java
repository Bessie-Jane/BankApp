package com.maven.bank.bankingservices;

import com.maven.bank.datastore.LoanRequestStatus;
import com.maven.bank.datastore.BankTransactionType;
import com.maven.bank.entities.*;
import com.maven.bank.datastore.AccountType;
import com.maven.bank.datastore.CustomerRepo;
import com.maven.bank.exceptions.MavenBankException;
import com.maven.bank.exceptions.MavenBankInsufficientFundsException;
import com.maven.bank.exceptions.MavenBankTransactionException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AccountServiceImpl implements AccountService{


    @Override
    public long openAccount(Customer theCustomer, AccountType type) throws MavenBankException  {
        long accountNumber = BigDecimal.ZERO.longValue();

        if (type == null){
            throw  new MavenBankException("account type required to open account");
        }
        if(type == AccountType.SAVINGSACCOUNT){
            accountNumber = openSavingsAccount(theCustomer);
        }else if(type == AccountType.CURRENTACCOUNT){
            accountNumber = openCurrentAccount(theCustomer);
        }
        return  accountNumber;
    }

    @Override
    public long openSavingsAccount(Customer theCustomer) throws MavenBankException {

        if (theCustomer == null){
            throw  new MavenBankException("Customer and account type required to open account");
        }
        SavingsAccount newAccount = new SavingsAccount();
        if (accountTypeExists(theCustomer, newAccount.getClass().getTypeName())){
            throw new MavenBankException("Customer already has requested account type");
        }
        newAccount.setAccountNumber(BankService.generateAccountNumber());
        theCustomer.getAccounts().add(newAccount);

        //add to datastore
        CustomerRepo.getCustomers().put(theCustomer.getBvn(), theCustomer);
        return newAccount.getAccountNumber();
    }

    @Override
    public long openCurrentAccount(Customer theCustomer) throws MavenBankException {

        if (theCustomer == null){
            throw  new MavenBankException("customer and account type required to open account");
        }

        CurrentAccount newAccount = new CurrentAccount();
        if (accountTypeExists(theCustomer, newAccount.getClass().getTypeName())){
            throw  new MavenBankException("customer already has requested account type");
        }

        newAccount.setAccountNumber(BankService.generateAccountNumber());
        theCustomer.getAccounts().add(newAccount);

        //add to datastore
        CustomerRepo.getCustomers().put(theCustomer.getBvn(), theCustomer);
        return newAccount.getAccountNumber();
    }

    @Override
    public BigDecimal deposit(BigDecimal amount, long accountNumber) throws MavenBankTransactionException ,MavenBankException {
        Account depositAccount = findAccount (accountNumber);

        BankTransactionType transactionType = BankTransactionType.DEPOSIT;
        validateTransaction(amount, depositAccount);

        BigDecimal newBalance = BigDecimal.ZERO;
        newBalance = depositAccount.getBalance ().add (amount);
        depositAccount.setBalance (newBalance);
        return newBalance;
    }

    @Override
    public void addBankTransaction(BankTransaction transaction, Account account) throws MavenBankTransactionException, MavenBankException{
        if(transaction == null || account == null){
            throw new MavenBankTransactionException("Transaction and Account are required to add ");
        }

        if(transaction.getType() == BankTransactionType.DEPOSIT){
            deposit(transaction.getAmount(), account.getAccountNumber());
        }else if(transaction.getType() == BankTransactionType.WITHDRAWAL){
            withdraw(transaction.getAmount(), account.getAccountNumber());
        }
        account.getTransactions().add(transaction);
    }

    @Override
    public LocalDateTime openingYear(Account theAccount, LocalDate year) {
        CurrentAccount applyingForLoan = new CurrentAccount();
        return null;
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
//            if (foundAccount == null){
//                throw new MavenBankException("Account Not Found");
//            }

        return foundAccount;
    }

    @Override
    public Account findAccount(Customer customer, long accountNumber) throws MavenBankException {
        return null;
    }

    @Override
    public BigDecimal withdraw(BigDecimal amount, long accountNumber) throws MavenBankTransactionException, MavenBankInsufficientFundsException , MavenBankException{
        //verify withdrawer

        Account theAccount = findAccount (accountNumber);

//      BankTransactionType transactionType = BankTransactionType.WITHDRAWAL;
        validateTransaction(amount, theAccount);

        try{
            checkForSufficientBalance(amount, theAccount);

        }catch (MavenBankInsufficientFundsException insufficientFundsException){
            //apply for overdraft
            this.applyForOverDraft(theAccount);
            throw insufficientFundsException;
        }

        BigDecimal newBalance = debitAccount (amount, accountNumber);
        return newBalance;
    }

    @Override
    public void applyForOverDraft(Account theAccount) {
        //TODO
    }

    @Override
    public LoanRequestStatus applyForLoan(Account theAccount) {
        return null;
    }

    public BigDecimal debitAccount(BigDecimal amount, long accountNumber) throws MavenBankException {
        Account theAccount = findAccount (accountNumber);
        BigDecimal newBalance =  theAccount.getBalance ().subtract (amount);
        theAccount.setBalance (newBalance);

        return newBalance;

    }

    private boolean accountTypeExists(Customer aCustomer, String typeName) {
        boolean accountTypeExists = false;
        for(Account customerAccount: aCustomer.getAccounts()){
            if(customerAccount.getClass().getTypeName() == typeName){
                accountTypeExists = true;
                break;
            }
        }
        return accountTypeExists;
    }

    public void validateTransaction(BigDecimal amount, Account account) throws MavenBankTransactionException {

         if (account == null){
                throw new MavenBankTransactionException ("Withdrawal account not found");
         }
         if (amount.compareTo (BigDecimal.ZERO) < 0){
            throw new MavenBankTransactionException ("Deposit amount cannot be negative");
         }
         if (amount.compareTo (BigDecimal.ZERO) < BigDecimal.ONE.intValue ()){
            throw new MavenBankTransactionException ( "Withdrawal amount cannot be Negative!!" );
         }
    }


    private void checkForSufficientBalance(BigDecimal amount, Account account) throws MavenBankInsufficientFundsException{
        if(amount.compareTo(account.getBalance()) < 0){
            throw new MavenBankInsufficientFundsException("Insufficient account balance");
        }
    }
}
