## about checkstyle

- 概要:
  - Javaのコーディングスタイル検査ツール。
  - IDEやエディタで利用できる code formatter と一部重なるチェック内容もあるが、それらでカバーできていない範囲(javadocなど)も幅広く柔軟にチェックできる。
- 公式
  - https://checkstyle.org/
  - https://github.com/checkstyle
- Apache Maven Checkstyle Plugin
  - https://maven.apache.org/plugins/maven-checkstyle-plugin/
- Eclipse Checkstyle Plugin
  - https://checkstyle.org/eclipse-cs/

### running checkstyle 

```
[run only checkstyle plugin]
$ ./mvnw checkstyle:checkstyle

[run checkstyle plugin in "site" build lifecycle]
$ ./mvnw site
```

refs: https://maven.apache.org/plugins/maven-checkstyle-plugin/usage.html

### Eclipse Checkstyle Plugin configuration

install eclipse plugin: https://checkstyle.org/eclipse-cs/

import checkstyle configuration xml into eclipse:

1. import xml into eclipse global configuration.
   1. `Window` menu -> `Preferences` menu -> `[Checkstyle]` -> `Global Check Configurations` -> click `[New]`
   2. Select: `Type` -> `"Project Relative Configuration"`
   3. Location: click "Browse" -> then select project -> select `(this project)/config-files-linter/checkstyle-google_checks_10_23_1_custom.xml`
   4. Name: set `javasnack-checkstyle`
   5. `Apply and Close`
2. setup project specific setting
   1. right-click project -> `Properties` (ALT + Enter)
   2. `[Checkstyle]` -> `Main` tab -> check `"Checkstyle active for this project"`
   3. select imported xml configration
   4. `Apply and Close`
3. run first checkstyle
   1. right-click project -> `Checkstyle` -> `"Check Code with Checkstyle"`
4. show checkstyle check result
   1. `Window` menu -> `Show View` menu -> `Other ...` menu -> `Checkstyle` -> select `Checkstyle violations` -> `Open`

reload updated xml to Eclipse: (unstable way)
1. some operations in `Window` menu -> `Preferences` menu -> `[Checkstyle]` config then `"Apply and Click"`. (e.g. checkbox on <> off)
2. project setting -> switch checkstyle activate / deactivate some times.
3. rebuild project.

## 参考資料

about google java style:

- Google Java Style Guide
  - https://google.github.io/styleguide/javaguide.html
- Google Java Style Guide (非公式和訳)
  - https://kazurof.github.io/GoogleJavaStyle-ja/
- checkstyle – Google's Style
  - https://checkstyle.org/google_style.html
- Google Java Styleの特徴的なところまとめ - Qiita
  - https://qiita.com/kazurof/items/f99c90cb9eb091884ac4
- Google Java Style Guide - Qiita
  - https://qiita.com/unhurried/items/2bc10b0762498d1fc042

参考記事:

- Javaのコーディング規約チェックツールCheckstyleの使い方、CIとの統合、オープンソースプロジェクトでの活用事例 - Sider Blog
  - https://blog-ja.sideci.com/entry/2017/12/27/checkstyle-and-oss
- Checkstyle 使い方メモ - Qiita
  - https://qiita.com/opengl-8080/items/cb4122a19269e8e683a4

## TIPS & MEMO

元になるSunやGoogleのスタイル設定を extend/inherit/include などで部分的に拡張できないか？→無い。
- https://github.com/checkstyle/checkstyle/issues/2873
- https://github.com/checkstyle/checkstyle/issues/3738
- https://github.com/checkstyle/checkstyle/issues/4484
