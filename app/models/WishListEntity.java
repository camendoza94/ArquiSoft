package models;

import com.avaje.ebean.Model;

import javax.persistence.*;

@Entity
@Table(name = "wishlistentity")
public class WishListEntity extends Model{

    public static Finder<Long, WishListEntity> FINDER = new Finder<>(WishListEntity.class);

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE,generator = "WishList")
    private Long id;
    private String username;

    public WishListEntity() {
        this.id=null;
        this.username ="NO NAME";
    }

    public WishListEntity(Long id) {
        this();
        this.id = id;
    }

    public WishListEntity(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    @Override
    public String toString() {
        return "WishListEntity{" +
                "id=" + id +
                ", username='" + username + "\'}";
    }
}