package br.com.sinerji.comprascrawler.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public final class Counter {
	
	@Getter
	private int value;
	
	public void increment() {
		value += 1;
	}
	
	public void decrement() {
		value -= 1;
	}
}
