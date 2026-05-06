package org.sid.ebankingbackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data @NoArgsConstructor @AllArgsConstructor
public class CustmerDto {

    private Long id;
    private String name ;
    private String email ;


}
