## javasnack : JMH samples

こちらのディレクトリは、 OpenJDK JMH 公式サンプルを以下のURLからコピペしたものになります。

- https://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
- 2020-02 - 04月ごろのリビジョン。

勉強用に、各サンプルごとのポイントをまとめたもの:

- 01, HelloWorld
  - JMHの基本となる `@Benchmark` アノテーションの動作確認用。
- 02, BenchmarkModes
  - ベンチマークのモードを `@BenchmarkMode` で指定。平均時間を見る `AverageTime`, 単位時間当たりに実行できた回数を見る `Throughput` がよく使われる。
- 03, States
  - メソッドの呼び出し毎に状態を保存したい(カウンタやデータなど)場合に `@State` アノテーションを付けてメソッド引数にDIする方法の紹介。`Scope` も `Benchmark` と `Thread` の2種類を紹介。
- 04, DefaultState
  - ベンチマークメソッドを定義してるクラス自体に `@State` アノテーションを付けてインスタンスフィールドに状態を保存する例。
- 05, StateFixtures
  - `@Setup` と `@TearDown` アノテーションによる `@State` オブジェクトの生成/終了処理の実装例(Fixture)。
- 06, FixtureLevel
  - `@Setup` と `@TearDown` がどの粒度で呼ばれるか `Level` パラメータで指定する例。
- 07, FixtureLevelInvocation
  - `@Setup` と `@TearDown` で使える `Level.Invocation` の紹介と注意点について。
- 08, DeadCode
  - コンパイラによる Dead-Code Elimination (DCE) の影響を確認できるデモ。
- 09, Blackholes
  - DCE を回避するための手法の紹介。戻り値でreturnさせる / JMHが提供する `Blackhole` クラスを使う など。
- 10, ConstantFold
  - JVMによる Constant Folding (定数畳み込み) の発生と回避方法を確認できるデモ。 `@State` 化したインスタンスフィールドを使って回避している。
- 11, Loops
  - 「あるメソッドを XX 回呼び出す処理」をベンチマークすると不正確な結果になるよ、というデモ。 `@OperationsPerInvocation` アノテーションも紹介しているが、それでもループ最適化処理の影響でズレが生じる。
- 12, Forking
  - 同じJVMインスタンス中で複数のベンチマークを行うとJVMのプロファイリングによる影響が出てしまう、というデモ。
  - JMHはデフォルトでは各ベンチマークをfork(別のJVMで実施)しているため基本的にはJVMプロファイリングの影響が出ないようになっている。
- 13, RunToRun
  - JVMで実行ごとにブレが発生するような場合に、複数回 Fork することで均等化するデモ。
- 14 : (欠番)
- 15, Grouping
  - `@Group`, `@GroupThreads` アノテーションを使って複数のベンチマークをグループ化してマルチスレッドで同時実行するデモ。
- 16, CompilerControl
  - `@CompilerControl` アノテーションでHostSpotVMにおけるインライン化の有効/無効を指定するデモ。
- 17, SyncIterations
  - `-si {true|false}` オプションによる、複数スレッドでのベンチマーク iteration の同時開始デモ。(defaultはtrue)
- 18, Control
  - `Control` インスタンスをベンチマークメソッドの引数にDIして、ベンチマークの終了を内部で検知するデモ。(ベンチマークの制御状態をベンチマーク対象内部から取得できる)
- 19 : (欠番)
- 20, Annotations
  - `@Warmup, @Measurement, @Fork` アノテーションを使って、JMHのコマンドライン引数で指定していたのと同じパラメータをハードコードするデモ。
- 21, ConsumeCPU
  - `Blackhole.consumeCPU(N)` を使ってCPUサイクルを消費する処理をベンチマークメソッド内に埋め込むデモ。
- 22, FalseSharing
  - マルチスレッドからアクセスするメモリブロックが隣接していて、せっかくキャッシュに載っても他のスレッドからのアクセスで取り直しになってしまう False Sharing の悪影響と回避方法の紹介デモ。
  - https://en.wikipedia.org/wiki/False_sharing
