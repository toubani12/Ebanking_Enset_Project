package org.sid.ebankingbackend.entities;

import org.sid.ebankingbackend.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
@Entity
@Data @NoArgsConstructor @AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type" , length = 4)
public abstract class BankAccount {
    @Id
    private String id ;
    private double balance ;
    private Date createdAt ;
    @Enumerated(EnumType.STRING)
    private AccountStatus status ;
    @ManyToOne
    private Custmer custmer ;
    @OneToMany(mappedBy = "bankAccount" , fetch = FetchType.LAZY)
    private List<AccountOperation> accountOperations ;


}
