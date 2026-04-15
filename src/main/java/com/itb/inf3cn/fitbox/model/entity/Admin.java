package com.itb.inf3cn.fitbox.model.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@DiscriminatorValue("ADMIN")
@Setter // atribui informação ao atributo
@Getter // recupera a informação do atributo
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Admin extends Usuario{

    private String nivelAcesso;

}
