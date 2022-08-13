package com.dbs.payment.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

//
import com.dbs.payment.entities.BankBIC;
import com.dbs.payment.entities.Customer;
import com.dbs.payment.services.TransactionService;
@RestController
@RequestMapping("/api")
public class Controller {

	@Autowired
	TransactionService transactionService;
	@GetMapping("/")
	@ResponseBody
	public String getHome() {
    	System.out.println("Fetching customer....");
		return "sai chandra";
	}

    @GetMapping("customer/{cid}")
    public Customer getCustomerById(@PathVariable String cid) {
    	System.out.println("Fetching customer....");
        Customer customer = transactionService.getCustomerById(cid);
        System.out.println(customer);
        return customer;
    }
    @GetMapping("bank/{bic}")
    public BankBIC getBankByBIC(@PathVariable String bic) {
        BankBIC bank = transactionService.getBankByBIC(bic);
        return bank;
    }

    @PostMapping("transaction")
    public ResponseEntity<Object> makeTransaction(@RequestBody TransactionRequest request) {
    	 System.out.println(request.toString());
        return transactionService.createTransaction(request);
    }
    @GetMapping("getTrans/{id}")
    public ResponseEntity<Object> getTrans(@PathVariable long id) {
//   	 System.out.println(request.toString());
       return transactionService.getTransaction(id);
   }

}
