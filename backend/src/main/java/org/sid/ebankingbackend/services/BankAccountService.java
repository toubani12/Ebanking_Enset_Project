package org.sid.ebankingbackend.services;

import org.sid.ebankingbackend.dtos.*;
import org.sid.ebankingbackend.exceptions.BalanceNotSufisantException;
import org.sid.ebankingbackend.exceptions.BankAccountNotFoundException;
import org.sid.ebankingbackend.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {
    CustmerDto saveCustmer(CustmerDto custmer);
    CurrentAccountDto saveCurrentBankAccount(double initialBalance, double overDrafte, Long id) throws CustomerNotFoundException;
    SavingAccountDto saveSavingBankAccount(double initialBalance, double InterestRate, Long id) throws CustomerNotFoundException;
    List<CustmerDto> listCustmers();
    BankAccountDto getBankAccount(String accountId) throws BankAccountNotFoundException;
    void debit (String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufisantException;
    void credit(String accountId,double amount, String description) throws BankAccountNotFoundException;
    void transfer(String accountIdSource ,String accountIdDestination , double amount) throws BankAccountNotFoundException, BalanceNotSufisantException;

    List<BankAccountDto> bankAccounts();

    CustmerDto getCustmer(Long id) throws CustomerNotFoundException;

    CustmerDto updateCustmer(CustmerDto custmerdto);

    void deleteCustmer(Long id) ;

    List<AccountOperationDto> accountHistory(String accountId);

    AccountHistoryDto getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException;

    List<CustmerDto> searchCustmers(String keyword);
//    List<BankAccount> bankAccounts();


}



