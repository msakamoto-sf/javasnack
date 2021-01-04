# Java含む正規表現関連の参考メモ

JavaDoc リファレンス:

- english
  - https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html
  - https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/regex/Pattern.html
- japanese
  - https://docs.oracle.com/javase/jp/8/docs/api/java/util/regex/Pattern.html
  - https://docs.oracle.com/javase/jp/11/docs/api/java.base/java/util/regex/Pattern.html

正規表現全般:

- `Java正規表現の使い方`
  - https://www.javadrive.jp/start/regex/
- `Regular-Expressions.info - Regex Tutorial, Examples and Reference - Regexp Patterns`
  - https://www.regular-expressions.info/

## 正規表現のテスト・可視化ツール

オンラインでJava/PCRE/JavaScriptなどの正規表現をテストできるサイト:

- `Java regex tester`
  - http://java-regex-tester.appspot.com/
- `RegexPlanet: online regular expression testing for Java`
  - http://www.regexplanet.com/advanced/java/index.html
- `Online regex tester and debugger: PHP, PCRE, Python, Golang and JavaScript`
  - https://regex101.com/
  - Javaへの対応は明記されていないが、JavaはPCRE互換のため、PHP/PCREなどである程度カバーできる。

オンラインでJava/PCRE/JavaScriptなどの正規表現をデバッグ(状態遷移の可視化)ができるサイト:

- `Debuggex: Online visual regex tester. JavaScript, Python, and PCRE.`
  - https://www.debuggex.com/
  - JavaScript/Python/PCRE 対応
- `Regexper` (JavaScriptのみ)
  - https://regexper.com/
- `i Hate Regex - The Regex Cheat Sheet`
  - https://ihateregex.io/

## 最短/最長/強欲マッチ, バックトラック, パフォーマンス関連

キーワード:

- 最長一致 数量子(quantifier): `X?`, `X*`, `X+`
  - longest match / greedy / greediness
- 最短一致 数量子(quantifier): `X??`, `X*?`, `X+?`
  - shortest match / lazy / laziness / ungreedy / reluctant
- 強欲な(possessive) 数量子(quantifier): `X?+`, `X*+`, `X++`
- 違いの一覧
  - https://www.regular-expressions.info/refrepeat.html

パフォーマンスを意識した最短/最長/強欲の使い分けとバックトラック関連:

- `欲張り正規表現 - Atzy-&gt;getLog()`
  - https://atzy.hatenadiary.org/entry/20110309/p1
- `遅いッ！遅すぎるッ！Java の正規表現のお話。 - Cybozu Inside Out | サイボウズエンジニアのブログ`
  - https://blog.cybozu.io/entry/8757
- `最長一致数量子/最短一致数量子/強欲な数量子 - 任意の文字と繰り返し(量指定子) - Java正規表現の使い方`
  -  https://www.javadrive.jp/start/regex/repeat/index2.html
- `正規表現のパフォーマンスの話をされても全くピンと来なかった僕は、backtrackに出会いました。 - Qiita`
  -  https://qiita.com/mochizukikotaro/items/d36e61e56220da5f95d1
- `最短一致・最長一致・独占的量指定子つき最長一致のベンチマーク - Qiita`
  -  https://qiita.com/mpyw/items/5247207ba718ded353b2
- `パフォーマンスを意識して正規表現を書く - Shin x Blog`
  -  https://blog.shin1x1.com/entry/regex-performance
- `はじめての正規表現とベストプラクティス#9: '.*'や'.+'がバックトラックで不利な理由`
  -  https://techracho.bpsinc.jp/hachi8833/2019_04_19/73216
- `Five Invaluable Techniques to Improve Regex Performance`
  -  https://www.loggly.com/blog/five-invaluable-techniques-to-improve-regex-performance/

バックトラックが発生する仕組みと、最短/最長での違いが分かりやすく解説されている記事:

- `regex - How to avoid (linear) back tracking in a non-greedy regular expression? - Stack Overflow`
  -  https://stackoverflow.com/questions/39075946/how-to-avoid-linear-back-tracking-in-a-non-greedy-regular-expression
- `Flagrant Badassery ≫ Performance of Greedy vs. Lazy Regex Quantifiers`
  -  http://blog.stevenlevithan.com/archives/greedy-lazy-performance
- `java - Can I improve performance of this regular expression further - Stack Overflow`
  -  https://stackoverflow.com/questions/33869557/can-i-improve-performance-of-this-regular-expression-further/33869801

