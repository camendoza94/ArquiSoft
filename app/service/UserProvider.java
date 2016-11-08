package service;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import models.UsuarioEntity;
import play.mvc.Http.Session;

import javax.annotation.Nullable;
import javax.inject.Inject;

/**
 * Service layer for User DB entity
 */
public class UserProvider {

    private final PlayAuthenticate auth;

    @Inject
    public UserProvider(final PlayAuthenticate auth) {
        this.auth = auth;
    }

    @Nullable
    public UsuarioEntity getUser(Session session) {
        final AuthUser currentAuthUser = this.auth.getUser(session);
        final UsuarioEntity localUser = UsuarioEntity.findByAuthUserIdentity(currentAuthUser);
        return localUser;
    }
}
