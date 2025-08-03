## about PMD(Programming Mistake Detector)

- 概要:
  - Java のソースコードを静的解析して、バグや未使用の変数/コード、極端に複雑なコードを検出する静的検査ツール。
  - CPD (Copy/Paste Detector) も内蔵しており、重複コードの検出が可能。
- 公式
  - https://pmd.github.io/
  - https://docs.pmd-code.org/latest/
  - https://docs.pmd-code.org/latest/pmd_userdocs_tools_maven.html
  - https://maven.apache.org/plugins/maven-pmd-plugin/
- PMD Eclipse Plugin (PMD本家が開発している Eclipse Plugin)
  - https://github.com/pmd/pmd-eclipse-plugin

### running PMD

```
[run only PMD plugin]
$ ./mvnw pmd:pmd

[run PMD plugin in "site" build lifecycle]
$ ./mvnw site
```

refs: https://docs.pmd-code.org/latest/pmd_userdocs_tools_maven.html

### PMD Eclipse Plugin configuration

install eclipse plugin: https://github.com/pmd/pmd-eclipse-plugin

import project specific ruleset xml:

1. right-click project -> `Properties` -> `[PMD]` -> check `"Enable PMD"`
2. `"Rule source"` -> check `"Use the ruleset configured in a project file"` -> Browse -> select `config-files-linter/pmd-javasnack-custom-rule.xml`
3. `Apply and Close`
4. if you asked "The project doesn't contain a ruleset file. Do you want to create a ruleset from the configured properties ?", click "No".
5. View check result: `Window` menu -> `Perspective` -> `Open Perspective` -> `Other ...` -> select `PMD` -> `Open`

If you edited rule xml, no need to reconfigure. just do PMD -> "Check Code" again.

NOTE:

1. もし指定した ruleset xml ファイル通りに plugin 側で検出していない様子があれば(例: 除外設定したはずなのに検出される)、もう一度 PMD のルールセットを再設定しなおしてみると反映されることがある。
2. ruleset xml の変更を反映したいときは、一度 Eclipse 上でのPMDチェック結果をクリアして、もう一度検出し直す。
   1. right-click project -> Properties -> PMD -> click "Clear Violation Reviews"
   1. right-click project -> Properties -> PMD -> click "Clear Violations"
   1. right-click project -> Properties -> PMD -> click "Check Code" again
3. もしPMDの検出が煩わしければ、 `Full build enabled` のチェックを外すなどしてリアルタイム/フルビルド時の実行を抑制しても良い。
