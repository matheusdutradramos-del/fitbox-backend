package com.itb.inf3cn.fitbox.model.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("CLIENTE")
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class Cliente extends Usuario {

    private String numeroPontos;
}
