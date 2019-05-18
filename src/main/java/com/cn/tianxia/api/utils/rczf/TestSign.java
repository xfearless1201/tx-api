package com.cn.tianxia.api.utils.rczf;

public class TestSign {
	public static void main(String[] args) throws Exception {
		String publicKeyStr = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC0sJMGLD0UQUYObjsMHBGUYQEVEOCkBCNzzkYWSM0RYToK49hLpmxpNLbNcSMSUwOs6AfzDW9Tbpcotjg4JiphZqrBjG4Vj2acQPxBp06oJBYdvoCM42AFFLthHNDTmP+O7OYPrwiTTSYPlIUO8HyojhfQ6Dc9guiit7L98FWhmQIDAQAB";
//				"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCAYZ5gqFKPOOmiJk+IVstJPfS5DRnGIByjMOL0Waod0v2LHZO7tRubdsWti6JxjNS5Syu0G82YDCyhmEVwy0AE6ufrV7f3IhAQ9AJPkZCA9pCEjDSHtVtt3823A+PFtyQ1Lku+eWqcou+7IwT3uW2a6ZAb9VCcJmVbYFk+xkThdQIDAQAB";
//		String privateKeyStr = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIBhnmCoUo846aImT4hWy0k99LkNGcYgHKMw4vRZqh3S/Ysdk7u1G5t2xa2LonGM1LlLK7QbzZgMLKGYRXDLQATq5+tXt/ciEBD0Ak+RkID2kISMNIe1W23fzbcD48W3JDUuS755apyi77sjBPe5bZrpkBv1UJwmZVtgWT7GROF1AgMBAAECgYA3Jzj+N3H4e6/LbIAAh6Ef3xESqvEmr8b9yNPHu/mchHdOW6+LFZw9psH386Qe+iytSgEFiGhD6P8HkT5L3dWrarrzm7hzWjgVjSRJfk+vR58jqCBXCdCZyCe8QjBeUKIiCdWA3rx9NsO+OZ++2tVMpa9P+c+4yiwUlNK7JXB8BQJBAMbws5q9sW048Y9LLc5EITT1knEKO6HrWRCElz1Kql6W2YemStHGVpIGlYoboPxqqOwyRpszbw+XAapBwPdEHMcCQQClNBbO3LGS8tIPOEa8mshYQ7pLNcw3KBJ5o3ILrWflVjSr7udkMv2KskYJPaPtuAAVUQ1qVnjEYi3N66EfVrvjAkBf7E+tnSmf8IUJAsbjXhZk4sPpnXWDbWdUf5otA4OCeFoK/jO1Ul0LrAEOxqOpEgTBXryMuRAkBDvZTDsu/rihAkEAj9hTI7u2QqV7khUGQqLjXzdZtrMZJc2WiKNwYgqTNHVjV3GluPFNIr8njFRHsG1OZUE11SmF7jkueOZ6XLdA0QJAKgnupYCFvUCtOga6EmHilhZhQwOdgw/cUkmCYK113uuuUKV1JvtHXI/qrGfXZwFWm35kIWfy/EOpidjm+vGlnQ==";
		String privateKeyStr = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMMm1rHzTn99rMDQv0CW6jcA8spjCFejkZBcdyp5kPqGuII3QJXKH9TV68Uz+7A3aFeJvTJ7xVT4/2+L3xq3FpEqqHBkCTfDkHf7GB4AhzJymBdXkJC7wYLvhYGNf59mvUqnm6SU0XktOZb6itKvnLIyupQFJqDKE54HeLMBQxYrAgMBAAECgYAobb5ipT4o6VdFprlIXztsY7TourV6uncoig9h7Eddr1VAHMQzg+kuRZcPhqJoskHaiL16XOvXm7IHYNm6hh2VXh1ZMtrKaV7bA7FLBmKSNV39Y/MIGzgLMxS96mt2JnxD2LRWDV1KyzQlmBNSf5lijhuQcttm+a7BAV7NSnNJCQJBAPsRn3+GMcRvY8Vt/VcldS/PVDtiAkmTogIluKwCQnrAiAr+PG/tLRY5PLTnh/DgAmZYJCyM06c2UtO/OgQEV+0CQQDG/BBMsC1YRch+qrx9J5P3+yX9MceXkRGvXBuiWspNmBDmaVoCGNgjd0cN3XgOXZ1i3gri4TqSSUQI3qlxYjN3AkAefZJoM0zh9UEhnezxY2wq5TvuhkWO1+4J4rjdstyN+cnLw/plAWHDXCoiMigRObMw6K1j96pQmUlPy95o1Ho1AkA+RdhcB67JN12dtpUyndZC/0hOSuvp1S6xsKO9VaiGTBbN5R6UFW5e+w8zmaHe7RE6Rb8mbdJEwcUW+YgRwefVAkEA+uSciKgIfuYIrytDmDH2IFpBHIkZCKwgK4tREspKvfNhq6hrJ+VTcc8umOY0XdSujBsakvnQHYVjIx+OyESu0A==";
		String plainText = "中国";
		
		/* signature */
		String signData = SignUtils.Signaturer(plainText,privateKeyStr);
		System.out.println("original text:"+plainText);
		System.out.println("signData："+signData);
		
		/* Verify signature  */
		boolean signStat = SignUtils.validataSign(plainText, signData, publicKeyStr);
		System.out.println("Verify signature result:"+signStat);
		
		/* Data encryption and decryption */
		String priEncodeStr = RSAUtils.privateKeyEncrypt(privateKeyStr, plainText);
		System.out.println("Encryption results -- priEncodeStr："+priEncodeStr);
		String pubDecodeStr = RSAUtils.publicKeyDecrypt(publicKeyStr, priEncodeStr);
		
		
		System.out.println("decryption results -- pubDecodeStr："+pubDecodeStr);
	}
}
