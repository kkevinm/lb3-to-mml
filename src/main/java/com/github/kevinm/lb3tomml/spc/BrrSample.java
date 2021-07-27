package com.github.kevinm.lb3tomml.spc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BrrSample {

	private final String name;
	private final List<Byte> data;

	public BrrSample(String name, List<Byte> data) {
		this.name = name;
		this.data = Collections.unmodifiableList(data);
	}

	public BrrSample(int number, List<Byte> data) {
		this(String.format("%02x", number), data);
	}

	public String getName() {
		return name;
	}

	public byte[] getData() {
		byte[] result = new byte[data.size()];
		for (int i = 0; i < data.size(); i++) {
			result[i] = data.get(i);
		}
		return result;
	}

	public int getAmkHeader() {
		return ((data.get(0) & 0xff) + (data.get(1) << 8)) & 0xffff;
	}

	public String getFullName() {
		return name + ".brr";
	}

	public int getSize() {
		return data.size() - 2;
	}

	public static BrrSample extract(String name, Aram aram, int startAddress, int loopAddress) {
		boolean end = false;
		int currentAddress = startAddress;

		List<Byte> data = new ArrayList<>();
		int amkHeader = loopAddress - startAddress;
		data.add((byte) (amkHeader & 0xff));
		data.add((byte) ((amkHeader>>8) & 0xff));

		while (!end) {
			byte header = (byte) aram.getUnsignedByte(currentAddress++);
			data.add(header);

			for (int i = 0; i < 8; i++) {
				data.add((byte) aram.getUnsignedByte(currentAddress++));
			}

			if ((header & 0x01) != 0) {
				end = true;
			}
		}

		return new BrrSample(name, data);
	}

	public static BrrSample extract(int number, Aram aram, int startAddress, int loopAddress) {
		return extract(String.format("%02x", number), aram, startAddress, loopAddress);
	}

}
