package org.sid.ebankingbackend.web;

import org.sid.ebankingbackend.dtos.AccountHistoryDto;
import org.sid.ebankingbackend.dtos.AccountOperationDto;
import org.sid.ebankingbackend.dtos.BankAccountDto;
import org.sid.ebankingbackend.exceptions.BankAccountNotFoundException;
import org.sid.ebankingbackend.repositories.AccountOperationRepository;
import org.sid.ebankingbackend.services.BankAccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")

public class BankAccountController {

    private final AccountOperationRepository accountOperationRepository;
    private BankAccountService bankAccountService ;
    public BankAccountController(BankAccountService bankAccountService, AccountOperationRepository accountOperationRepository){
        this.bankAccountService = bankAccountService;
        this.accountOperationRepository = accountOperationRepository;
    }
    @GetMapping("/accounts/{accountId}")
    public BankAccountDto getBankAccount(@PathVariable String accountId) throws BankAccountNotFoundException {
        return bankAccountService.getBankAccount(accountId);
    }
    @GetMapping("/accounts")
    public List<BankAccountDto> listAccounts(){
        return  bankAccountService.bankAccounts();
    }
    @GetMapping("/accounts/{accountId}/operations")
    public  List<AccountOperationDto> getHistory(@PathVariable  String accountId){
        return bankAccountService.accountHistory(accountId);
    }
    @GetMapping("/accounts/{accountId}/pageOperations")

    public AccountHistoryDto getAccountHistory(@PathVariable String accountId,
                                               @RequestParam(name = "page", defaultValue = "0") int page,
                                               @RequestParam(name = "size", defaultValue = "5") int size) throws BankAccountNotFoundException {
        return bankAccountService.getAccountHistory(accountId,page,size);
    }

    @PostMapping("/accounts/debit")
    public void debit(@RequestParam String accountId, @RequestParam double amount, @RequestParam String description) throws Exception {
        bankAccountService.debit(accountId, amount, description);
    }

    @PostMapping("/accounts/credit")
    public void credit(@RequestParam String accountId, @RequestParam double amount, @RequestParam String description) throws Exception {
        bankAccountService.credit(accountId, amount, description);
    }

    @PostMapping("/accounts/transfer")
    public void transfer(@RequestParam String accountSource, @RequestParam String accountDestination, @RequestParam double amount) throws Exception {
        bankAccountService.transfer(accountSource, accountDestination, amount);
    }
}
