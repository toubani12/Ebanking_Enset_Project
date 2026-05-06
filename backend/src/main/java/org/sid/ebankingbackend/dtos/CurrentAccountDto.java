package org.sid.ebankingbackend.dtos;

import org.sid.ebankingbackend.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public  class CurrentAccountDto extends BankAccountDto {

    private String id ;
    private double balance ;
    private Date createdAt ;
    private AccountStatus status ;
    private CustmerDto custmerDto ;
    private double overDraft ;

}
