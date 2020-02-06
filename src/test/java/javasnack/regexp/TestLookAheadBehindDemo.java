package javasnack.regexp;

import org.junit.jupiter.api.Test;

/* reference:
 * - 正規表現（肯定先読み、否定先読み、肯定戻り読み、否定戻り読み） - satosystemsの日記
 *   - https://satosystems.hatenablog.com/entry/20100519/1274237784
 * - 正規表現の先読み・後読みを極める！ - あらびき日記
 *   - https://abicky.net/2010/05/30/135112/
 * - 先読みと後読み - Java正規表現の使い方
 *   - https://www.javadrive.jp/start/regex/lookahead/
 */
public class TestLookAheadBehindDemo {
    @Test
    public void testPositiveLookAheadDemo() {
        // Japanese : "肯定先読み" 
        // TODO (?=regexp)
    }

    @Test
    public void testNegativeLookAheadDemo() {
        // Japanese : "否定先読み" 
        // TODO (?!regexp)
    }

    @Test
    public void testPositiveLookBehindDemo() {
        // Japanese : "肯定後読み" or "肯定戻り読み" 
        // TODO (?<=regexp)
    }

    @Test
    public void testNegativeLookBehindDemo() {
        // Japanese : "否定後読み" or "否定戻り読み" 
        // TODO (?<!regexp)
    }
}
