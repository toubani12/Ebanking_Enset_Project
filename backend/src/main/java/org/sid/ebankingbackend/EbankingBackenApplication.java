package org.sid.ebankingbackend;

import org.sid.ebankingbackend.dtos.BankAccountDto;
import org.sid.ebankingbackend.dtos.CurrentAccountDto;
import org.sid.ebankingbackend.dtos.CustmerDto;
import org.sid.ebankingbackend.dtos.SavingAccountDto;
import org.sid.ebankingbackend.entities.*;
import org.sid.ebankingbackend.repositories.CustomerRepository;
import org.sid.ebankingbackend.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankingBackenApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankingBackenApplication.class, args);
    }
    @Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService, CustomerRepository customerRepository) {
        return args -> {
            Stream.of("hassan", "yassine", "Aicha").forEach(name -> {
                CustmerDto custmer = new CustmerDto();
                custmer.setName(name);
                custmer.setEmail(name + "@gmail.com");
                bankAccountService.saveCustmer(custmer);
            });
            bankAccountService.listCustmers().forEach(custmer -> {;
                try {
                    bankAccountService.saveCurrentBankAccount(Math.random() * 90000, 9000, custmer.getId());
                    bankAccountService.saveSavingBankAccount(Math.random() * 120000, 5.5, custmer.getId());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            List<BankAccountDto> bankAccounts = bankAccountService.bankAccounts();
            for(BankAccountDto bankAccount:bankAccounts){
                for (int i = 0; i < 20; i++) {
                    String accountId ;
                    if(bankAccount instanceof SavingAccountDto){
                        accountId = ((SavingAccountDto) bankAccount).getId();
                    }else {
                        accountId = ((CurrentAccountDto)bankAccount).getId();
                    }
                    bankAccountService.credit(accountId, Math.random() * 120000, "Credit");
                    bankAccountService.debit(accountId, Math.random() * 10000, "Debit");

                }
            }

        };
    }}