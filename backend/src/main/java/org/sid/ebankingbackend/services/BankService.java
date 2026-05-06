package org.sid.ebankingbackend.services;

import org.sid.ebankingbackend.entities.BankAccount;
import org.sid.ebankingbackend.entities.CurrentAccount;
import org.sid.ebankingbackend.entities.SavingAccount;
import org.sid.ebankingbackend.repositories.BankAccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class BankService {
    @Autowired
    private BankAccountRepository bankAccountRepository ;
    public void consulter(){
        BankAccount bankAccount1 = bankAccountRepository.findById("05b18270-cd04-4296-9437-91444afb5319").orElse(null);
        if(bankAccount1!= null){
            System.out.println("***********************");
            System.out.println(bankAccount1.getId());
            System.out.println(bankAccount1.getBalance());
            System.out.println(bankAccount1.getStatus());
            System.out.println(bankAccount1.getCreatedAt());
            System.out.println(bankAccount1.getCustmer().getName());
            if(bankAccount1 instanceof CurrentAccount){
                System.out.println("OverDraft : "+((CurrentAccount) bankAccount1).getOverDraft());
            }else if(bankAccount1 instanceof SavingAccount){
                System.out.println("IntrestRate : "+((SavingAccount) bankAccount1).getIntrestRAte());
            }
            bankAccount1.getAccountOperations().forEach(op->{;
                System.out.println("***********************");
                System.out.println(op.getType());
                System.out.println(op.getAmount());
                System.out.println(op.getOperationDate());
            });
        }
    }
}
