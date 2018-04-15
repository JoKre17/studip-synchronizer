package de.luh.kriegel.studip.synchronizer.content.util;

import java.math.BigInteger;

public class Id {

	// 6e6f002856c3a0480a36bd1d97d67c70
	private String id_hex;
	
	// 294301540684354112428131192988899370874525954272
	private BigInteger id_bi;
	
	public Id(String hex) {
		assert hex != null;
		assert hex.length() == 32;
		
		this.id_hex = hex;
		this.id_bi = new BigInteger(hex, 32);
	}
	
	public Id(BigInteger bi) {
		assert bi != null;
		
		this.id_hex = bi.toString();
		this.id_bi = bi;
	}
	
	public String asHex() {
		return id_hex;
	}
	
	public BigInteger asBigInt() {
		return id_bi;
	}
	
}
