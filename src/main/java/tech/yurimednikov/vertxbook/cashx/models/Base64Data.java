package tech.yurimednikov.vertxbook.cashx.models;

public final class Base64Data {
    
    private final byte[] bytes;

    public Base64Data(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    
}
