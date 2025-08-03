## about SpotBugs

- 概要:
  - Java のクラスファイルを解析してバグや一部の命名規約ルール違反などを検出する静的検査ツール。
- 公式
  - https://spotbugs.github.io/
  - https://spotbugs.readthedocs.io/ja/latest/ (日本語)
  - https://spotbugs.readthedocs.io/en/latest/ (英語)
- SpotBugs Maven Plugin
  - https://spotbugs.github.io/spotbugs-maven-plugin/
- SpotBugs Eclipse plugin
  - https://marketplace.eclipse.org/content/spotbugs-eclipse-plugin

### running spotbugs

```
[run only spotbugs plugin]
$ ./mvnw spotbugs:spotbugs

[run spotbugs plugin in "site" build lifecycle]
$ ./mvnw site
```

refs: https://spotbugs.github.io/spotbugs-maven-plugin/usage.html

### Eclipse SpotBugs plugin configuration

install eclipse plugin:

- https://spotbugs.readthedocs.io/en/latest/eclipse.html
- https://spotbugs.readthedocs.io/ja/latest/eclipse.html


1. import project specific include/exclude filter.
   1. right-click project -> `Properties` -> `[SpotBugs]` -> check `"Enable project specific settings"`
   1. check `"Run automatically"`, and `"(also on full build)"`
      - NOTE: もし動作が重くなったり煩わしければ、ここのチェックを外してもOK.
   1. at `"Filter files"` tab, add `config-files-linter/spotbugs-exclude-filter.xml` to exclude filter file, add `config-files-linter/spotbugs-include-filter.xml` to include filter file.
2. SpotBugs plugin setting (see pom.xml configuration)
   1. set "analysis effort" (分析力) to "Maximal" (最大)
   2. set "Minimum rank to report" (報告する最小ランク) to 15 ("Of Concern")
   3. set "Minimum confidence to report" (レポートする最低の信頼度) to "Medium"
   4. check below categories (see spotbugs-include-filter.xml)
      1. "Bad practice"
      2. "Malicious code vulnerability"
      3. "Correctness"
      4. "Performance"
      5. "Security"
      6. "Dodgy code"
      7. "Multithreaded correctness"
      8. "Internationalization"
   5. do NOT check "Experimental" category.
3. run SpotBugs check manually:
   1. right-click project -> `SpotBugs` -> click `Find Bugs`
   2. view check result: `Window` menu -> `Show View` menu -> `Other ...` -> `SpotBugs` -> select `Bug Explorer` -> `Open`

If you edited exclude/include xml filter, then right-click project -> `SpotBugs` -> clear markers and `Find Bugs` again.
