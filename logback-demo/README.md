# logback サンプル設定XML

試し方:

1. `mvnw package` でjarファイルを生成 (`mvnw -Dmaven.test.skip=true package` でもOK)
2. 試したい設定ファイルを確認し、コメントで指定されている実行用の`javasnack.snacks.logback` パッケージ配下のクラス名を確認。
3. `java -Dlogback.configurationFile=(試したい設定ファイル) -jar target/javasnack-(...).jar` で起動し、`javasnack.snacks.logback` パッケージ配下のサンプルクラスを実行する。(設定ファイルは相対パス指定でOK)

## 参考資料

logback 公式マニュアル:

- http://logback.qos.ch/manual/
- http://logback.qos.ch/manual/index_ja.html (日本語)
- ショートカット:
  - Console/File Appender: http://logback.qos.ch/manual/appenders.html
  - メセージフォーマット(PatternLayout): http://logback.qos.ch/manual/layouts.html#ClassicPatternLayout

ブログ記事:

- `logback機能,設定まとめ - Qiita`
  - https://qiita.com/rubytomato@github/items/93770f827e46cc7e684f
- `Logback 使い方メモ - Qiita`
  - https://qiita.com/opengl-8080/items/49719f2d35171f017aa9

how-to:

- java起動時のコマンドラインオプションで設定ファイルを指定する方法:
  - http://logback.qos.ch/manual/configuration.html#configFileProperty
- `logback で出すログを強制的に１行にして収集しやすく・運用しやすくする - Qiita`
  - https://qiita.com/roundrop@github/items/8989b7f29d70f618e503
- `Add something to log if there is an exception in logback - Stack Overflow`
  - https://stackoverflow.com/questions/47018509/add-something-to-log-if-there-is-an-exception-in-logback

