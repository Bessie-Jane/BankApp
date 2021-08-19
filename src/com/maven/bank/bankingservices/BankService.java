package com.maven.bank.bankingservices;

import java.time.Year;

public class BankService {
    private static long currentBVN = 2;
    private static long currentAccountNumber = 3;
    private static Year accountOpeningYear = Year.now().minusYears(7);

    public static long generateBVN(){
        currentBVN++;
        return currentBVN;
    }

    public static void tearDownMethod(){
     currentBVN = 2;
     currentAccountNumber= 3;
    }

    public static long generateAccountNumber() {
        ++currentAccountNumber;

        return currentAccountNumber;
    }

    public static long getCurrentBVN() {
        return currentBVN;
    }

    private static void setCurrentBVN(long currentBVN) {
        BankService.currentBVN = currentBVN;
    }

    public static long getCurrentAccountNumber() {
        return currentAccountNumber;
    }

    private static void setCurrentAccountNumber(long currentAccountNumber) {
        BankService.currentAccountNumber = currentAccountNumber;
    }

    public static Year getAccountOpeningYear() {
        return accountOpeningYear;
    }

    public static void setAccountOpeningYear(Year accountOpeningYear) {
        BankService.accountOpeningYear = accountOpeningYear;
    }
}
