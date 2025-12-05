package com.lumina_bank.userservice.repository;

import com.lumina_bank.userservice.model.BusinessUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessUserRepository extends JpaRepository<BusinessUser,Integer> {
}
