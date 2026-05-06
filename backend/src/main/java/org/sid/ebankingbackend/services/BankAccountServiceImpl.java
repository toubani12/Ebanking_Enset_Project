package org.sid.ebankingbackend.services;

import org.sid.ebankingbackend.dtos.*;
import org.sid.ebankingbackend.entities.*;
import org.sid.ebankingbackend.enums.OperationType;
import org.sid.ebankingbackend.exceptions.BalanceNotSufisantException;
import org.sid.ebankingbackend.exceptions.BankAccountNotFoundException;
import org.sid.ebankingbackend.exceptions.CustomerNotFoundException;
import org.sid.ebankingbackend.mappers.BankAccountMapperImpl;
import org.sid.ebankingbackend.repositories.AccountOperationRepository;
import org.sid.ebankingbackend.repositories.BankAccountRepository;
import org.sid.ebankingbackend.repositories.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Transactional
@Slf4j
public class BankAccountServiceImpl implements  BankAccountService{
    private BankAccountRepository bankAccountRepository;
    private CustomerRepository customerRepository ;
    private AccountOperationRepository accountOperationRepository ;
    private BankAccountMapperImpl dtoMapper ;
//    Logger log = LoggerFactory.getLogger(this.getClass().getName());
    @Override
    public CustmerDto saveCustmer(CustmerDto custmerdto) {
        log.info("Saving custmer");

        Custmer custmer = dtoMapper.fromcustomerDto(custmerdto);
        Custmer savedCustmer = customerRepository.save(custmer);

        return dtoMapper.fromcustomer(savedCustmer);
    }

    @Override
    public CurrentAccountDto saveCurrentBankAccount(double initialBalance, double overDrafte, Long id) throws CustomerNotFoundException {
        CurrentAccount bankAccount  = new CurrentAccount();
        Custmer custmer = customerRepository.findById(id).orElse(null);
        if(custmer == null){
            throw  new CustomerNotFoundException("Customer not found");
        }
        bankAccount.setId(UUID.randomUUID().toString());
        bankAccount.setCreatedAt(new Date());
        bankAccount.setBalance(initialBalance);
        bankAccount.setOverDraft(overDrafte);
        bankAccount.setCustmer(custmer);
        CurrentAccount savedCurentaccount  = bankAccountRepository.save(bankAccount);
        return dtoMapper.fromCurrentAccount(savedCurentaccount);

    }

    @Override
    public SavingAccountDto saveSavingBankAccount(double initialBalance, double InterestRate, Long id) throws CustomerNotFoundException {
        SavingAccount bankAccount  = new SavingAccount();
        Custmer custmer = customerRepository.findById(id).orElse(null);
        if(custmer == null){
            throw  new CustomerNotFoundException("Customer not found");
        }
        bankAccount.setId(UUID.randomUUID().toString());
        bankAccount.setCreatedAt(new Date());
        bankAccount.setBalance(initialBalance);
        bankAccount.setIntrestRAte(InterestRate);
        bankAccount.setCustmer(custmer);
        SavingAccount savedSavingAccount  = bankAccountRepository.save(bankAccount);
        return dtoMapper.fromSavingAccount(savedSavingAccount);

    }


    @Override
    public List<CustmerDto> listCustmers() {
        List<Custmer> custmers = customerRepository.findAll();
        List<CustmerDto> custmerDtos = custmers.stream().map(custmer -> dtoMapper.fromcustomer(custmer)).collect(Collectors.toList());
        return  custmerDtos;
    }

