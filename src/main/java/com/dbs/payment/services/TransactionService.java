package com.dbs.payment.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.dbs.payment.controller.TransactionRequest;
import com.dbs.payment.entities.BankBIC;
import com.dbs.payment.entities.Customer;
import com.dbs.payment.entities.MessageCode;
import com.dbs.payment.entities.TransactionHistory;
import com.dbs.payment.repository.BankRepository;
import com.dbs.payment.repository.CustomerRepository;
import com.dbs.payment.repository.MessageCodeRepository;
import com.dbs.payment.repository.TransactionRepository;

@Service
public class TransactionService {
	@Autowired
    CustomerRepository customerRepository;
	@Autowired
    BankRepository bankRepository;
	@Autowired
    MessageCodeRepository messageCodeRepository;
	@Autowired
    TransactionRepository transactionRepository;

    public Customer getCustomerById(String cid) {
    	
    	Optional<Customer> result = customerRepository.findById(cid);
    	if(result != null) return result.get();
    	return null;
    }
    public BankBIC getBankByBIC(String bic) {
        Optional<BankBIC> result = bankRepository.findById(bic);
        if(result!=null) return result.get();
        return null;
    }
	public ResponseEntity<Object> createTransaction(TransactionRequest request) {
		Optional<Customer> custRes = customerRepository.findById(request.getSenderAccountNumber());
		if(custRes.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid Customer ID");
		}
		Customer customer = custRes.get();
		Optional<BankBIC> bankRes = bankRepository.findById(request.getBic());
		if(bankRes.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid BIC ID");
		}
		Optional<MessageCode> mesRes = messageCodeRepository.findById(request.getMessageCode().toUpperCase());
		if (mesRes.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid Message Code");
		}
		if(!request.getTransferType().equalsIgnoreCase("bank") && !request.getTransferType().equalsIgnoreCase("customer")) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid Transfer Type");
		}
		Double transferFee = 0.0025 * request.getAmount();
        Double totalAmount = request.getAmount() + transferFee;
        if(customer.getClearBalance()<totalAmount && !customer.isOverdraft()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Insufficient funds");
        }
        if (customer.getName().toUpperCase().contains("HDFC BANK")) {
        	if(!request.getTransferType().equalsIgnoreCase("bank")) {
        		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bank Can transfer only to bank. Select transfer type as (Bank)");
        	}
        }
    	double bal = customer.getClearBalance();
    	bal = bal<totalAmount ? 0 : bal-totalAmount;
    	customer.setClearBalance(bal);
    	customerRepository.save(customer);
    	TransactionHistory transactionItem = new TransactionHistory(
    			request.getTransferType(),
    			request.getMessageCode(),
    			request.getAmount(),
    			request.getSenderAccountNumber(),
    			request.getBic(),
    			request.getReceiverAccountNumber(),
    			request.getReceiverAccountName(),
    			transferFee);
    	transactionRepository.save(transactionItem);
    	return ResponseEntity.status(HttpStatus.OK).body("Transfer successfull");
	}
	public ResponseEntity<Object> getTransaction(long id) {
		// TODO Auto-generated method stub
		Optional<TransactionHistory> res = transactionRepository.findById(id);
		if(res.isEmpty())
		return null;
		return ResponseEntity.status(HttpStatus.OK).body(res.get());
	}
 


}
