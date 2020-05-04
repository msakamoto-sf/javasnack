## Java ジェネリクス関連勉強用テストコード集

用語:
- covariance
  - 共変
  - `Parent parent = instanceOfChild;` : Javaの参照型全般でこれが成立
- contravariance
  - 反変
  - `Child child = instanceOfParent;` : Javaの参照型全般でこれは不成立, ただし下限付き境界ワイルドカード型では例外的に成立するケースあり。
- invariant or nonvariant
  - 非変
  - `List<Child> children = (other List<Child> instance)` : Javaのジェネリック型についてはこれが基本。
- generic type
  - ジェネリック型
  - `List<E>`
- type parameter, formal type parameter
  - 仮型パラメータ or 型変数
  - `<T>` の `T`
- parameterized type
  - パラメータ化された型
  - `List<String>`
- actual type parameter
  - 実型パラメータ
  - `List<String>` の `String`
- raw type
  - 原型
  - `List<String>` の `List`
- bounded type parameter
  - 境界型パラメータ
  - `<E extends Number>`
- unbounded wildcard type
  - 非境界ワイルドカード型
  - `List<?>`
- (upper) bounded wildcard type
  - (上限付き) 境界ワイルドカード型 or 共変ワイルドカード
  - `List<? extends Number>`
- (lower) bounded wildcard type
  - 下限付き境界ワイルドカード型 or 反変ワイルドカード
  - `List<? super Number>`
- generic method
  - `static <E> List<E> asList(E[] a)`
- recursive type bound
  - `<T extends Comparable<T>>`
- type token
  - `String.class`
  - 参考 : https://docs.oracle.com/javase/tutorial/extra/generics/literals.html
- reifieable (or reified) type
  - reify : 「具象化する」「具体化する」という動詞。
  - 具象化可能型 : 実行時に具体的な型として扱えるもの。ジェネリクスを使っていないクラス/インターフェイスや原型、プリミティブ型、それらの配列など。
  - 反対に、ジェネリクスを使った方は non-reifiable (or non-reified) type と呼ばれ、「具象化不可能型」「非具象化可能型」「非具象化可能仮パラメータ」などの日本語訳が確認された。
  - non-reifieable type はジェネリクス型が主に対象となり、コンパイルされるとイレイジャにより実型パラメータがコードから除去される(クラス情報には残り、リフレクションで取り出すことはできる)ため、実行時に具体的な型を定めることができず、原型(raw type)で扱うことになる。これはジェネリクス導入前のコードとの互換性維持を目的としている。
  - reifiable / non-reifiable については特に可変長引数とジェネリクスを組み合わせたときの heap pollution で重要となってくる。本ページの後ろに関連URLをまとめたので、そちらも参照のこと。

Java ジェネリクス勉強用参考資料:

- `Javaジェネリクス再入門 - プログラマーの脳みそ`
  - https://nagise.hatenablog.jp/entry/20101105/1288938415
- `JJUG CCC 2013 Fall でジェネリクスのセッションやりました - プログラマーの脳みそ`
  - https://nagise.hatenablog.jp/entry/20131111/1384168238
- `Java Generics Hell Advent Calendar 2017 - Adventar`
  - https://adventar.org/calendars/2751
- `Java ジェネリクスのポイント - Qiita`
  - https://qiita.com/pebblip/items/1206f866980f2ff91e77
- `Java総称型メモ(Hishidama's Java Generics Memo)`
  - https://www.ne.jp/asahi/hishidama/home/tech/java/generics.html
- `Lesson: Generics (Updated) (The Java™ Tutorials > Learning the Java Language)`
  - https://docs.oracle.com/javase/tutorial/java/generics/index.html
- `AngelikaLanger.com - Java Generics FAQs - Frequently Asked Questions - Angelika Langer Training/Consulting`
  - http://www.angelikalanger.com/GenericsFAQ/JavaGenericsFAQ.html
- `generics - Difference between <? super T> and <? extends T> in Java - Stack Overflow`
  - https://stackoverflow.com/questions/4343202/difference-between-super-t-and-extends-t-in-java
  - 境界ワイルドカード型のupper/lower boundedの使い分けについてはPECS原則というのがあり、それについて良くまとめられている。

reifiable type / non-reifiable type 参考資料:

- `Chapter 4. Types, Values, and Variables > 4.7. Reifiable Types`
  - https://docs.oracle.com/javase/specs/jls/se14/html/jls-4.html#jls-4.7
- `Non-Reifiable Types (The Java™ Tutorials > Learning the Java Language > Generics (Updated))`
  - https://docs.oracle.com/javase/tutorial/java/generics/nonReifiableVarargsType.html
- `非具象化可能仮パラメータを可変長引数メソッドに使用する場合のコンパイラの警告の改善`
  - https://docs.oracle.com/javase/jp/8/docs/technotes/guides/language/non-reifiable-varargs.html
- `java - What are Reified Generics? How do they solve Type Erasure problems and why can't they be added without major changes? - Stack Overflow`
  - https://stackoverflow.com/questions/879855/what-are-reified-generics-how-do-they-solve-type-erasure-problems-and-why-cant
- `【Effective Java】項目２５：配列よりリストを選ぶ - The King's Museum`
  - https://www.thekingsmuseum.info/entry/2016/02/11/111718
- `Javaのinstanceofについて言語仕様を見てみる - nFact`
  - https://nokok.hatenablog.com/entry/2019/01/14/024439

reifiable type となる条件: 以下の条件のいずれかに当てはまる型。

- ジェネリクス型以外のクラスやインターフェイス
- すべての型引数が非境界ワイルドカード型(unbounded wildcard type)となっているパラメータ化された型(parameterized type)
- 原型(raw type)
- プリミティブ型
- 要素の型が具象化可能型となっている配列型
- ネストされた型の場合は、"." で区切られたそれぞれの型が具象化可能型である

