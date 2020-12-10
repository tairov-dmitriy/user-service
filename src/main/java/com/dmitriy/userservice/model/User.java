package com.dmitriy.userservice.model;

import com.dmitriy.userservice.validation.DateConstraint;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "login")
    @ApiModelProperty(value = "User login", name = "login", required = true, example = "Ivanov")
    @NotNull
    @Size(min = 1, max = 32)
    private String login;

    @Column(name = "fullName")
    @ApiModelProperty(value = "Full user name", name = "fullName", required = true, example = "Ivanov Ivan")
    @NotNull
    @Size(min = 1, max = 256)
    private String fullName;

    @Column(name = "birthDate")
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy", timezone="UTC")
    @ApiModelProperty(value = "User birth date", name = "birthDate", required = true, example = "1.12.1988")
    @NotNull
    @DateConstraint
    private Date birthDate;

    @Column(name = "phone")
    @ApiModelProperty(value = "User phone", name = "phone", required = true, example = "+7-111-111-11-11")
    @Size(min = 1, max = 20)
    @Pattern(regexp = "^\\+?[\\d\\s-]*(\\([\\d\\s-]*\\))?[\\d\\s-]*$")
    private String phone;

    @Column(name = "locked")
    @ApiModelProperty(value = "Flag of user locking", name = "locked", required = true, example = "false")
    @NotNull
    private Boolean locked = false;

    public User() {}

    public User(@NotNull @Size(min = 1, max = 32) String login,
                @NotNull @Size(min = 1, max = 256) String fullName,
                @NotNull @DateConstraint Date birthDate,
                @Size(min = 1, max = 20) @Pattern(regexp = "^\\+?[\\d\\s-]*(\\([\\d\\s-]*\\))?[\\d\\s-]*$") String phone) {
        this.login = login;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.phone = phone;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        User u = ((User)obj);
        return login.equals(u.getLogin()) && fullName.equals(u.getFullName()) && birthDate.equals(u.getBirthDate()) &&
               phone.equals(u.getPhone()) && locked == u.getLocked();
    }

    @Override
    public int hashCode() {
        return login.hashCode();
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }
}
