package me.web_server;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Hasher {
	private final static MessageDigest digest;
	
	static {
		try {
			digest = MessageDigest.getInstance("SHA3-512");
		} catch (NoSuchAlgorithmException exception) {
			throw new RuntimeException(exception);
		}
	}

	private Hasher() {
		super();
	}

	public final static byte[] hash(String string) {
		digest.reset();

		return digest.digest(string.getBytes(StandardCharsets.UTF_8));
	}
}
