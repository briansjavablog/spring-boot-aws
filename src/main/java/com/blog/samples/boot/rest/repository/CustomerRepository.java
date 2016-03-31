package com.blog.samples.boot.rest.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.blog.samples.boot.rest.model.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

    public List<Customer> findByFirstName(String firstName); 
}