    @Override
    public BankAccountDto getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(()-> new BankAccountNotFoundException("bank account not found"));
        if(bankAccount instanceof CurrentAccount){
            return dtoMapper.fromCurrentAccount((CurrentAccount) bankAccount);
        }else {
            return dtoMapper.fromSavingAccount((SavingAccount) bankAccount);
        }


    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException , BalanceNotSufisantException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElse(null);
        if(bankAccount == null){
            throw new BankAccountNotFoundException("Bank account not found");
        }
        if(bankAccount.getBalance()<amount){
            throw new BalanceNotSufisantException("Balance not sufficient");
        }
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setOperationDate(new Date());
        accountOperation.setAmount(amount);
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setBankAccount(bankAccount);
        accountOperation.setDescription(description);
        accountOperationRepository.save(accountOperation);
//        bankAccount.getAccountOperations().add
//                (accountOperationRepository.save(new AccountOperation(null,new Date(),amount, OperationType.DEBIT,bankAccount,description)));
        bankAccount.setBalance(bankAccount.getBalance()-amount);
        bankAccountRepository.save(bankAccount);




    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException  {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElse(null);
        if(bankAccount == null){
            throw new BankAccountNotFoundException("Bank account not found");
        }

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setOperationDate(new Date());
        accountOperation.setAmount(amount);
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setBankAccount(bankAccount);
        accountOperation.setDescription(description);
        accountOperationRepository.save(accountOperation);

//        bankAccount.getAccountOperations().add
//                (accountOperationRepository.save(new AccountOperation(null,new Date(),amount, OperationType.DEBIT,bankAccount,description)));
        bankAccount.setBalance(bankAccount.getBalance()+amount);

        bankAccountRepository.save(bankAccount);




    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws  BankAccountNotFoundException,BalanceNotSufisantException{
        debit(accountIdSource,amount,"Transfer to "+accountIdDestination);
        credit(accountIdDestination,amount,"Transfer from "+accountIdSource);

    }
    @Override
    public List<BankAccountDto> bankAccounts() {
        List<BankAccount> bankAccounts =   bankAccountRepository.findAll();
        List<BankAccountDto> bankAccountDtos = bankAccounts.stream().map(bankAccount -> {
            if(bankAccount instanceof  SavingAccount){
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return dtoMapper.fromSavingAccount(savingAccount);
            }else{
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return dtoMapper.fromCurrentAccount(currentAccount);
            }

        }).collect(Collectors.toList());
        return bankAccountDtos;
    }
    @Override
    public CustmerDto getCustmer(Long id) throws CustomerNotFoundException {
        Custmer custmer = customerRepository.findById(id).orElseThrow(()->new CustomerNotFoundException("Customer not found"));
        return  dtoMapper.fromcustomer(custmer);
    }
    @Override
    public CustmerDto updateCustmer(CustmerDto custmerdto) {


        Custmer custmer = dtoMapper.fromcustomerDto(custmerdto);
        Custmer savedCustmer = customerRepository.save(custmer);

        return dtoMapper.fromcustomer(savedCustmer);
    }
    @Override
    public void deleteCustmer(Long id) {
        customerRepository.deleteById(id);
    }
    @Override
    public List<AccountOperationDto> accountHistory(String accountId){
        List<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountId);
        return  accountOperations.stream().map(op->dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
    }

    @Override
    public AccountHistoryDto getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(()-> new BankAccountNotFoundException("bank account not found"));
        Page<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountId, PageRequest.of(page, size));
        AccountHistoryDto accountHistoryDto = new AccountHistoryDto();
        List<AccountOperationDto> accountOperationDtos =accountOperations.getContent().stream().map(op->dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
        accountHistoryDto.setAccountOperationDtos(accountOperationDtos);
        accountHistoryDto.setAccountId(bankAccount.getId());
        accountHistoryDto.setBalance(bankAccount.getBalance());
        accountHistoryDto.setPageSize(size);
        accountHistoryDto.setCurrentPage(page);
        accountHistoryDto.setTotalPages(accountOperations.getTotalPages());
        return accountHistoryDto;
    }

    @Override
    public List<CustmerDto> searchCustmers(String keyword) {
        List<Custmer> custmers = customerRepository.findByNameContains(keyword);
        List<CustmerDto> custmerDtos = custmers.stream().map(custmer -> dtoMapper.fromcustomer(custmer)).collect(Collectors.toList());
        return  custmerDtos;
    }
}

