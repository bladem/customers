package org.pausanchez.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Data
@Table(name = "clientes")
@NoArgsConstructor
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    private String nombre;
    @NotNull
    private String apellido1;
    private String apellido2;
    private String numeroIdentificacion;

    @OneToMany(mappedBy = "cliente",cascade = {CascadeType.ALL},fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Contacto> contactos;

    public Cliente(Long id){
        this.id = id;
    }
}
