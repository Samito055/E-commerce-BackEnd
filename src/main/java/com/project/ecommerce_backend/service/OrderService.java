package com.project.ecommerce_backend.service;

import com.project.ecommerce_backend.Models.LocalUser;
import com.project.ecommerce_backend.Models.WebOrder;
import com.project.ecommerce_backend.Models.dao.WebOrderDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private WebOrderDAO webOrderDAO;

    public OrderService(WebOrderDAO webOrderDAO){
        this.webOrderDAO = webOrderDAO;
    }

    public List<WebOrder> getOrders(LocalUser user){
        return webOrderDAO.findByUser(user);
    }
}
