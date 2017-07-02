package com.thomas.auth;

//import static org.assertj.core.api.Assertions.tuple;

import java.util.stream.Stream;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


//import org.omg.PortableInterceptor.ACTIVE;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SpringBootApplication
public class SpringAuthServiceApplication {
	
	@Bean
	CommandLineRunner clr(AccountRepository accountRepository){
		return args -> {
			Stream.of("aju, password1", "name2, password2", "name3, password3", "name4, password4")
			.map(tpl -> tpl.split(",") )
			.forEach(tpl -> accountRepository.save(new Account(tpl[0], tpl[1], true)));
		};
		
		
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringAuthServiceApplication.class, args);
	}
}


interface AccountRepository extends JpaRepository<Account, Long>{
	
}


@Data 
@NoArgsConstructor
@AllArgsConstructor
@Entity
class Account{
	 
	public Account(String username, String password, boolean active) {
		//super();
		this.username = username;
		this.password = password;
		this.active = active;
	}
	
	@GeneratedValue @Id
	private long id; 
	private String username, password;
	private boolean active;
	
	
}
