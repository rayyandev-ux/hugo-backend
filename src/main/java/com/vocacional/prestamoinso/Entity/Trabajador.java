package com.vocacional.prestamoinso.Entity;

import com.vocacional.prestamoinso.Entity.enums.ERole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "trabajador")
public class Trabajador extends User{
    private boolean needsPasswordChange = true;
}