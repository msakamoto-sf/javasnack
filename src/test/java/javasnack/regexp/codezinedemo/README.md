# CodeZine連載「正規表現エンジンを作ろう」をJavaで挑戦

以下の連載において Python で書かれている正規表現エンジンを、Javaで書くのに挑戦。

- `正規表現エンジンを作ろう連載一覧：CodeZine（コードジン）`
  - https://codezine.jp/article/corner/237

Javaで書いた場合、命名規約やコーディングの慣習などでPython版から大分変更となったため、一旦著作権は自身のものとしてライセンスも javasnack 全体のAPL2にしています。問題あればご連絡ください。

以下、Javaで書いた場合の作業ログおよびその他補足メモ：

## 連載記事(1)

初回記事なので、正規表現の概要解説と有限オートマトンの導入説明。

ポイント: 正規表現は、文字入力をイベントとした有限オートマトンのモデルで考えることができる。文字による状態遷移で最終的に「受理状態」となれば、マッチしたと考える。

- 有限オートマトン (Finite Automaton)
  - https://ja.wikipedia.org/wiki/%E6%9C%89%E9%99%90%E3%82%AA%E3%83%BC%E3%83%88%E3%83%9E%E3%83%88%E3%83%B3
- 非決定性オートマトン (Nondeterministic Finite Automaton : NFA)
  - ある入力文字に対して、遷移先(矢印)が複数存在する。
  - 空文字(ε)での遷移を許可している。
  - このため、ある文字によりどの状態に遷移するか、あるいは空文字として遷移するか、複数の選択肢が発生することとなり、どれになるか決定できない。複数の選択肢をそれぞれ試す総当りが必要となる。
- 決定性オートマトン (Deterministic Finite Automaton : DFA)
  - 非決定性が無いオートマトン
  - 全ての入力文字に対して、必ず矢印が1つだけ存在する。= 1つの文字で、複数の状態のどれかに遷移する、ということは無い。また、文字空間全てについてそれぞれただ1つの遷移先が存在する。
  - 空文字(ε)での遷移も許可していない。
- 前方一致
  - この連載記事においては、マッチングを文字列の先頭から1文字ずつ受けて判定していく。
  - 全文一致の場合は最後まで入力して「受理状態」となることがマッチ条件だが、前方一致の場合は途中で「受理状態」となればそこで切り上げてOK.
- 後方一致
  - この連載記事においては、マッチ対象の文字列を1文字ずつ後方に短くしていきながら判定する。
  - `cat|rat` が `autocrat` にマッチするケースを考えると、後方一致では `autocrat`, `utocrat`, `tocrat`, ... と短くしていき、最終的に `rat` でマッチする。


## 連載記事(2)

元記事URL : https://codezine.jp/article/detail/3154

連載記事の主目的であるDFA正規表現エンジンの開発に着手する。
実装予定の正規表現としては以下の文法をサポート予定。

- `A|B` : A or B (和集合)
- `AB` : 文字列AB (連結)
- `A*` : Aの0個以上の繰り返し
- `(...)` : 演算の優先順位
- `バックスラッシュ + 任意文字` : 1文字の任意文字(エスケープ)

内部的にはまずNFAとして正規表現を解析し、それをDFAに変換する方式としている。
そのため実装の流れは以下のようになる。

1. NFAとDFAの実装
2. 正規表現のコンパイル (構文木の作成)
3. 構文木からNFAを作成
4. NFAからDFAへの変換
5. 全てを組み合わせて完成

### NFAの内部表現を Java で実装

NFAの数学的な定義:

1. 状態の集合（丸）
   - 遷移関数の戻り値 + 開始状態 + 受理状態の集合 となるので、それらを定義すれば自動的に定まる。
2. 文字の集合（Unicode文字）
   - Java での `Character` 型を利用する。
3. 遷移関数（矢印）
   - 引数: 今の状態, 文字 **or 空文字（ε）**
   - 戻り値: 次の状態 **の集合**
4. 開始状態（最初の矢印がさす丸）
5. 受理状態の集合（二重丸）

ここで「状態」をどう実装するか？
NFAを表現する図を見ていくと、状態（丸）の中に数字が入っており、状態1, 2, 3, ... として表現している。
元の連載記事ではPython 2系ということもあり、型が明示されていないものの、サンプルコードからそのまま整数値として実装していることが伺える。
よって、Javaで実装するときもそのまま整数値(int, Integer)として実装してみる。
状態を整数値として表現するので、遷移関数とその引数もおおよそ以下のようにJavaで表現できる。

```java
int someStateTransitionFunction(int currentState, char inputCharacter) {
}
```

ただし、今考えているのはNFAにおける遷移関数であるため、戻り値 = 遷移先の状態は複数を取りうる。
元の連載記事では Python の frozenset を使ってこのような「状態の集合」を表現している。
「集合の集合」を扱いやすいことを理由として、setではなくfrozensetを使っている。
Javaにおいては、現段階では `Set<Integer>` としておけばまずは無難と思われる。

さらにもう一点、NFAでは空文字列(ε)も扱える必要がある。
これを表現するため、入力文字を `java.util.Optional<Character>` にする。
したがって、以下のメソッドがNFAにおける遷移関数の Java 版となる。

```java
Set<Integer> someNfaStateTransitionFunction(int currentState, Optional<Character>) {
}
```

これをJava8の関数型インターフェイスで表現し、ソースコードを読みやすくする。

