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

## 連載記事(3), (4)

元記事URL:

- (3) : 正規表現のコンパイラの作成
  - https://codezine.jp/article/detail/3158
- (4) : コンパイラが生成した構文木からNFAを作る
  - https://codezine.jp/article/detail/3164

元記事では最初にコンパイラを作成し、次にコンパイラが生成した構文木からNFAを作る流れで解説している。
これは、以下の処理の流れをその順番通りに作っていく解説となっている。

1. 字句解析(lexer)
2. 構文解析(parser) -> 構文木生成(node)
3. InterpreterパターンによるNFAの生成 (Interpreter パターンについては参考資料[4] - [6] 参照)

自分の場合、コンパイラ関連の技術に疎いこともあり、この流れではうまく理解できなかった。
というのも、特に構文解析とNFAの生成がお互いに強く関連していて、構文解析のコードがNFAを生成するInterpreterパターンに強く依存している。
つまり元の連載記事(3)のサンプルコードを理解するには、その先の(4)のサンプルコードを理解する必要がある。
その順番に気づかずに (3) を読んでいると、(4)に登場するクラス = その時点では未解説のクラスがいきなり登場してパニックに陥る。

そこでこちらの作業メモでは、自分と同様に混乱した読者向けに、元記事とは異なる順序で解説を試みる。
文法や構文木など、概念的な部分を最初に押さえるのは元記事の流れ通りだが、その後、Interpreterパターンの実装を先に行い、依存コンポーネントが出来上がってから上に戻ってコンパイラを作成する流れとしている。

1. 実装する正規表現の機能と文法を考える。
2. 文法をもとに必要となる構文木を考える。
3. その構文木を元に動的に生成可能なNFAの表現方法を考え、実装する。
4. NFAの実装を利用した、構文木処理(lexer + parser)を実装する。

### 実装する正規表現の機能と文法

元の連載記事 (2) にある通り以下の機能を実装する。(再掲)

- `A|B` : A or B (和集合)
- `AB` : 文字列AB (連結)
- `A*` : Aの0個以上の繰り返し
- `(...)` : 演算の優先順位
- `バックスラッシュ + 任意文字` : 1文字の任意文字(エスケープ)

これをどう文法規則に落とし込むか。
元の連載記事(2), (3)から推測するに、以下の流れで組み立てていったものと思われる。

1. `A|B` の左右の要素は、そのまま正規表現の入れ子になる。つまりこれが上位の文法規則になる。
2. `AB` は文字列の連結というより、正規表現の連結と考える。
3. `A*` は文字の繰り返しというより、正規表現の繰り返しと考える。
4. `(...)` の中身は正規表現または文字となる。 -> これを一番底となる要素における。
5. `(...)` を底になる要素と置けば、また通常の1文字も底となる要素にできる。 
   - `(` + 正規表現 + `)` または1文字、という文法規則にできる。
   - -> `factor -> "(" subexpr ")" | CHARACTER`
6. 続いて文法規則の中で単一の正規表現要素しか扱っていないのは `A*` なので、これを2番底の文法規則にできる。これは `A` だけも表現可能できる。
   - -> `star -> factor "*" | factor`
7. `AB` を考えると、これは `star` の連結と考えることができる。`star star`, `star star star`, `star star star star ...` と続くことを考えると、最初の `star` に続いて正規表現の連結が再帰的に配置される文法規則ですっきり整理できる。(この辺の詳細は元記事(3) 参照)
   - -> `subseq -> star subseq | star`
8. 最後の `A|B` については "正規表現1 | 正規表現2" または "正規表現" と考えれば、上位の文法規則として配置できる。また、ここまで空文字列を扱ってこなかったので、ここで空文字列も導入する。 `subseq` の上に、`subseq` または空文字 という文法規則 (`seq`) を置き、 `|` で `seq` を連結する。ただし `|` の右側は空文字列を許容しないので、 `subseq` とする。
   - -> `seq -> subseq | ''`
   - -> `subexpr -> seq '|' subexpr | seq`
   - -> `expression -> subexpr EOF`

上位から順に並べ押したのが以下の文法規則となる。

```
(A) expression -> subexpr EOF
(B) subexpr -> seq '|' subexpr | seq
(C) seq -> subseq | ''
(D) subseq -> star subseq | star
(E) star -> factor '*' | factor
(F) factor -> '(' subexpr ')' | CHARACTER
```

### 必要な構文木の検討

文法規則を元に、構文木で必要となるnodeを検討する。

1. `factor` で必要となるのが、一文字を表現するnodeであり、これが全ての基本になる。
   - 空文字(ε)もこれで表す。