パフォーマンスを考慮して最短一致を使うときの参考資料

- `最長一致数量子/最短一致数量子/強欲な数量子 - 任意の文字と繰り返し(量指定子) - Java正規表現の使い方`
  -  https://www.javadrive.jp/start/regex/repeat/index2.html
- `正規表現のパフォーマンスの話をされても全くピンと来なかった僕は、backtrackに出会いました。 - Qiita`
  -  https://qiita.com/mochizukikotaro/items/d36e61e56220da5f95d1
- `最短一致・最長一致・独占的量指定子つき最長一致のベンチマーク - Qiita`
  -  https://qiita.com/mpyw/items/5247207ba718ded353b2
- `パフォーマンスを意識して正規表現を書く - Shin x Blog`
  -  https://blog.shin1x1.com/entry/regex-performance
- `はじめての正規表現とベストプラクティス#9: `.*`や`.+`がバックトラックで不利な理由｜TechRacho（テックラッチョ）〜エンジニアの「？」を「！」に〜｜BPS株式会社`
  -  https://techracho.bpsinc.jp/hachi8833/2019_04_19/73216

## NFA, DFA, オートマトン関連資料

- `決定性と非決定性（DFA, NFA）`
  - http://basicwerk.com/blog/archives/1511
- `オートマトンと言語理論, 明治大, 水谷, 2019年`
  - https://www.isc.meiji.ac.jp/~mizutani/cs/automata/automatonbook.pdf
- `オートマトンと言語理論, 成蹊大, 山本, 2019年`
  - https://www.ci.seikei.ac.jp/yamamoto/lecture/automaton/text.pdf
- `正規表現エンジンを作ろう連載一覧：CodeZine（コードジン）`
  - https://codezine.jp/article/corner/237
- `今日は Regex Festa の日です - 北海道苫小牧市出身の初老PGが書くブログ`
  - https://hiratara.hatenadiary.jp/entry/2019/10/18/182013

## 正規表現DoS (ReDoS) 関連資料

- `[Z-Ⅲ] ReDoSの検出プログラムの作成とOSSへの適用`
  - https://www.slideshare.net/ssuser9ef9aa/redos
  - https://www.slideshare.net/ssuser9ef9aa/redososs
- `その正規表現の書き方で大丈夫？ ReDoS 攻撃の怖さと対策方法 | yamory Blog`
  - https://yamory.io/blog/about-redos-attack/
- `^ReDoSの色々$ - Speaker Deck`
  - https://speakerdeck.com/lhazy/redosfalsese
- `正規表現入門　星の高さを求めて`
  - https://www.slideshare.net/sinya8282/ss-32629428
- `The Regular Expression Denial of Service (ReDoS) cheat-sheet | by James Davis | Level Up Coding`
  - https://levelup.gitconnected.com/the-regular-expression-denial-of-service-redos-cheat-sheet-a78d0ed7d865
- `正規表現とセキュリティ / Regular Expressions and Their Security-Related Aspects - Speaker Deck`
  - https://speakerdeck.com/lmt_swallow/regular-expressions-and-their-security-related-aspects
- `Regular expression Denial of Service - ReDoS | OWASP`
  - https://owasp.org/www-community/attacks/Regular_expression_Denial_of_Service_-_ReDoS
- `Preventing Regular Expression Denial of Service (ReDoS)`
  -  https://www.regular-expressions.info/redos.html
- `正規表現の落とし穴（ReDoS - Regular Expressions DoS） - Qiita`
  - https://qiita.com/prograti/items/9b54cf82a08302a5d2c7
- `正規表現を使ったDoS    ReDoS    yohgaki's blog`
  - https://blog.ohgaki.net/regex-dos-redos

チェックツール:

- `GitHub - jagracey/RegEx-DoS: RegEx Denial of Service (ReDos) Scanner`
  - https://github.com/jagracey/RegEx-DoS
- `GitHub - substack/safe-regex: detect possibly catastrophic, exponential-time regular expressions`
  - https://github.com/substack/safe-regex
- `gagyibenedek/ReDoS-checker: Check your regex for ReDoS vulnerability.`
  - https://github.com/gagyibenedek/ReDoS-checker
- `ReScue | A tool to detect ReDoS.`
  - https://2bdenny.github.io/ReScue/
- https://owasp.org/www-community/attacks/Regular_expression_Denial_of_Service_-_ReDoS
- `How can I recognize an evil regex? - Stack Overflow`
  - https://stackoverflow.com/questions/12841970/how-can-i-recognize-an-evil-regex