```java
@FunctionalInterface
public interface NfaStateTransitFunction {
    Set<Integer> apply(int currentState, Optional<Character> inputCharacter);
}
```

連載の元記事のPythonコードを見ると、「イベント x 状態遷移」の組み合わせはこの遷移関数に全て押し込める形になっている。
よってNFA全体を表現するオブジェクトとしては、遷移関数 + 初期状態 + 受理状態の集合、の3つのパラメータを保持する。
遷移関数について Java 8 の関数型インターフェイスを活用したのが、以下のJava版 NFA オブジェクトとなる。
(以下、特に断りなく lombok のアノテーションを使用する)

```java
@ToString
@EqualsAndHashCode
@AllArgsConstructor(staticName = "of")
public class Nfa {
    /** transition function */
    public final NfaStateTransitFunction transition;
    /** starting state */
    public final int start;
    /** set of acceptable states */
    public final Set<Integer> accept;
}
```

このクラスは結局何を表現するかというと、特定の正規表現に対応した状態遷移の「設計図」を提供する。
メソッドが何も定義されていないことからも分かる通り、これは単に設計図でしかなく、実際に入力文字を与えて状態遷移を管理する実行エンジンが別に必要となり、連載記事(3) 以降でそれが紹介される。

### DFAの内部表現を Java で実装

DFAの数学的な定義:

1. 状態の集合（丸）
2. 文字の集合（Unicode文字）
3. 遷移関数（矢印）
   - 引数: 今の状態, 文字
   - 戻り値: 次の状態
4. 開始状態（最初の矢印がさす丸）
5. 受理状態の集合（二重丸）

NFAとほぼ同じで、異なるのは遷移関数の引数と戻り値になる。
DFAにおいては引数の文字は空文字を扱わない。
また、戻り値についても次の状態は常に1つに定めるのがDFAの定義なので、集合ではなく単一の値を返すことになる。
したがってDFAの遷移関数をJavaで表現すると、以下のようなメソッドになる。

```java
int someDfaStateTransitionFunction(int currentState, char inputCharacter) {
}
```

これをJava8の関数型インターフェイスで表現し、ソースコードを読みやすくする。

```java
@FunctionalInterface
public interface DfaStateTransitFunction {
    int apply(int currentState, char inputCharacter);
}
```

DFA全体を表現する Java オブジェクトは、NFAと同様に以下のようになり、遷移関数の定義だけが異なる。

```java
@ToString
@EqualsAndHashCode
@AllArgsConstructor(staticName = "of")
public class Dfa {
    /** transition function */
    public final DfaStateTransitFunction transition;
    /** starting state */
    public final int start;
    /** set of acceptable states */
    public final Set<Integer> accept;
}
```

このクラスもNFAと同様、特定の正規表現に対応した状態遷移の「設計図」を提供する。
したがって実際に入力文字を与えて状態遷移を管理する実行エンジンが別に必要となり、そのランタイムを続けて実装していく。

### DFAのシミュレーション: DfaRuntime

DFAについて、実際に入力文字列から1文字ずつ与えて状態を変化させ、最終的に受理状態となる = マッチしたかを判定するランタイム: DfaRuntime クラスを作る。
元の連載記事でのPythonサンプルコードを元に、Java版を組み上げてみる。

ランタイムが必要とする情報は、DFAの設計図である。
管理する情報としては現在状態となるので、以下のような field + constructor を組める。

```java
public class DfaRuntime {
    private final Dfa dfa;
    private int currentState;

    public DfaRuntime(final Dfa dfa) {
        this.dfa = dfa;
        this.currentState = dfa.start;
    }
}
```

続けて1文字を入力し、状態遷移関数を呼び出して次の状態を取得、更新するメソッドを用意する。
これは詳細な実装に係る内部向けの処理なので private とし、公開用には別途扱いやすいAPIを用意する(後述)

```java
private void transit(final char c) {
    this.currentState = dfa.transition.apply(Integer.valueOf(currentState), Character.valueOf(c));
}
```

現在の状態が受理状態に含まれるか判定するメソッドを用意する。

```java
private boolean isCurrentStatusAcceptable() {
    return this.dfa.accept.contains(Integer.valueOf(currentState));
}
```

最後に、入力文字列をまとめて受け取り1文字ずつ transit() させて結果を呼び出すショートカット用公開APIを用意する。

```java
public boolean accept(final String input) {
    input.codePoints().mapToObj(c -> (char) c).forEach(c -> this.transit(c));
    return this.isCurrentStatusAcceptable();
}
```

## 連載記事(3)

元記事URL : https://codezine.jp/article/detail/3158

(TODO)

## 参考資料

記事中で参照されている参考資料からショートカットとして抜粋、および自身で探して見つかった資料集。

連載記事(1) より

- `計算理論の基礎`, Michael Sipser 著、渡辺治・太田和夫 訳、共立出版、2000年4月
  - https://www.amazon.co.jp/exec/obidos/ASIN/4320029488/secodezine-22/
  - 原著は `Introduction to the Theory of Computation`, https://www.amazon.co.jp/dp/113318779X
- `Regular Expression Matching Can Be Simple And Fast`, Russ Cox, January 2007
  - https://swtch.com/~rsc/regexp/regexp1.html

連載記事(3) より

- `Compilers Principles, Techniques, & Tools`, Alfred V. Aho Monica S. Lam Ravi Sethi Jeffrey D. Ullman 著、Addison-Wesley、2007年10月
  - https://www.amazon.co.jp/exec/obidos/ASIN/0321547985/secodezine-22/





