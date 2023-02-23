package org.pausanchez.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
public class Contacto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(length = 12)
    private String telefono1;
    @Column(length = 12)
    private String telefono2;
    private String mail;
    private String direccion;
    private String localidad;
    @Column(length = 8)
    private String codigoPostal;
    private String provincia;

    @ManyToOne()
    @JoinColumn(name = "cliente_fk", nullable = false)
    @JsonBackReference
    private Cliente cliente;

}
