# Java8 以降における関数型プログラミングパラダイム (FunctionInterface, Lambda, Stream) の勉強ノート

Java8 以降で追加された function, stream パッケージやラムダ記法は関数型プログラミングのパラダイムを意識している。
以下、そもそも関数型プログラミングのパラダイムとはどんなものか、調べたりしたときの調査メモ・勉強ノート。

注意 : 主に 2019 - 2020 にかけての独学に基づいているため、間違ってるところもあるかもです。

## ラムダ計算と関数型プログラミング

ラムダ計算が先で、関数型プログラミングが後。

1. https://en.wikipedia.org/wiki/Lambda_calculus
2. https://en.wikipedia.org/wiki/Functional_programming

2020-01 時点での理解:

- ラムダ計算は計算処理の原理的な部分を数学で扱えるように抽象化したもの、という理解。チューリング完全。
  - 入力を受け取り出力に変換する処理を「関数」として扱う。
  - 値には free な値と bind された値がある。
  - 複数の関数を順番に適用していくなかで、入力に登場する値がどんどん置き換わっていく(substitution)。
  - 関数の中身や適用順序を工夫 + 再帰を使うことで、四則演算やAND/OR/NOT演算、真偽値表現や条件分岐を実現できる。再帰があるのでループ処理も表現できる。
  - つまり、関数を組み合わせていくだけで立派な「プログラミング」が可能となる。
- 関数型プログラミングはラムダ計算を発展させて、他の派生アイデアとともに実際のプログラミング言語として実装していったパラダイム。
  - 実際に実装した言語としてはLISPが有名で、ここからいろいろ派生していった。
  - ラムダ計算に、実装で必要だったり便利なアイデアをいろいろ肉付けしていった感じ？
  - 全体的には「関数ブロックの組み合わせ」で表現するため、宣言的(declarative)な見え方になる。特にループ処理が再帰やそのラッパーで表現されることもあり、ソースコードを見ると数学の定義を読んでいるような印象も受ける。
  - 反対に、従来のパラダイムは「命令的(imperative)」とも呼ばれる。「これをしろ」という命令を順番に書いていくので、分かりやすいと言えば分かりやすい。

Wikipedia からざっくり要約してみた関数型プログラミングで重要となるアイデア:
- ファーストクラス + 高階関数 (first class and higher order function)
  - 値として持つことができる関数が first class function. 超雑に書くなら、関数ポインタのこと。関数への参照を変数に代入できたり、代入した変数経由で関数を呼び出せれば概ねそれは first class function.
  - 値として持てるのであれば、引数に指定したり、戻り値として返すこともできるはず。first class function を引数に取ったり戻り値にする関数の作りが、higher-order function.
  - OOPのパラダイムでも擬似的に扱えるし、関数ポインタとしてならC/マシン語の時代から使えたので、これは広く使える便利なアイデア。
- 副作用を持たない純粋関数 (pure fuunction or expression)
  - メモリの読み書きやI/Oを伴わないことを「副作用が無い」と呼んでるらしい。理論的にはスタックだけで完結するイメージ？
  - 実際のところメモリ読み書きは避けがたい。現実的には、少なくとも外部I/Oが無く、メモリのR/Wがあってもその影響が関数のスコープ内で完結している、というのが重要と思われる。
  - 「入力値だけで出力が定まるため、どの順番で入力しても、ある入力値については常に同じ値が出力される」特性と思われる。「呼び出し順序に依存性が無い」と言い換えても良いかも？
  - これがあると何が嬉しいかというと、主に最適化でメリットがあるらしい。
    - 使わないのであれば消すことができる。(他と影響しないので)
    - 入力値に対する出力値をキャッシュすることで、処理を効率化できる。-> メモ化, memoization。(ある入力値に対しては常に同じ結果のみが発生し、他との影響も無いので)
    - 並列処理できる。スレッドセーフ。
- 再帰(recursion, recursive call)
  - ラムダ計算で必須。
  - 実装するとスタックを極端に消費してしまうことがある。対策として 末尾呼び出し最適化 (TCO : Tail Call Optimization) がある。
