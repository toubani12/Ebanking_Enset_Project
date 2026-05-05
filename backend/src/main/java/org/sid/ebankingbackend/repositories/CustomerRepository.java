package org.sid.ebankingbackend.repositories;

import org.sid.ebankingbackend.entities.Custmer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Custmer  , Long> {
    List<Custmer> findByNameContains(String name);

}
