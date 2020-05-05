# RE2/J のデモ

- google/re2j: linear time regular expression matching in Java
  - https://github.com/google/re2j
- `遅いッ！遅すぎるッ！Java の正規表現のお話。 - Cybozu Inside Out | サイボウズエンジニアのブログ`
  - https://blog.cybozu.io/entry/8757

`java.util.regex` パッケージとの違い:

- `Pattern.UNIX_LINES`, `Pattern.COMMENTS`, `Pattern.CANON_EQ`, `Pattern.LITERAL` などのフラグが使えない。(一部使えるものもある)
  - `TestRe2jBasicFlagsAndMatcherReplaceDemo.testPatternCompileAndFlags()` 参照
- `(?...)` フラグで指定したものを `Pattern#flags()` で取り出せない。
  - `TestRe2jBasicFlagsAndMatcherReplaceDemo.testPatternCompileAndFlags()` 参照
- `UNIX_LINES`フラグが無い影響で `\n` しか改行として扱われない。= MULTILINEモードが `\r\n`, `\r`, その他改行文字で動かなくなる。
  - `TestRe2jBasicFlagsAndMatcherReplaceDemo.testUnixLineFlag()` 参照
- `Pattern#quote()` が `\Q-\E` ではなく `\` でエスケープする。
  - `TestRe2jBasicEscapeQuoteDemo.quoteDemo()` 参照
- 後方参照(backreference) をサポートしていない。
  - `TestRe2jBasicFlagsAndMatcherReplaceDemo.testGroupMatch7()` 参照
- Atomic Group  をサポートしていない。
  - `TestRe2jAtomicGroupNotSupport` 参照
- 強欲な数量子(possessive quantifier) をサポートしていない。
  - `TestRe2jQuantifiersDemo.testRe2jNotSupportPossesiveQuantifier()` 参照
- 肯定/否定 先読み/戻り読み をサポートしていない。
  - `TestRe2jLookAheadBehindNotSupport` 参照

