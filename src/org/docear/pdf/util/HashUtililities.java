package org.docear.pdf.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtililities {

	/**
	 * A hash function used in Berkeley DB
	 *  
	 * @param str Value to be hashed
	 * @return the 64bit hash code
	 */
	public static long hashBerkeleyDB64(String str) {
		return hashBerkeleyDB64(str.getBytes());
	}

	/**
	 * A hash function used in Berkeley DB
	 *  
	 * @param str Value to be hashed
	 * @return the 64bit hash code
	 */
	public static long hashBerkeleyDB64(byte[] str) {
		long hash = 0;

		for (int i = 0; i < str.length; i++) {
			hash = str[i] + (hash << 6) + (hash << 16) - hash;
		}

		return hash;
	}

	/**
	 * Standard Template Library C++ - hash function 
	 * @param str Value to be hashed
	 * @return the 64bit hash code
	 */
	public static long hashSTL64(String str) {
		return hashSTL64(str.getBytes());
	}

	/**
	 * Standard Template Library C++ - hash function 
	 * @param str Value to be hashed
	 * @return the 64bit hash code
	 */
	public static long hashSTL64(byte[] str) {
		long hash = 0;
		for (int i = 0; i < str.length; i++) {
			hash = 5 * hash + str[i];
		}
		return hash;
	}

	
	/**
	 *  FNV hashes are designed to be fast while maintaining a low collision rate. The FNV speed allows one to quickly hash lots of data while maintaining a reasonable collision rate. The high dispersion of the FNV hashes makes them well suited for hashing nearly identical strings such as URLs, hostnames, filenames, text, IP addresses, etc.<br><br>
	 *  The IETF has an informational draft on The FNV Non-Cryptographic Hash Algorithm: <code>http://tools.ietf.org/html/draft-eastlake-fnv-03</code> 
	 * @param str Value to be hashed
	 * @return hex string representing the hash code
	 */
	public static long hashFNV64(String str) {
		return hashFNV64(str.getBytes());
	}

	
	/**
	 *  FNV hashes are designed to be fast while maintaining a low collision rate. The FNV speed allows one to quickly hash lots of data while maintaining a reasonable collision rate. The high dispersion of the FNV hashes makes them well suited for hashing nearly identical strings such as URLs, hostnames, filenames, text, IP addresses, etc.<br><br>
	 *  The IETF has an informational draft on The FNV Non-Cryptographic Hash Algorithm: <code>http://tools.ietf.org/html/draft-eastlake-fnv-03</code> 
	 * @param str Value to be hashed
	 * @return hex string representing the hash code
	 */
	public static long hashFNV64(byte[] bytes) {
		long nHashVal = 0xcbf29ce484222325L;
		long nMagicPrime = 0x00000100000001b3L;

		for (int i = 0; i < bytes.length; i++) {
			nHashVal ^= bytes[i];
			nHashVal *= nMagicPrime;
		}

		return nHashVal;
	}
	
	
	/**
	 *  FNV hashes are designed to be fast while maintaining a low collision rate. The FNV speed allows one to quickly hash lots of data while maintaining a reasonable collision rate. The high dispersion of the FNV hashes makes them well suited for hashing nearly identical strings such as URLs, hostnames, filenames, text, IP addresses, etc.<br><br>
	 *  The IETF has an informational draft on The FNV Non-Cryptographic Hash Algorithm: <code>http://tools.ietf.org/html/draft-eastlake-fnv-03</code> 
	 * @param str Value to be hashed
	 * @return hex string representing the hash code
	 */
	public static String hashFNV128(String str) {
		return hashFNV128(str.getBytes());
	}
	
	/**
	 *  FNV hashes are designed to be fast while maintaining a low collision rate. The FNV speed allows one to quickly hash lots of data while maintaining a reasonable collision rate. The high dispersion of the FNV hashes makes them well suited for hashing nearly identical strings such as URLs, hostnames, filenames, text, IP addresses, etc.<br><br>
	 *  The IETF has an informational draft on The FNV Non-Cryptographic Hash Algorithm: <code>http://tools.ietf.org/html/draft-eastlake-fnv-03</code> 
	 * @param str byte array
	 * @return hex string representing the hash code
	 */
	public static String hashFNV128(byte[] str) {
		/**
		 * http://www.isthe.com/chongo/tech/comp/fnv/index.html#FNV-1a
		 * 
		 * 32 bit FNV_prime = 224 + 28 + 0x93 = 16777619<br>
		 * 
		 * 64 bit FNV_prime = 240 + 28 + 0xb3 = 1099511628211<br>
		 * 
		 * 128 bit FNV_prime = 288 + 28 + 0x3b = 309485009821345068724781371<br>
		 * 
		 * 256 bit FNV_prime = 2168 + 28 + 0x63 = 374144419156711147060143317175368453031918731002211<br>
		 * 
		 * 512 bit FNV_prime = 2344 + 28 + 0x57 = 35835915874844867368919076489095108449946327955754392558399825615420669938882575126094039892345713852759<br>
		 * 
		 * 1024 bit FNV_prime = 2680 + 28 + 0x8d = 5016456510113118655434598811035278955030765345404790744303017523831112055108147451509157692220295382716162651878526895249385292291816524375083746691371804094271873160484737966720260389217684476157468082573<br>
		 * <br>
		 * 
		 * 32 bit offset_basis = 2166136261<br>
		 * 
		 * 64 bit offset_basis = 14695981039346656037<br>
		 * 
		 * 128 bit offset_basis = 144066263297769815596495629667062367629<br>
		 * 
		 * 256 bit offset_basis = 100029257958052580907070968620625704837092796014241193945225284501741471925557<br>
		 * 
		 * 512 bit offset_basis = 9659303129496669498009435400716310466090418745672637896108374329434462657994582932197716438449813051892206539805784495328239340083876191928701583869517785<br>
		 * 
		 * 1024 bit offset_basis = 14197795064947621068722070641403218320880622795441933960878474914617582723252296732303717722150864096521202355549365628174669108571814760471015076148029755969804077320157692458563003215304957150157403644460363550505412711285966361610267868082893823963790439336411086884584107735010676915<br>
		 * <br>
		 * 
		 */
		BigInteger nHashVal = new BigInteger("144066263297769815596495629667062367629");
		BigInteger nMagicPrime = new BigInteger("309485009821345068724781371");
		
		for (int i = 0; i < str.length; i++) {
			nHashVal = nHashVal.xor(BigInteger.valueOf(str[i])); //^= str[i];
			nHashVal = nHashVal.multiply(nMagicPrime); //*= nMagicPrime;
		}
		return nHashVal.toString(16);
	}
	public static String hashSHA2(String str) {
		try {
			return hashSHA2(str.getBytes("UTF-8"));
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	public static String hashSHA2(byte[] str) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] mdbytes = md.digest(str);
			StringBuffer hexString = new StringBuffer();
	    	for (int i=0;i<mdbytes.length;i++) {
	    	  hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
	    	}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}
}
