package service;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.service.AbstractUserService;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import models.UsuarioEntity;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class MyUserService extends AbstractUserService {

	@Inject
	public MyUserService(final PlayAuthenticate auth) {
		super(auth);
	}

	@Override
	public Object save(final AuthUser authUser) {
		final boolean isLinked = UsuarioEntity.existsByAuthUserIdentity(authUser);
		if (!isLinked) {
			return UsuarioEntity.create(authUser).getId();
		} else {
			// we have this user already, so return null
			return null;
		}
	}

	@Override
	public Object getLocalIdentity(final AuthUserIdentity identity) {
		// For production: Caching might be a good idea here...
		// ...and dont forget to sync the cache when users get deactivated/deleted
		final UsuarioEntity u = UsuarioEntity.findByAuthUserIdentity(identity);
		if(u != null) {
			return u.getId();
		} else {
			return null;
		}
	}

	@Override
	public AuthUser merge(final AuthUser newUser, final AuthUser oldUser) {
		if (!oldUser.equals(newUser)) {
			UsuarioEntity.merge(oldUser, newUser);
		}
		return oldUser;
	}

	@Override
	public AuthUser link(final AuthUser oldUser, final AuthUser newUser) {
		UsuarioEntity.addLinkedAccount(oldUser, newUser);
		return newUser;
	}
	
	@Override
	public AuthUser update(final AuthUser knownUser) {
		// User logged in again, bump last login date
		UsuarioEntity.setLastLoginDate(knownUser);
		return knownUser;
	}

}
