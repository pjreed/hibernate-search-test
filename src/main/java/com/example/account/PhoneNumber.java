package com.example.account;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;

import javax.persistence.*;

@Entity
@Table(name = "phone_numbers")
public class PhoneNumber {
    @ManyToOne
    @JoinColumn(name="account_id")
    @JsonBackReference
    @ContainedIn
    private Account account;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Field(name = "pn_id")
    private Long id;

    @Field
    private String number;

    public PhoneNumber() {

    }

    public PhoneNumber(Account account, String number) {
        this.account = account;
        this.number = number;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
