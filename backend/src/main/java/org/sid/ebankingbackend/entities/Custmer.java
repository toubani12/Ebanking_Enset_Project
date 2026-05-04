package org.sid.ebankingbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Custmer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String name ;
    private String email ;
    @OneToMany(mappedBy = "custmer",fetch = FetchType.LAZY)


    private List<BankAccount> bankAccounts ;

}
