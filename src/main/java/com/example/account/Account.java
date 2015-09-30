package com.example.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")
@Entity
@Table(name = "account")
@NamedQuery(name = Account.FIND_BY_EMAIL, query = "select a from Account a where a.email = :email")
@Indexed
public class Account implements java.io.Serializable {

	public static final String FIND_BY_EMAIL = "Account.findByEmail";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Field(name = "account_id")
	private Long id;

	@Column(unique = true)
    @Field
	private String email;
	
	@JsonIgnore
	private String password;

    @Field
	private String role = "ROLE_USER";

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    @IndexedEmbedded(depth=1, prefix="phone_number_")
    private Set<PhoneNumber> phoneNumbers = new HashSet<>();

    protected Account() {

	}
	
	public Account(String email, String password, String role) {
		this.email = email;
		this.password = password;
		this.role = role;
	}

	public Long getId() {
		return id;
	}

    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

    public Set<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    private void setPhoneNumbers(Set<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
}
