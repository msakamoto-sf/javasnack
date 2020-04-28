## TODO

- Effective Java からジェネリクス部分についての各項目をテストコードで表現
  - https://www.informit.com/articles/article.aspx?p=2861454
  - item 29 : ジェネリクスを使った簡単なStackクラス実装例
  - item 30 : identityFunction() -> emptyList()/emptyMap() で実装してみる。`public static <E extends Comparable<E>> E max(Collection<E> c) {` のサンプルも。
  - item 31 : PECS 原則を使った Stack の改善。Chhoser, union, max, swap を使った細かい例示。
  - item 32 : 可変長引数とジェネリクスの分かりやすい解説
  - item 33 : `Class<?>.cast()` を使った、classをキーとするtypesafeなコンテナの例。
- JMXを使って自分自身のメモリ使用量はCPU使用率・実行スレッド数をセルフチェックしてみる。
- CyclicBarrierもう一度。(前回テストコード書いたときはよく分からなかった・・・かも？？)
  - https://qiita.com/nogitsune413/items/ec0132c306e1f15c6f87
  - https://nowokay.hatenablog.com/entry/20081128/1227840634
  - https://www.eeb.co.jp/wordpress/?p=282
- SpotBugs 再調整(一時期、Eclipseプラグインがうまく動かなかったので無効化してたうちに溜まったwarning対応他)
