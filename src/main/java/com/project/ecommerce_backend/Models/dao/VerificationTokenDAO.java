package com.project.ecommerce_backend.Models.dao;

import com.project.ecommerce_backend.Models.LocalUser;
import com.project.ecommerce_backend.Models.VerificationToken;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface VerificationTokenDAO extends ListCrudRepository <VerificationToken, Long> {


    Optional<VerificationToken> findByToken(String token);

    void deleteByUser(LocalUser user);

    List<VerificationToken> findByUser_IdOrderByIdDesc(Long id);

}
