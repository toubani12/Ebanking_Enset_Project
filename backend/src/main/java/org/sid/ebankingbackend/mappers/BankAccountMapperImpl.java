package org.sid.ebankingbackend.mappers;

import org.sid.ebankingbackend.dtos.AccountOperationDto;
import org.sid.ebankingbackend.dtos.CurrentAccountDto;
import org.sid.ebankingbackend.dtos.CustmerDto;
import org.sid.ebankingbackend.dtos.SavingAccountDto;
import org.sid.ebankingbackend.entities.AccountOperation;
import org.sid.ebankingbackend.entities.CurrentAccount;
import org.sid.ebankingbackend.entities.Custmer;
import org.sid.ebankingbackend.entities.SavingAccount;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class BankAccountMapperImpl {
    public CustmerDto fromcustomer(Custmer custmer){
        CustmerDto  custmerDto = new CustmerDto();
        BeanUtils.copyProperties(custmer,custmerDto);
        return  custmerDto;

    }
    public Custmer fromcustomerDto(CustmerDto custmerDto){
        Custmer custmer = new Custmer();
        BeanUtils.copyProperties(custmerDto,custmer);
        return  custmer;
    }

    public CurrentAccountDto fromCurrentAccount(CurrentAccount currentAccount){
        CurrentAccountDto currentAccountDto = new CurrentAccountDto();
        BeanUtils.copyProperties(currentAccount,currentAccountDto);
        currentAccountDto.setCustmerDto(fromcustomer(currentAccount.getCustmer()));
        currentAccountDto.setType(currentAccount.getClass().getSimpleName());
        return currentAccountDto;

    }
    public  CurrentAccount fromCurrentAccountDto(CurrentAccountDto currentAccountDto){
        CurrentAccount currentAccount = new CurrentAccount();
        BeanUtils.copyProperties(currentAccountDto,currentAccount);
        currentAccount.setCustmer(fromcustomerDto(currentAccountDto.getCustmerDto()));
        return  currentAccount;
    }
 public SavingAccountDto fromSavingAccount(SavingAccount savingAccount){
        SavingAccountDto savingAccountDto = new SavingAccountDto();
        BeanUtils.copyProperties(savingAccount,savingAccountDto);
        savingAccountDto.setCustmerDto(fromcustomer(savingAccount.getCustmer()));
        savingAccountDto.setType(savingAccount.getClass().getSimpleName());
        return  savingAccountDto;
 }
 public SavingAccount fromSavingAccountDto(SavingAccountDto savingAccountDto){
        SavingAccount savingAccount = new SavingAccount();
        BeanUtils.copyProperties(savingAccountDto,savingAccount);
        savingAccount.setCustmer(fromcustomerDto(savingAccountDto.getCustmerDto()));
        return  savingAccount;
 }
 public AccountOperationDto fromAccountOperation(AccountOperation accountOperation){
        AccountOperationDto accountOperationDto = new AccountOperationDto();
        BeanUtils.copyProperties(accountOperation,accountOperationDto);
        return accountOperationDto;
 }
}
