package org.dbunit.dataset.csv;
/* JDK8 バージョンまでは、ここにDbUnitのCSV DataSet でバイナリ列(blob, binaryなど)をBASE64で保存するために
 * DbUnitのソースコードからコピー・カスタマイズした DataSet / DataSetWriter / DataSetProducer 実装がおいてあった。
 * また javasnack.h2 パッケージの方でそれを使って実際にblob/binary列を読み書き/DbUnitでexportして比較する
 * テストコードを置いていた。
 * see: http://dbunit.sourceforge.net/
 * 
 * しかしながら JDK11 へのバージョンアップで DbUnit も最新に上げたところ、おそらく古いソースからカスタマイズした
 * 影響か、テストケースが失敗するようになってしまった。
 * 
 * WriterやProducerを直接改造するのではなく、以下の資料も参考に IDataSet の tableMetaData の Column で 
 * DataType.BINARY に入れ替える方法も試してみた。
 * https://qiita.com/sh-ogawa/items/6a96a9bc3195d7ed50f7
 * 
 * -> その結果、Base64エンコードはされるのだが、76文字目で改行がされてしまい、CSVが壊れてしまうことがわかった。
 * (もともとそれだったから、わざわざカスタム実装をコピペ改造で作った記憶も・・・)
 * 
 * そもそもなぜCSVにこだわるかというと、お仕事関連で実際にCSVでDBのimport/exportをテストコードで処理する必要があり、
 * しかもバイナリ列を含むテーブルがその操作対象だった、ということがある。
 * しかしながら、現状はすでにその必要が無くなっており、ぶっちゃけXMLのimport/exportでも問題ない。
 * このため、CSVで改行なしのBASE64にするためのカスタム実装をする意義が無くなった。
 * 
 * よって、カスタム実装についてはもともとのDbUnitのコードからコピーしてカスタムしているという
 * メンテナンスの悪さもあり、削除した。（という跡地）
 * 
 * さらに FlatXml を使ってテストコードを検証してみたところ、varcharにおいて改行やNULLなど制御文字などをいれると
 * そこでもXMLの処理などでエラーが発生することが判明した。
 * お仕事関連で言えば、0x00 - 0xFF までがvarcharでも想定される状況だったため、ここにおいて、
 * 今後のメンテナンスや「ライブラリに振り回されるリスク」を考慮し、varcharにおいてバイナリデータを扱うような
 * イレギュラーなユースケースにおいてはDbUnitを使用しない方が良さそうと思われる。
 */
