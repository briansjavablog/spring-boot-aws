package com.blog.samples.boot.rest.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.blog.samples.boot.rest.exception.CustomerNotFoundException;
import com.blog.samples.boot.rest.exception.InvalidCustomerRequestException;
import com.blog.samples.boot.rest.model.Address;
import com.blog.samples.boot.rest.model.Customer;
import com.blog.samples.boot.rest.model.CustomerImage;
import com.blog.samples.boot.rest.repository.CustomerRepository;
import com.blog.samples.boot.service.FileArchiveService;

import lombok.extern.slf4j.Slf4j;

/**
 * Customer Controller exposes a series of RESTful endpoints
 */
@RestController
@Slf4j
public class CustomerController {

	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private FileArchiveService fileArchiveService; 
	 
	
	@RequestMapping(value = "/customers", method = RequestMethod.POST)
    public @ResponseBody Customer createCustomer(            
            @RequestParam(value="firstName", required=true) String firstName,
            @RequestParam(value="lastName", required=true) String lastName,
            @RequestParam(value="dateOfBirth", required=true) @DateTimeFormat(pattern="yyyy-MM-dd") Date dateOfBirth,
            @RequestParam(value="street", required=true) String street,
            @RequestParam(value="town", required=true) String town,
            @RequestParam(value="county", required=true) String county,
            @RequestParam(value="postcode", required=true) String postcode,
            @RequestParam(value="image", required=true) MultipartFile image) {
        
        try {
        	
        	CustomerImage customerImage = fileArchiveService.saveFileToS3(image);        	
        	Customer customer = new Customer(firstName, lastName, dateOfBirth, customerImage, 
        										new Address(street, town, county, postcode));
        	
        	customerRepository.save(customer);
            return customer;
            
        } catch (Exception ex) {
            log.error("Error occurred creating customer", ex);
            throw new RuntimeException(ex);
        }      
    }
	
	
	/**
	 * Get customer using id. Returns HTTP 404 if customer not found
	 * 
	 * @param customerId
	 * @return retrieved customer
	 */
	@RequestMapping(value = "/customers/{customerId}", method = RequestMethod.GET)
	public Customer getCustomer(@PathVariable("customerId") Long customerId) {
		
		/* validate customer Id parameter */
		if (null==customerId) {
			throw new InvalidCustomerRequestException();
		}
		
		Customer customer = customerRepository.findOne(customerId);
		
		if(null==customer){
			throw new CustomerNotFoundException();
		}
		
		return customer;
	}

	
	/**
	 * Gets all customers.
	 *
	 * @return the customers
	 */
	@RequestMapping(value = "/customers", method = RequestMethod.GET)
	public List<Customer> getCustomers() {
		
		return (List<Customer>) customerRepository.findAll();
	}
	
	
	/**
	 * Deletes the customer with given customer id if it exists and returns HTTP204.
	 *
	 * @param customerId the customer id
	 */
	@RequestMapping(value = "/customers/{customerId}", method = RequestMethod.DELETE)
	public void removeCustomer(@PathVariable("customerId") Long customerId, HttpServletResponse httpResponse) {

		if(customerRepository.exists(customerId)){
			Customer customer = customerRepository.findOne(customerId);
			fileArchiveService.deleteImageFromS3(customer.getCustomerImage());
			customerRepository.delete(customer);	
		}
		
		httpResponse.setStatus(HttpStatus.NO_CONTENT.value());
	}

}