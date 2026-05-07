package org.sid.ebankingbackend.web;

import org.sid.ebankingbackend.dtos.CustmerDto;
import org.sid.ebankingbackend.exceptions.CustomerNotFoundException;
import org.sid.ebankingbackend.services.BankAccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/custmers")
@AllArgsConstructor
@Slf4j
@CrossOrigin("*")
public class CustmerController {
    private BankAccountService bankAccountService;
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') ")
    public List<CustmerDto> custmers(){
        return bankAccountService.listCustmers();
    }
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') ")
    public List<CustmerDto> searhCustmers(@RequestParam(name = "keyword" , defaultValue = "") String keyword ){
        return bankAccountService.searchCustmers(keyword);
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') ")
    public CustmerDto getcusCustmer(@PathVariable(name = "id") Long id) throws CustomerNotFoundException {
        return  bankAccountService.getCustmer(id);
    }
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') ")
    @PostMapping
    public CustmerDto saveCustomer(@RequestBody  CustmerDto custmerDto){
        bankAccountService.saveCustmer(custmerDto);
        return custmerDto;
    }
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') ")

    @PutMapping("/{id}")
    public CustmerDto updateCustmer(@PathVariable(name = "id") Long id ,@RequestBody CustmerDto custmerDto){
        custmerDto.setId(id);
        return bankAccountService.updateCustmer(custmerDto);

    }
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') ")

    @DeleteMapping("/{id}")
    public void deleteCustmer(@PathVariable(name = "id") Long id) {
        bankAccountService.deleteCustmer(id);
    }

}
