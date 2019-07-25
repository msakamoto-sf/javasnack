package javasnack.junit;

import java.util.Arrays;

public class SomeObject {
    public int intval;
    public long longval;
    public String strval;
    public byte[] bytes;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(bytes);
        result = prime * result + intval;
        result = prime * result + (int) (longval ^ (longval >>> 32));
        result = prime * result + ((strval == null) ? 0 : strval.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SomeObject other = (SomeObject) obj;
        if (!Arrays.equals(bytes, other.bytes)) {
            return false;
        }
        if (intval != other.intval) {
            return false;
        }
        if (longval != other.longval) {
            return false;
        }
        if (strval == null) {
            if (other.strval != null) {
                return false;
            }
        } else if (!strval.equals(other.strval)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SomeObject [intval=" + intval + ", longval=" + longval + ", strval=" + strval + ", bytes="
                + Arrays.toString(bytes) + "]";
    }

}
