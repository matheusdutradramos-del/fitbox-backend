package com.itb.inf3cn.fitbox.model.entity;


import com.itb.inf3cn.fitbox.model.enums.TipoUsuario;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Usuario")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_usuario", discriminatorType = DiscriminatorType.STRING)
@Setter // atribui informação ao atributo
@Getter // recupera a informação do atributo
@NoArgsConstructor // Construtor sem parâmetros (padrão)
@AllArgsConstructor // Construtor com todos os parâmetros
@Builder // Forma otimizada para criação de objetos
@EqualsAndHashCode(onlyExplicitlyIncluded = true) //otimizar a busca dentro de coleções e evitar duplicidade de objetos
public class Usuario {

    @Id  // Chave Primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Auto-Increment (identificado de 1 em 1)
    @EqualsAndHashCode.Include
    private Long id;
    @Column(length = 100, nullable = true)
    private String nome;
    @Column(length = 15, nullable = true)
    private String cpf;
    @Column(length = 45, nullable = false)
    private String email;
    @Column(length = 255, nullable = false)
    private String password;
    @Column(length = 15, nullable = true)
    private String sexo;
    @Column(nullable = true)
    private String dataNascimento;
    @Column(length = 100, nullable = true)
    private String Logradouro;
    @Column(length = 10, nullable = true)
    private String cep;
    @Column(length = 45, nullable = true)
    private String bairro;
    @Column(length = 45, nullable = true)
    private String cidade;
    @Column(length = 45, nullable = true)
    private String uf;
    private boolean codStatus;
    @Enumerated(EnumType.STRING)
    @Column(length = 20, name = "tipo usuario", insertable = false, updatable = false)
    private TipoUsuario tipoUsuario;

}
