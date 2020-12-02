package me.web_server;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.data.relational.core.mapping.Table;

@Entity
@Table(value = "test")
public class User {
	@Id
	private String username;
	private byte[] passwordHash;

	public User(String username, byte[] passwordHash) {
		super();

		this.username = username;
		this.passwordHash = passwordHash;
	}

	public String getUsername() {
		return username;
	}

	public byte[] getPasswordHash() {
		return passwordHash;
	}
}