- 遅延評価(lazy または non-strict evaluation) と先行評価(early または strict evaluation)
  - 式の評価(ようするに "実行") を本当にその式の値が必要となるタイミングまで遅らせるのが遅延評価らしい。
  - "lazy" の反対に "early" とあり、日本語だと「先行評価」っぽい。ただ一部記事では「正格評価」という表現も見かけたのだけど、同じもの？
  - 何が嬉しいかというと、ループ処理などで「無限遅延リスト」なる代物を扱えるのが便利らしい。「そのデータの範囲は予め決められないので、データが必要となるときに初めて生成する」みたいなパターンがユースケースとなる。分かりやすい例だと乱数とかかな？
- 型システム (type system)
  - ラムダ計算には型付き(typed lambda calculation)と型無し(untyped lambda calculation)がある。
  - 型付きだとコンパイル時に厳密にチェックしてくれるので、信頼性が向上する。型推測もあると便利。また「カリー・ハワード同型対応」(Curry–Howard correspondence)により数学的な「証明」(proof)を検証するのにも使われるようになった。
  - LISPなど型無しだと実行時のチェックとなる。
- 参照透過性 (referential transparency)
  - 一度変数に代入したら、変更できないこと。らしい。厳密には参照透過性の結果としてそうなるっぽい？結果として変数の扱いについて「副作用を持たなくなる」ので、関数型プログラミングにとってはプラスに働く。
  - 参照透過性を備えた関数を作ると結果的に pure function に近づき、スレッドセーフに扱えるようになる・・・ので、厳密な理論背景を知らなくても、実践テクニックとして覚えておけば良いかも。

## 関数型プログラミングと Stream

(TODO)

- Functional Programming - 6. Streams
  - https://sites.ualberta.ca/~jhoover/325/CourseNotes/section/Streams.htm

## 関数型プログラミングと圏論(category theory)

(TODO : Haskell を本格的に理解するのであれば必要そうだが、Java8の関数型プログラミング向けツールを使うだけならあまり必要無いかも。)

- https://en.wikipedia.org/wiki/Category_theory
- https://medium.com/@tzehsiang/javascript-functor-applicative-monads-in-pictures-b567c6415221

## 関数型プログラミングと Reactive Stream

(TODO)

----

2020-01時点の感想 : OOPより学習コスト高い。

## 例外をThrowできるFunctionalInterface

参考:

- `Java8のStreamやOptionalでクールに例外を処理する方法 - Qiita`
  - https://qiita.com/q-ikawa/items/3f55089e9081e1a854bc
- `Java Streams API を使って例外処理をきっちり行なうコードを書くことは難しい - Qiita`
  - https://qiita.com/daylife/items/b977f4f29b1f8ced3a02
- `[Javaの小枝] lambda式とチェック例外の相性の悪さをなんとかする - Qiita`
  - https://qiita.com/KIchiro/items/4fafd74c46d08275eb56
- `[Java] OptionalやStreamで例外を投げたい - Qiita`
  - https://qiita.com/yoshi389111/items/c6b7d373a00f8fd3d5f3
- `Java 8 Lambda function that throws exception? - Stack Overflow`
  - https://stackoverflow.com/questions/18198176/java-8-lambda-function-that-throws-exception
- `lambda - How can I throw CHECKED exceptions from inside Java 8 streams? - Stack Overflow`
  - https://stackoverflow.com/questions/27644361/how-can-i-throw-checked-exceptions-from-inside-java-8-streams/
- `java - Throwing exception from lambda - Stack Overflow`
  - https://stackoverflow.com/questions/31637892/throwing-exception-from-lambda/31638189
- `Java 8 Lambda function that throws exception? - Stack Overflow`
  - https://stackoverflow.com/questions/18198176/java-8-lambda-function-that-throws-exception
- `java - A better approach to handling exceptions in a functional way - Stack Overflow`
  - https://stackoverflow.com/questions/31270759/a-better-approach-to-handling-exceptions-in-a-functional-way/
- `Java8のラムダ式でチェック例外を投げられないのを何とかするやつ`
  - https://gist.github.com/kokumura/cd88320ee9d667ef8e46
