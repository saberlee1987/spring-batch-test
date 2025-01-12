package com.saber.springbatchtest.processor;

import com.saber.springbatchtest.dto.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class CustomerProcessor implements ItemProcessor<Customer, Customer> {
    @Override
    public Customer process(Customer customer) throws Exception {
        log.info("customer process ===> {}", customer);
        return customer;
    }
}
