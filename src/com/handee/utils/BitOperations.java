package com.handee.utils;

public class BitOperations
{
	public static void printBinary(byte b) 
	{
		System.out.print("byte:" + b + ",binary:");
		for (int j = 7; j >= 0; j--)
			if (((1 << j) & b) != 0)
				System.out.print("1");
			else
				System.out.print("0");
		System.out.println();
	}
	
	public static void printBinary(char c)
	{
		System.out.print("char:" + c + ",binary:");
		for (int j = 7; j >= 0; j--)
			if (((1 << j) & c) != 0)
				System.out.print("1");
			else
				System.out.print("0");
		System.out.println();
	}
	
	public static void printBinary(short s) 
	{
		System.out.print("byte:" + s + ",binary:");
		for (int j = 15; j >= 0; j--)
			if (((1 << j) & s) != 0)
				System.out.print("1");
			else
				System.out.print("0");
		System.out.println();
	}
	
	public static void printBinary(int i) 
	{
		System.out.print("int:" + i + ",binary:");
		for (int j = 31; j >= 0; j--)
			if (((1 << j) & i) != 0)
				System.out.print("1");
			else
				System.out.print("0");
		System.out.println();
	}
	
	public static void printBinary(long l) 
	{
		System.out.print("long:" + l + ",binary:");
		for (int j = 63; j >= 0; j--)
			if ((((long)1 << j) & l) != 0)
				System.out.print("1");
			else
				System.out.print("0");
		System.out.println();
	}
	
	public static void main(String[] args)
	{
		BitOperations.printBinary(12321312);
		BitOperations.printBinary(4);
		BitOperations.printBinary(12321312 | 4);
		BitOperations.printBinary(12321312 & 32);
//		BitOperations.printBinary(231);
		//System.out.println(i & 1);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}