package com.mariem.Springboot_hibernate_shoppingCart.dao;

import com.mariem.Springboot_hibernate_shoppingCart.entity.Account;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class AccountDao {

    @Autowired
    private SessionFactory sessionFactory ;


    public Account findAccount(String userName) {
        Session session= this.sessionFactory.getCurrentSession();
        return session.find(Account.class,userName) ;
    }

}
