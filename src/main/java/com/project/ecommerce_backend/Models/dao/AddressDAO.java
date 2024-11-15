package com.project.ecommerce_backend.Models.dao;

import com.project.ecommerce_backend.Models.Address;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface AddressDAO extends ListCrudRepository <Address, Long> {


    List<Address> findByUser_Id(Long id);
}
