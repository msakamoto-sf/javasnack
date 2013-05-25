package javasnack.tool;

import java.nio.charset.Charset;

public interface CharsetTool {
    // nio's Charset canonical name short cuts
    public static final String BINARY = "ISO-8859-1";
    public static final String LATIN1 = "ISO-8859-1";
    public static final String SJIS = "Shift_JIS";
    public static final String MS932 = "windows-31j";
    public static final String EUCJP = "EUC-JP";
    public static final String ISO2022JP = "ISO-2022-JP";
    public static final String UTF8 = "UTF-8";

    // nio's Charset instance short cuts
    public static final Charset CS_BINARY = Charset.forName(BINARY);
    public static final Charset CS_LATIN1 = Charset.forName(LATIN1);
    public static final Charset CS_SJIS = Charset.forName(SJIS);
    public static final Charset CS_MS932 = Charset.forName(MS932);
    public static final Charset CS_EUCJP = Charset.forName(EUCJP);
    public static final Charset CS_ISO2022JP = Charset.forName(ISO2022JP);
    public static final Charset CS_UTF8 = Charset.forName(UTF8);
}
