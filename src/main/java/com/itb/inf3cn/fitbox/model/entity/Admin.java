package com.itb.inf3cn.fitbox.model.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("ADMIN")
@Setter // atribui informação ao atributo
@Getter // recupera a informação do atributo
@EqualsAndHashCode(callSuper = true)
public class Admin extends Usuario{

    private String nivelAcesso;

}
