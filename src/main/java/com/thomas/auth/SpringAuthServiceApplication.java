package com.thomas.auth;

import java.util.Optional;

//import static org.assertj.core.api.Assertions.tuple;

import java.util.stream.Stream;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.springframework.security.core.userdetails.User;

//import org.omg.PortableInterceptor.ACTIVE;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SpringBootApplication
public class SpringAuthServiceApplication {
	
	@Bean
	CommandLineRunner clr(AccountRepository accountRepository){
		return args -> {
			Stream.of("name1,password1", "name2,password2", "name3,password3", "name4,password4")
			.map(tpl -> tpl.split(",") ) 
			.forEach(tpl -> accountRepository.save(new Account(tpl[0], tpl[1], true)));
		};
		
		
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringAuthServiceApplication.class, args);
	}
}


@Configuration
@EnableAuthorizationServer
class AuthServiceConfiguration extends AuthorizationServerConfigurerAdapter{
	private final AuthenticationManager authenticationManager;
	
	public AuthServiceConfiguration(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients
		.inMemory()
		.withClient("html5")
		.secret("secret")
		.authorizedGrantTypes("password")
		.scopes("openid");

	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(this.authenticationManager);
	}
	
	
	
}


@Service
class AccountUserDetailService implements UserDetailsService{
	
	private final AccountRepository accountRepository;
	
	public AccountUserDetailService(AccountRepository accountRepository) {
//		super();
		this.accountRepository = accountRepository;
	}


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		return accountRepository.findByUsername (username)
				.map(account -> new User(account.getUsername(),
						account.getPassword(), account.isActive(), account.isActive(), account.isActive(), account.isActive(),
						AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER") )
						)
				.orElseThrow(() -> new UsernameNotFoundException("Couldn't fine user name " + username + "!") ) ;
	}
	
    /*@Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findByUsername(username)
                .map(account -> {
                    boolean active = account.isActive();
                    return new User(
                            account.getUsername(),
                            account.getPassword(),
                            active, active, active, active,
                            AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER"));
                })
                .orElseThrow(() -> new UsernameNotFoundException(String.format("username %s not found!", username)));
    }*/
	
}



interface AccountRepository extends JpaRepository<Account, Long>{
	Optional<Account> findByUsername(String username); 
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
