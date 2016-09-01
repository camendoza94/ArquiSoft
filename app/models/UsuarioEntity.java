package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
@Table(name = "usuarioentity")
public class UsuarioEntity extends Model{

    public static Model.Finder<Long, UsuarioEntity> FINDER = new Model.Finder<>(UsuarioEntity.class);

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE,generator = "Usuario")
    private Long id;

    private String nombre;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JsonBackReference
    @JoinColumn(name = "campo_id")
    private CampoEntity campo;

    public UsuarioEntity(){

    }

    public CampoEntity getCampo() {
        return campo;
    }

    public void setCampo(CampoEntity campo) {
        this.campo = campo;
    }

    public UsuarioEntity(Long id, String nombre){
        this.id = id;
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "UsuarioEntity{" +
                "id=" + id + ", " +
                "nombre=" + nombre +
                '}';
    }
}
