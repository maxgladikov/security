package online.gladikov.security.config;

import java.util.Arrays;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;



//@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	@Override
	public Authentication authenticate (Authentication authentication) throws AuthenticationException {
		// The getName() method is  inherited by Authentication from the Principal interface.
		String username = authentication.getName();
		String password = String.valueOf(authentication.getCredentials());
		
		if ("maxx".equals(username) && "pas".equals(password)) {
		// This condition generally calls UserDetailsService and PasswordEncoder to test the username and password.
			return new UsernamePasswordAuthenticationToken (username, password, Arrays.asList());
		} else {
			throw new AuthenticationCredentialsNotFoundException ("Error in authentication!");
		}
	}

	@Override
	public boolean supports(Class<?> authenticationType) {
		return UsernamePasswordAuthenticationToken.class
				.isAssignableFrom(authenticationType);
	}
}