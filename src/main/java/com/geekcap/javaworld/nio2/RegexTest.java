package com.geekcap.javaworld.nio2;

public class RegexTest {

	public static void main(String[] args) {
		System.out.println("foo\n".split("\n").length);
		System.out.println("foo\n\n\n".split("\n").length);
		System.out.println("\nfoo\n\n".split("\n").length);
		System.out.println("foo\n".contains("\n"));
		
		System.out.println("foo\n".split("\n")[0]);
		
	}

}
