package im.toduck.global.security.authentication;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import im.toduck.domain.user.persistence.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CustomUserDetails implements UserDetails {
	private static final String ROLE_PREFIX = "ROLE_";
	private final Long userId;
	private final String username;
	private Collection<? extends GrantedAuthority> authorities;

	@Builder
	private CustomUserDetails(Long userId, String username,
		Collection<? extends GrantedAuthority> authorities) {
		this.userId = userId;
		this.username = username;
		this.authorities = authorities;
	}

	public static CustomUserDetails from(User user) {
		return CustomUserDetails.builder()
			.userId(user.getId())
			.username(user.getNickname())
			.authorities(List.of(new SimpleGrantedAuthority(ROLE_PREFIX + user.getRole().name())))
			.build();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
