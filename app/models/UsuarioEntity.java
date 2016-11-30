package models;

import be.objectify.deadbolt.java.models.Subject;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import com.feth.play.module.pa.user.*;
import controllers.Application;
import play.data.format.Formats;
import play.data.validation.Constraints;
import be.objectify.deadbolt.java.models.Permission;
import be.objectify.deadbolt.java.models.Role;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import models.TokenAction.Type;
import play.data.format.Formats;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "usuarioentity")
public class UsuarioEntity extends Model implements Subject {

    public static Model.Finder<Long, UsuarioEntity> FINDER = new Model.Finder<>(UsuarioEntity.class);

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE,generator = "Usuario")
    private Long id;

    @Constraints.Email
    // if you make this unique, keep in mind that users *must* merge/link their
    // accounts then on signup with additional providers
    // @Column(unique = true)
    public String email;

    public String name;

    public String lastName;

    public String firstName;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date lastLogin;

    public boolean active;

    public boolean emailValidated;

    @ManyToMany
    public List<SecurityRole> roles;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    public List<LinkedAccount> linkedAccounts;


    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JsonBackReference
    @JoinColumn(name = "campo_id")
    private CampoEntity campo;

    @ManyToMany
    public List<UserPermission> permissions;

    public UsuarioEntity(){

    }

    public CampoEntity getCampo() {
        return campo;
    }

    public void setCampo(CampoEntity campo) {
        this.campo = campo;
    }

    public UsuarioEntity(Long id, String name){
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return name;
    }

    public void setNombre(String nombre) {
        this.name = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getApellido() {
        return lastName;
    }

    public void setApellido(String apellido) {
        this.lastName = apellido;
    }
    @Override
    public String getIdentifier()
    {
        return Long.toString(this.id);
    }

    @Override
    public List<? extends Role> getRoles() {
        return this.roles;
    }

    @Override
    public List<? extends Permission> getPermissions() {
        return this.permissions;
    }

    public static boolean existsByAuthUserIdentity(
            final AuthUserIdentity identity) {
        final ExpressionList<UsuarioEntity> exp;
        if (identity instanceof UsernamePasswordAuthUser) {
            exp = getUsernamePasswordAuthUserFind((UsernamePasswordAuthUser) identity);
        } else {
            exp = getAuthUserFind(identity);
        }
        return exp.findRowCount() > 0;
    }

    private static ExpressionList<UsuarioEntity> getAuthUserFind(
            final AuthUserIdentity identity) {
        return FINDER.where().eq("active", true)
                .eq("linkedAccounts.providerUserId", identity.getId())
                .eq("linkedAccounts.providerKey", identity.getProvider());
    }

    public static UsuarioEntity findByAuthUserIdentity(final AuthUserIdentity identity) {
        if (identity == null) {
            return null;
        }
        if (identity instanceof UsernamePasswordAuthUser) {
            return findByUsernamePasswordIdentity((UsernamePasswordAuthUser) identity);
        } else {
            return getAuthUserFind(identity).findUnique();
        }
    }

    public static UsuarioEntity findByUsernamePasswordIdentity(
            final UsernamePasswordAuthUser identity) {
        return getUsernamePasswordAuthUserFind(identity).findUnique();
    }

    private static ExpressionList<UsuarioEntity> getUsernamePasswordAuthUserFind(
            final UsernamePasswordAuthUser identity) {
        return getEmailUserFind(identity.getEmail()).eq(
                "linkedAccounts.providerKey", identity.getProvider());
    }

    public void merge(final UsuarioEntity otherUser) {
        for (final LinkedAccount acc : otherUser.linkedAccounts) {
            this.linkedAccounts.add(LinkedAccount.create(acc));
        }
        // do all other merging stuff here - like resources, etc.

        // deactivate the merged user that got added to this one
        otherUser.active = false;
        Arrays.asList(new UsuarioEntity[] { otherUser, this }).forEach(u -> Ebean.save(u));
    }

    public static UsuarioEntity create(final AuthUser authUser) {
        final UsuarioEntity user = new UsuarioEntity();
        // user.permissions = new ArrayList<UserPermission>();
        if (((EmailIdentity) authUser).getEmail().equals("admin@admin.com")){
            user.roles = Collections.singletonList(SecurityRole
                    .findByRoleName(Application.ADMIN_ROLE));

        }else
        {
            user.roles = Collections.singletonList(SecurityRole
                    .findByRoleName(controllers.Application.USER_ROLE));
        }     // user.permissions.add(UserPermission.findByValue("printers.edit"));
        user.active = true;
        user.lastLogin = new Date();
        user.linkedAccounts = Collections.singletonList(LinkedAccount
                .create(authUser));

        if (authUser instanceof EmailIdentity) {
            final EmailIdentity identity = (EmailIdentity) authUser;
            // Remember, even when getting them from FB & Co., emails should be
            // verified within the application as a security breach there might
            // break your security as well!
            user.email = identity.getEmail();
            user.emailValidated = false;
        }

        if (authUser instanceof NameIdentity) {
            final NameIdentity identity = (NameIdentity) authUser;
            final String name = identity.getName();
            if (name != null) {
                user.name = name;
            }
        }

        if (authUser instanceof FirstLastNameIdentity) {
            final FirstLastNameIdentity identity = (FirstLastNameIdentity) authUser;
            final String firstName = identity.getFirstName();
            final String lastName = identity.getLastName();
            if (firstName != null) {
                user.firstName = firstName;
            }
            if (lastName != null) {
                user.lastName = lastName;
            }
        }

        user.save();
        // Ebean.saveManyToManyAssociations(user, "roles");
        // Ebean.saveManyToManyAssociations(user, "permissions");
        return user;
    }

    public static void merge(final AuthUser oldUser, final AuthUser newUser) {
        UsuarioEntity.findByAuthUserIdentity(oldUser).merge(
                UsuarioEntity.findByAuthUserIdentity(newUser));
    }

    public Set<String> getProviders() {
        final Set<String> providerKeys = new HashSet<String>(
                this.linkedAccounts.size());
        for (final LinkedAccount acc : this.linkedAccounts) {
            providerKeys.add(acc.providerKey);
        }
        return providerKeys;
    }

    public static void addLinkedAccount(final AuthUser oldUser,
                                        final AuthUser newUser) {
        final UsuarioEntity u = UsuarioEntity.findByAuthUserIdentity(oldUser);
        u.linkedAccounts.add(LinkedAccount.create(newUser));
        u.save();
    }

    public static void setLastLoginDate(final AuthUser knownUser) {
        final UsuarioEntity u = UsuarioEntity.findByAuthUserIdentity(knownUser);
        u.lastLogin = new Date();
        u.save();
    }

    public static UsuarioEntity findByEmail(final String email) {
        return getEmailUserFind(email).findUnique();
    }

    private static ExpressionList<UsuarioEntity> getEmailUserFind(final String email) {
        return FINDER.where().eq("active", true).eq("email", email);
    }

    public LinkedAccount getAccountByProvider(final String providerKey) {
        return LinkedAccount.findByProviderKey(this, providerKey);
    }

    public static void verify(final UsuarioEntity unverified) {
        // You might want to wrap this into a transaction
        unverified.emailValidated = true;
        unverified.save();
        TokenAction.deleteByUser(unverified, Type.EMAIL_VERIFICATION);
    }

    public void changePassword(final UsernamePasswordAuthUser authUser,
                               final boolean create) {
        LinkedAccount a = this.getAccountByProvider(authUser.getProvider());
        if (a == null) {
            if (create) {
                a = LinkedAccount.create(authUser);
                a.user = this;
            } else {
                throw new RuntimeException(
                        "Account not enabled for password usage");
            }
        }
        a.providerUserId = authUser.getHashedPassword();
        a.save();
    }

    public void resetPassword(final UsernamePasswordAuthUser authUser,
                              final boolean create) {
        // You might want to wrap this into a transaction
        this.changePassword(authUser, create);
        TokenAction.deleteByUser(this, Type.PASSWORD_RESET);
    }
}