2. 繰り返し(`star`) を表現するnodeが必要。
   - `a*` などの文字の繰り返しに加え、`(a|b)*` など正規表現自体の繰り返しも表現する、再帰的なnodeになる。
3. 連結(`seq`, `subseq`)を表現するnodeが必要。
   - `abc` などの文字列(= 文字の連結)に加え、`a*b` や `(a|b)c` など正規表現同士の連結も表現する、再帰的なnodeになる。
4. 最後にOR(`subexpr`)を表現するnodeが必要。
   - `a|b` などの文字のORに加え、`(a|b)|(c*)` など正規表現自体のORも表現する、再帰的なnodeになる。

一文字を表現するnode以外は全て再帰的なnodeになる。

### 構文木から生成可能なNFAの表現方法と実装

再帰的なnodeで構成されたツリー構造から何かしらのデータを構築する方法として、元の連載記事(4)では Interpreter パターンを採用している。
Interpreterパターンではツリー構造を再帰的に辿り、それぞれのnodeでのアクションを実行していく。
アクションの中で、何かしらのデータを構築し、それを永続化する。
再帰的にアクションが積み重なっていくことで、最終的なデータが完成する。

今回構築したい「何かしらのデータ」はNFAの設計図(前掲の `Nfa` クラス)となるが、ではどうすればNFAの状態遷移を Interpreter パターンのアクションの積み重ねで構築していくか？

答えは既に元の連載記事(4)で示されているものの、最初に一読したときは「なぜこれで表現できるのか？」がスッと頭に入ってこなかった。
そこで、こちらの作業ログでは元の連載記事(4)で提示されているコードについて、自分なりの解説を加えることで理解を深めてみる。

#### 遷移関数をmapとして表現することの検討

NFAの構成要素:

1. 初期状態
2. 遷移関数 (状態, 入力文字) -> 遷移先の状態の集合
3. 受理可能状態の集合

遷移関数を「データ」で表現しようとするとどうなるか？
シンプルに考えれば、パラメータに対する戻り値のmap構造で表現できる。
よって Interpreter パターンではこのmap構造を生成することがメインターゲットになる。

例えば `a(b|c)` に対応するNFAは、遷移関数のmap構造として以下が考えられる:

| 現在状態 | 入力文字 | 遷移先の状態の集合 |
| ------- | --------| ------------------|
| S1      | 'a'      | [S2, S3]         |
| S2      | 'b'      | [S4]             |
| S3      | 'c'      | [S5]             |

この場合初期状態はS1, 受理可能状態は S4, S5 となる。

これを INterpreter パターンで再帰的に辿ることで構築することになる。
いきなり単一の巨大なmap構造を作る方法も考えられるが、元の連載記事 (4) では単一nodeに対応するNFAフラグメントを作成し、それを再帰的に結合していく方法を採用している。

例えば `ab` という文字連結の正規表現があるとする。
文法規則では `a` , `b` という2つの文字nodeと、それを連結したnodeのツリーに構成される。
これをそれぞれのnodeごとのNFAフラグメントとして表現したあと、それを連結してみる。

まず `a` の文字nodeに対応するNFAフラグメントを考えてみる。
これは状態S1に対して、`a` という入力文字で状態S2に遷移するNFAフラグメントとして表現できる。
(S1, S2 の具体的な中身についてはここでは考えない。前に作成した `Nfa` クラスに従うなら整数値となり、これについては後述する)

| 現在状態 | 入力文字 | 遷移先の状態の集合 |
| ------- | --------| ------------------|
| S1      | 'a'      | [S2]             |

`b` についても同様に考える。

| 現在状態 | 入力文字 | 遷移先の状態の集合 |
| ------- | --------| ------------------|
| S3      | 'b'      | [S4]             |

この2つのnode = NFAフラグメントを、連結によって単一のNFAフラグメントにマージすることを考える。
単純に遷移関数を結合しただけでは、S2 -> S3 への遷移が表現できない。
そこで、以下のように S2 -> S3 に空文字(ε)で遷移するようにする。

| 現在状態 | 入力文字 | 遷移先の状態の集合 |
| ------- | --------| ------------------|
| S1      | 'a'      | [S2]             |
| S2      | ε        | [S3]             |
| S3      | 'b'      | [S4]             |

結合後の全体のNFAとしては、初期状態は S1, 受理可能状態は S4 となる。

なお、上の例であれば `b` のNFAフラグメントを結合するさいに、`b` 入力に対応する現在状態 S3 を、`a` の遷移先状態である S2 に書き換える方法も考えられる。
上の例では受理可能状態と初期状態がそれぞれ単一のため書き換えもできなくは無いが、ORを表現するnodeや繰り返しを表現するnodeが混ざってくると処理が複雑化する。
よって、既存のNFAフラグメントのパラメータは置き換えずに、それらを空文字(ε)で結合する新たな遷移設定を追加する方法が、単純で実装しやすいものと思われる。

