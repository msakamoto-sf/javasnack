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

