package org.axonframework.samples.bank;

import static org.junit.Assert.*;

import java.util.UUID;

import javax.transaction.Transactional;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.samples.bank.api.bankaccount.CreateBankAccountCommand;
import org.axonframework.samples.bank.api.banktransfer.CreateBankTransferCommand;
import org.axonframework.samples.bank.command.BankAccount;
import org.axonframework.samples.bank.query.bankaccount.BankAccountRepository;
import org.axonframework.samples.bank.query.banktransfer.BankTransferRepository;
import org.axonframework.samples.bank.web.dto.BankAccountDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.netflix.discovery.converters.Auto;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@Transactional
public class AxonBankApplicationTest {
	@Autowired
	private CommandGateway commandGateway;

	@Autowired
	private BankAccountRepository query;
	@Autowired
	private BankTransferRepository query2;
	
	@Autowired
	private EventStore store;
	
	private ObjectMapper om = new ObjectMapper()
			.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
			.configure(SerializationFeature.INDENT_OUTPUT, true)
			.setVisibility(PropertyAccessor.ALL, Visibility.NONE)
			.setVisibility(PropertyAccessor.FIELD,Visibility.ANY);

	//	@Autowired
	private Repository<BankAccount> repository;

	@Test
	public void test() throws Exception {
		// given
		CreateBankAccountCommand command1
			= new CreateBankAccountCommand(UUID.randomUUID().toString(), 100000L);
		
		// when
		commandGateway.send(command1);
		
		// when
		System.out.println(query.count());
		System.out.println(
				om.writeValueAsString(query.findAll())
				);
	}
	
	@Test
	public void testName() throws Exception {
		// given
		CreateBankAccountCommand command1
		= new CreateBankAccountCommand(UUID.randomUUID().toString(), 100000L);
		CreateBankAccountCommand command2
		= new CreateBankAccountCommand(UUID.randomUUID().toString(), 100000L);
		CreateBankTransferCommand command3 
		= new CreateBankTransferCommand(
				UUID.randomUUID().toString(), 
				command1.getBankAccountId(), 
				command2.getBankAccountId(), 
				500L);
		
		// when
		commandGateway.send(command1);
		commandGateway.send(command2);
		commandGateway.send(command3);
		
		// when
		System.out.println(query.count());
		System.out.println(
				om.writeValueAsString(query.findAll())
				);
		System.out.println("--- --- ---");
		System.out.println(query2.count());
		System.out.println(
				om.writeValueAsString(query2.findAll())
				);
	}
}
		