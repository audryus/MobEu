package com.mobiquityinc.packer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import com.mobiquityinc.exception.APIException;

/**
 * Testing the example sent.
 *
 */
public class PackerTest {

	String filePath;
	
	@Before
	public void before() {
		filePath = getClass().getClassLoader().getResource("example.txt").getPath().toString();
	}

	@Test
	public void test1() throws APIException {
		String returnedValue = Packer.pack(filePath);
		String expectedValue = "4\n" + 
				"-\n" + 
				"2,7\n" + 
				"8,9";
		assertEquals(expectedValue, returnedValue);
	}

	@Test
	public void test2() throws APIException {
		String returnedValue = Packer.pack(filePath);
		String expectedValue = "4\n" + 
				"-\n" + 
				"2,7\n" + 
				"8,2";
		assertNotEquals(expectedValue, returnedValue);
	}
	
}
