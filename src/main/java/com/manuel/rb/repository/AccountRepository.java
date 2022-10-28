package com.manuel.rb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manuel.rb.models.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
	Account findByIban(String iban);
}