NFAフラグメント同士の「連結」をうまく表現できた。
元の連載記事(4)では、同様の考え方で「繰り返し」や「OR」についても表現している。
残るは「状態」をどう管理するか、となる。

#### 「状態」の管理

遷移関数の実体は入力をキーとして出力を値とするmap構造であり、入力キーはuniqueを保つ必要がある。
これを実現するため、`Nfa` クラスで採用した整数値としての状態は、連番として自動採番されるのが望ましい。
Interpreter パターンにおいて、再帰的にツリーを辿る中で処理全体で一貫して管理する必要のあるパラメータは `Context` という形で引き継いでいく。
元の連載記事では `Context` クラスに状態の自動採番機能をもたせ、各nodeで新規に状態を採番するときに使うようにしている。

#### 構文木に対応するnodeと Interpreter パターンの実装

ここまでの検討と理解の積み重ねにより、ようやく実装に進むことができる。

まず Interpreter パターンの再帰処理全体でパラメータを管理する `Context` クラスを作成し、それに状態の自動採番機能を実装する。

- [Context](./Context.java)

次に NFA フラグメントを表現する `NfaFragment` クラスを作成する。
これはいわば「編集可能な `Nfa` クラス」であり、遷移関数のmap構造を操作したり、map構造を結合して新たなNFAフラグメントを生成する機能を提供する。

- [NfaFragment](./NfaFragment.java)
  - `build()` メソッドにより、このNFAフラグメントを使った `Nfa` クラスのインスタンスを生成できるようにしている。

続いて構文木のnodeに対応するクラスを作成していくが、再帰的に辿るため、統一したinterfaceである `INodeAssembler` を作成し、これを実装することとする。

```java
public interface INodeAssembler {
    NfaFragment assemble(final Context context);
}
```

あるnodeが実装している `assemble()` を呼び出すと、そのnodeにぶら下がっているnodeの `assemble()` が呼び出され、さらにそれにぶら下がっている node の `assemble()` が呼び出され・・・というように再帰的に辿るイメージとなり、これが Interpreter パターンによる再帰処理を実現している。

以下が、元の連載記事(4)を参考に `INodeAssembler` を実装した各nodeクラスとなる。詳細な解説はソースコードを参照。

- [CharacterNode](./node/CharacterNode.java) : 1文字を表現するnode
- [ConcatNode](./node/ConcatNode.java) : 連結を表現するnode
- [StarNode](./node/StarNode.java) : 繰り返しを表現するnode
- [UnionNode](./node/UnionNode.java) : ORを表現するnode

ここまでで Interpreter パターンの処理で必要となる、構文木に対応する各nodeと必要なクラスを生成できた。
この後は、元の連載記事(3)に戻り字句解析と構文解析を実装する。
nodeのツリーが出来上がれば、Interperter パターンの再帰処理を呼び出すことで、最終的に1つの遷移関数に結合されたNFAが生成されることになる。

### コンパイラの作成

(TODO)

## 参考資料

記事中で参照されている参考資料からショートカットとして抜粋、および自身で探して見つかった資料集。

連載記事(1) より

- [1] `計算理論の基礎`, Michael Sipser 著、渡辺治・太田和夫 訳、共立出版、2000年4月
  - https://www.amazon.co.jp/exec/obidos/ASIN/4320029488/secodezine-22/
  - 原著は `Introduction to the Theory of Computation`, https://www.amazon.co.jp/dp/113318779X
- [2] `Regular Expression Matching Can Be Simple And Fast`, Russ Cox, January 2007
  - https://swtch.com/~rsc/regexp/regexp1.html

連載記事(3) より

- [3] `Compilers Principles, Techniques, & Tools`, Alfred V. Aho Monica S. Lam Ravi Sethi Jeffrey D. Ullman 著、Addison-Wesley、2007年10月
  - https://www.amazon.co.jp/exec/obidos/ASIN/0321547985/secodezine-22/

Interpreter パターン

- [4] `Interpreter パターン - Wikipedia`
  - https://ja.wikipedia.org/wiki/Interpreter_%E3%83%91%E3%82%BF%E3%83%BC%E3%83%B3
- [5] `デザインパターン ～Interpreter～ - Qiita`
  - https://qiita.com/i-tanaka730/items/adf5090cdbfd55cbc9b5
- [6] `23．Interpreter パターン | TECHSCORE(テックスコア)`
  - https://www.techscore.com/tech/DesignPattern/Interpreter.html/




