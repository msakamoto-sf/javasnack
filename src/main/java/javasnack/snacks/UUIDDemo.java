package javasnack.snacks;

import java.nio.ByteBuffer;
import java.util.UUID;

import javasnack.tool.UnsignedByte;

public class UUIDDemo implements Runnable {

    static byte[] long2bytea(long src) {
        byte[] bytes = ByteBuffer.allocate(8).putLong(src).array();
        return bytes;
    }

    static void dumpuuid(UUID u) {
        /*
         * see: http://www.ietf.org/rfc/rfc4122.txt http://d.hatena.ne.jp/argius/20120607/1339082250
         * http://d.hatena.ne.jp/dayflower/20090306/1236314881
         */
        int version = u.version();
        System.out.print("version=[" + version + "] : ");
        switch (version) {
        case 1:
            System.out.println("Timestamp base");
            break;
        case 2:
            System.out.println("DCE Security base");
            break;
        case 3:
            System.out.println("Name(MD5) base");
            break;
        case 4:
            System.out.println("Random base");
            break;
        case 5:
            System.out.println("Name(SHA1) base");
            break;
        default:
            System.out.println("(unknown version)");
        }

        int variant = u.variant();
        System.out.print("variant=[" + variant + "] : ");
        switch (variant) {
        case 0:
            System.out.println("Reserved, NCS backward compatibility");
            break;
        case 2:
            System.out.println("RFC4122(Leach-Salz)");
            break;
        case 6:
            System.out
                    .println("Reserved, Microsoft Corporation backward compatibility");
            break;
        case 7:
            System.out.println("Reserved for future definition");
            break;
        default:
            System.out.println("(unknown variant)");
        }
        long MSB = u.getMostSignificantBits();
        long LSB = u.getLeastSignificantBits();
        byte[] MSB2 = long2bytea(MSB);
        byte[] LSB2 = long2bytea(LSB);
        System.out.println("MSB=[" + MSB + "/" + UnsignedByte.hex("", MSB2)
                + "]");
        System.out.println("(" + UnsignedByte.bits(MSB2) + ")");
        System.out.println("LSB=[" + LSB + "/" + UnsignedByte.hex("", LSB2)
                + "]");
        System.out.println("(" + UnsignedByte.bits(LSB2) + ")");

        if (1 == version) {
            System.out.println("UUID.timestamp=[" + u.timestamp() + "]");
            System.out
                    .println("UUID.clockSequence=[" + u.clockSequence() + "]");
            System.out.println("UUID.node=[" + u.node() + "]");
        }

    }

    @Override
    public void run() {
        UUID u1 = UUID.randomUUID();
        String u1str = u1.toString();
        System.out.println("UUID=[" + u1str + "]");
        UUID u1_2 = UUID.fromString(u1str);
        dumpuuid(u1_2);

        UUID u2 = UUID.nameUUIDFromBytes("abcdefg".getBytes());
        dumpuuid(u2);
    }
}
