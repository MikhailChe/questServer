package test.model.common;

import org.junit.Assert;
import org.junit.Test;

import quest.model.common.classes.PacketData;

public class TestPacketData {
	@Test
	public void TestConsutrctorByteArray() {
		byte[] byteArray = null;
		PacketData dataPacket = null;

		byteArray = new byte[] { 0, 0, 0, 0 };
		dataPacket = new PacketData(byteArray);
		Assert.assertEquals(0, dataPacket.perifiral);
		Assert.assertEquals(false, dataPacket.write);
		Assert.assertNotNull(dataPacket.data);
		Assert.assertEquals(0, dataPacket.data.length);

		byteArray = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		dataPacket = new PacketData(byteArray);
		Assert.assertEquals(0, dataPacket.perifiral);
		Assert.assertEquals(false, dataPacket.write);
		Assert.assertNotNull(dataPacket.data);
		Assert.assertEquals(0, dataPacket.data.length);

		byteArray = new byte[] { 0, 1, 0, 0 };
		dataPacket = new PacketData(byteArray);
		Assert.assertEquals(0, dataPacket.perifiral);
		Assert.assertEquals(true, dataPacket.write);
		Assert.assertNotNull(dataPacket.data);
		Assert.assertEquals(0, dataPacket.data.length);

		byteArray = new byte[] { 0, 1, 0, 0, 1 };
		dataPacket = new PacketData(byteArray);
		Assert.assertEquals(0, dataPacket.perifiral);
		Assert.assertEquals(true, dataPacket.write);
		Assert.assertNotNull(dataPacket.data);
		Assert.assertEquals(0, dataPacket.data.length);

		byteArray = new byte[] { 0, 1, 0, 1 };
		dataPacket = new PacketData(byteArray);
		Assert.assertEquals(0, dataPacket.perifiral);
		Assert.assertEquals(true, dataPacket.write);
		Assert.assertNotNull(dataPacket.data);
		Assert.assertEquals(1, dataPacket.data.length);
		Assert.assertArrayEquals(new byte[] { 0 }, dataPacket.data);

		byteArray = new byte[] { 0, 1, 0, 1, 17 };
		dataPacket = new PacketData(byteArray);
		Assert.assertEquals(0, dataPacket.perifiral);
		Assert.assertEquals(true, dataPacket.write);
		Assert.assertNotNull(dataPacket.data);
		Assert.assertEquals(1, dataPacket.data.length);
		Assert.assertArrayEquals(new byte[] { 17 }, dataPacket.data);

		byteArray = new byte[] { 0, 1, 0, 1, 0, 16 };
		dataPacket = new PacketData(byteArray);
		Assert.assertEquals(0, dataPacket.perifiral);
		Assert.assertEquals(true, dataPacket.write);
		Assert.assertNotNull(dataPacket.data);
		Assert.assertEquals(1, dataPacket.data.length);
		Assert.assertArrayEquals(new byte[] { 0 }, dataPacket.data);

		byteArray = new byte[] { 0, 1, 0, 1, 12, 16 };
		dataPacket = new PacketData(byteArray);
		Assert.assertEquals(0, dataPacket.perifiral);
		Assert.assertEquals(true, dataPacket.write);
		Assert.assertNotNull(dataPacket.data);
		Assert.assertEquals(1, dataPacket.data.length);
		Assert.assertArrayEquals(new byte[] { 12 }, dataPacket.data);

	}

	@Test
	public void TestConstructorBoolean() {
		PacketData data = new PacketData((byte) 2, true, true);
		Assert.assertEquals(2, data.perifiral);
		Assert.assertEquals(true, data.write);
		Assert.assertEquals(1, data.data.length);
		Assert.assertNotEquals(0, data.data[0]);

		data = new PacketData((byte) 2, true, false);
		Assert.assertEquals(2, data.perifiral);
		Assert.assertEquals(true, data.write);
		Assert.assertEquals(1, data.data.length);
		Assert.assertEquals(0, data.data[0]);
	}
}
