# eclipse の workspace 設定

NOTE: msakamot-sf 個人の好みに基づいた設定です。

## $ Workspaceのフォントや見た目のおすすめ設定

`Window` メニュー -> `Preferences` から:

- フォントのカスタマイズ：
  - `[General]` -> `[Appearance]` -> `[Colors and Fonts]` -> `Basic` -> `Text Font` : お好みの等幅フォントに変更。ソースコードエディタ・コンソールViewなどに表示するフォントになる。
- 行番号と空白文字の表示
  - `[General]` -> `[Editors]` -> `[Text Editors]` -> `Show line numbers` と `Show whitespace characters` にチェックを入れる。
- ファイルセーブ時に使ってない import 文を消す
  - `[Java]` -> `[Editor]` -> `[Save Actions]` -> `Perform the selected actions on save` にチェックを入れ、`Organize imports` にチェックを入れる。
- 補完時に上書きする
  - `[Java]` -> `[Editor]` -> `[Content Assist]` -> `Complletion overwrites` にチェックを入れる。
- [JUnit5](https://junit.org/junit5/) や [AssertJ](https://joel-costigliola.github.io/assertj/) の assertion を補完できるようにする。
  - `[Java]` -> `[Editor]` -> `[Content Assist]` -> `[Favorites]` に以下を追加
  - `org.junit.jupiter.api.Assertions` : JUnit5
  - `org.assertj.core.api.Assertions` : AssertJ
  - [mockito](https://site.mockito.org) を使う場合は、以下を追加すると良い。
  - `org.mockito.ArgumentMatchers`
  - `org.mockito.Mockito`
- どこで「;」を押しても行末にいれる
  - `[Java]` -> `[Editor]` -> `[Typing]` -> `Automatically insert at correct position` の `Semicolons` にチェックを入れる。
- クラス名なのか変数名なのかわかりやすくするためのSyntax Coloring
  - `[Java]` -> `[Editor]` -> `[Syntax Coloring]` -> Classes,Enums,IntefacesなどをEnable,Boldにする。
- `com.sun` 以下, `sun` 以下, java.awt.List や Swingパッケージを補完対象から外す
  - `[Java]` -> `[Appearance]` -> `[Type Filters]` -> `Add` ボタンで、`com.sun.**`, `sun.*`, `java.awt.*`, `javax.swing.*` を追加
  - その他使っているライブラリに応じて、アプリケーション側では使わないパッケージを追加するなどしてチューニングしておくと、import補完などの精度が向上する。
- Workspaceでの改行コードや文字コード
  - `[General]` -> `[Workspace]` で変更。（最近はデフォルトでUTF-8になってる。改行もデフォルト任せでよい）
- JSPファイルの文字コード
  - `[General]` -> `[Content Types]` で"Text" - "JSP"のDefault encodingをUTF-8に変更
  - `[Web]` -> `[JSP Files]` でEncodingを設定 (Mars.2だとデフォルトでUTF-8になってた)

## $ パースペクティブ(Perspective), ビュー(View)のおすすめ設定

パースペクティブ(Perspective):

- `Java EE` は使わなければcloseしてOK.(機能が豊富だが、使わない機能が多ければ `Java` パースペクティブで十分)
- `Window` -> `Perspective` -> `Open Perspective` から以下のパースペクティブをOpenしておくのがおすすめ。
  - `Java`, `Debug`, `Git`

`Java` パースペクティブで必須のView : `Window` -> `Show View` -> `Other...` から表示できる。

- `Java` -> `Package Explorer`
  - プロジェクト内の論理的なエクスプローラ。Eclipseプロジェクトの設定ファイルなど非表示。
- `General` -> `Navigator`
  - プロジェクトファイル全てのエクスプローラ。Eclipseプロジェクトの設定ファイルなども表示。
- `General` -> `Outline`
  - 開いたファイルの論理構成アウトラインを表示。Java, XMLなどに対応。
- `Java` -> `Type Hierarchy`
  - Javaソースのクラス継承の階層構造を表示。
- `General` -> `Problems`
  - コンパイルエラーや警告、その他のエラーなど表示。
- `General` -> `Console`
  - Java/Junitテスト実行時の標準入出力, エラー出力
- `General` -> `Progress`
  - workspaceやプロジェクトのビルドなど様々な処理の進捗状況を表示。これがないと、裏側で何が行われているのか分からなくなる。
- `General` -> `Tasks`
  - `TODO` などでマークした箇所を一覧表示。
- `General` -> `Error Log`
  - Eclipse 自体の実行時のエラーや警告、情報ログなどが表示される。
- `Server` -> `Servers`
  - Webアプリ(Servletなど)実行時に使うサーバ設定を登録・表示。
- `Checkstyle` -> `Checkstyle violations`
  - Checkstyle の検出結果を表示。(Checkstyleを使う場合は有用)
- `SpotBugs` -> `Bug Explorer`
  - FindBugs の検出結果を表示。(FindBugsを使う場合は有用)
- `General` -> `Search`
  - 検索結果を表示。
- `Java` -> `JUnit`
  - JUnitテスト実行結果を表示。

## $ Cleanup/Formatterのインポート

- Cleanup (Javaソースファイル保存時の自動処理) 設定のインポート:
  - `Window` メニュー -> `Preferences` -> `[Java]` -> `[Code Style]` -> `[Clean Up]` -> `[Import ...]` からインポートする。
  - javasnack用設定ファイル: [javasnack-eclipse-java-cleanup.xml](./javasnack-eclipse-java-cleanup.xml)
- Formatter (Javaソースコードのフォーマッタ) 設定のインポート:
  - `Window` メニュー -> `Preferences` -> `[Java]` -> `[Code Style]` -> `[Formatter]` -> `[Import ...]` からインポートする。
  - javasnack用設定ファイル: [javasnack-eclipse-java-formatter.xml](./javasnack-eclipse-java-formatter.xml)

## $ EGitからSSHでcloneするときの秘密鍵を指定するには

https://wiki.eclipse.org/EGit/User_Guide/Remote より, `Windows` メニュー -> `Preferences` -> `[General]` -> `[Network Connections]` -> `[SSH2]` で、以下の項目をチェック。

- "General" タブで、"SSH2 Home" は適切か？
- "General" タブで、"Private keys" は適切な秘密鍵ファイル名が設定されているか？
- "Key Management" タブで、 "Load Existing Key..." で秘密鍵の読み込みは試したか？
- "Authentication Methos" タブで、"password" のチェックを外したか？（外してなければ、外す）

なおこちらで扱えるのは OpenSSH 系のツールで生成した秘密鍵。
putty系やWinSCP系のツールで生成した秘密鍵はそのままでは扱えないので、OpenSSH系にエクスポートしたものを使用するよう注意が必要。

## $ EclipseにLombokをインストールする

1. lombok.jar を公式サイト  https://projectlombok.org/ よりダウンロード
2. `java -jar lombok.jar`
   - Linux の場合はデスクトップ環境で実行すること。
3. GUIのインストールアプリが起動したら、Eclipse の展開先ディレクトリを指定してインストールする。

参考：

- Lombok - Qiita
  - http://qiita.com/yyoshikaw/items/32a96332cc12854ca7a3
- Lombok 使い方メモ - Qiita
  - http://qiita.com/opengl-8080/items/671ffd4bf84fe5e32557
