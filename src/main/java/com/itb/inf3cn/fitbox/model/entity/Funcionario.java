package com.itb.inf3cn.fitbox.model.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("FUNCIONARIO")
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class Funcionario extends Usuario{

    private String cnh;

}
