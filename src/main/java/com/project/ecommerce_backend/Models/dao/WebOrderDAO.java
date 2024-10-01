package com.project.ecommerce_backend.Models.dao;

import com.project.ecommerce_backend.Models.LocalUser;
import com.project.ecommerce_backend.Models.WebOrder;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface WebOrderDAO extends ListCrudRepository<WebOrder, Long> {

    List<WebOrder> findByUser(LocalUser user);

}