- 23, AuxCounters
  - `@State` オブジェクトのpublicフィールドを、追加のベンチマークメトリクスとして抽出する `@AuxCounters` アノテーションのデモ。カウンタの最終値や、ベンチマーク処理途中のフラグ状況の把握などに使える。
- 24, Inheritance
  - `@Benchmark` メソッドを継承したクラスでベンチマークが実行されるデモ。
- 25, ApiGa
  - JMHのフレームワーク自体を呼び出してベンチマーク結果を取得し、それにより遺伝的アルゴリズム(GA)でパフォーマンス改善をしていくデモ。(複雑なのでコピペ省略)
- 26, BatchSize
  - ベンチマークを時間で区切って計測するのではなく、ベンチマークメソッドの呼び出し回数(batch size)で区切って計測するデモ。実行時間に対して非線形でパフォーマンスが変化するような場合に、呼び出し回数を固定にすることで計測結果を安定させている。
- 27, Params
  - `@Param` アノテーションを使って、パラメータを変えて複数のベンチマークを計測するデモ。
- 28, BlackholeHelpers
  - `Blackhole` を `@Benchmark` だけでなく `@Setup` / `@TearDown` メソッドのパラメータにも渡して、fixtureで Blackhole使えるよ、というデモ。
- 29, StatesDag
  - 複数の `@State` オブジェクトを入れ子にして使うこともできる。その場合、JMH側で依存関係を調整して、適切な順序で `@Setup` / `@TearDown` が呼ばれるよ、というデモ。
- 30, Interrupts
  - ベンチマーク処理実行中に、JMH側でスレッドにinterruptするデモ。
- 31, InfraParams
  - ベンチマーク設定/イテレーション(ベンチマーク時の繰り返し回数)設定/スレッド設定など、JMH側の設定内容を `@State` オブジェクトの `@Setup` で取得し、ベンチマーク処理の中から参照するデモ。
- 32, BulkWarmup
  - Bulk warmup により、わざと複数のベンチマークのwarmupを事前に済ませてからベンチマーク処理に進むデモ。ベンチマークのプロファイルを分けるのではなく、ミックスすることでワーストケースでの測定をしたい、というユースケース向き。
- 33, SecurityManager
  - SecurityManager を有効にしたり、無効にしたりしてベンチマーク実行する紹介デモ。
- 34, SafeLooping
  - 11, Loops のようにベンチマークメソッドの中でループ処理を行うのは基本的には良くない。とはいえ、ある程度の量のデータセットに対して順次処理する一連の操作をベンチマークしようとしたらループは避けられない。
  - そんな場合に、ループ処理の最適化を可能な限り抑制して安定した正確なベンチマークを行うためのTIPS紹介。 Blackhole を使ってループ処理の畳込みを防止したり、`@CompilerControl` でインライン化を抑制している。
- 35, Profilers
  - JMHの組み込みプロファイラと、OS依存のプロファイラの紹介デモ。組み込みプロファイラではstackベースでのメソッド呼び出し状況やGC/classloading/JITコンパイル状況などを取得できる。
- 36, BranchPrediction
  - ベンチマーク用のfixtureデータについて、ベンチマーク中で条件分岐処理がある場合、fixtureデータが綺麗にソートされた状態/ランダムな状態で条件分岐予測の影響差が出てしまい、本来のワークロードと異なる計測結果になるよ、という紹介デモ。
- 37, CacheAccess
  - ベンチマーク処理中でのメモリアクセスの仕方によってキャッシュヒットの影響が大きくなるデモ。
- 38, PerInvokeSetup
  - ソート処理など `@State` オブジェクトに副作用を与え、その結果次のベンチマーク呼び出しが本来計測すべき状態では計測できない場合の紹介と、それをどう回避するかの紹介デモ。
  - 例としてソート処理を使っていて、一度 `@State` オブジェクト中の配列をソートしてしまえば2回目以降はソート済みとなるため正しく計測できない例を示している。
  - 回避方法として `@Setup(Level.Invocation)` （推奨されていない) と、ベンチマーク中で `Arrays.copyOf()` で配列をコピーする方法 (こちらが大部分のユースケースで推奨とのこと) を紹介している。
