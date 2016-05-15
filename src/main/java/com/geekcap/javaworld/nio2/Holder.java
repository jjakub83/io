package com.geekcap.javaworld.nio2;

public class Holder<T> {

	Class<T> clazz;
	public T value;
	public Holder(Class<T> clazz) {
		try {
			value = clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
