package org.pausanchez.repositories;

import org.pausanchez.entities.Customer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class CustomerRepository {

    @Inject
    private EntityManager entityManager;

    @Transactional
    public void addCustomer(Customer customer){
        entityManager.persist(customer);
    }

    @Transactional
    public void deleteCustomer(Customer customer){
        entityManager.remove(customer);
    }

    @Transactional
    public List<Customer> getCustomers(){
        return entityManager.createQuery("select c from Customer c").getResultList();
    }

    @Transactional
    public void updateCustomer(Customer customer){
        entityManager.merge(customer);
    }
}
