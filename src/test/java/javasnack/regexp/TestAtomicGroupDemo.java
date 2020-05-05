/*
 * Copyright 2020 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package javasnack.regexp;

import static javasnack.regexp.StartEnd.se;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

public class TestAtomicGroupDemo {
    /* atomic group: possessive quantifier とほぼ同じ。group内でのbacktrackは発生しうる(ref[4])。
     * ref[1]: https://stackoverflow.com/questions/24107034/how-to-make-atomic-group-work-with-alternations-in-java-regex
     * ref[2]: https://www.logicbig.com/tutorials/core-java-tutorial/java-regular-expressions/atomic-groups.html
     * ref[3]: https://www.regular-expressions.info/atomic.html
     * ref[4]: https://stackoverflow.com/questions/18292247/difference-between-possessive-quantifier-and-once-only-subpatterns
     * ref[5]: https://www.javaworld.com/article/2077757/optimizing-regular-expressions-in-java.html
     * 
     * NOTE: ref[5] では "independent group" という表記も使われている。
     */

    @Test
    public void testAtomicGroupDemo() {
        final Pattern atomicgroup1 = Pattern.compile("((?>a*)(a))");
        Matcher m = atomicgroup1.matcher("aaaaa");
        assertThat(m.find()).isFalse();

        final Pattern atomicgroup2 = Pattern.compile("(?>a*+)");
        m = atomicgroup2.matcher("aaaaa");
        assertThat(m.find()).isTrue();
        assertThat(se(m)).isEqualTo(se(0, 5));
        assertThat(m.groupCount()).isEqualTo(0);
        assertThat(se(m, 0)).isEqualTo(se(0, 5, 0, "aaaaa"));

        final Pattern atomicgroup3 = Pattern.compile("a(?>bc|b)c");
        m = atomicgroup3.matcher("abc");
        /* (?>bc|b) の bc が先にマッチして、それ以外の可能性は探らない。
         * -> abc すべて消費してしまい、正規表現の最後の c にマッチしなくなる。
         */
        assertThat(m.find()).isFalse();
        m = atomicgroup3.matcher("abcc");
        // これなら bc 消費したあとに c が来るので、 a bc c とマッチする。
        assertThat(m.find()).isTrue();
        assertThat(se(m)).isEqualTo(se(0, 4));
        assertThat(m.groupCount()).isEqualTo(0);
        assertThat(se(m, 0)).isEqualTo(se(0, 4, 0, "abcc"));

        // ref[4] によると、以下のようなケースで atomic group と possessive の差異が現れる。
        final Pattern possessive4 = Pattern.compile("a++ab");
        m = possessive4.matcher("aaaabbbb");
        // "a++" (possessive) が最初のaをすべて消費してしまい、残りが "b..." となり、後ろの "ab" にマッチせず。
        assertThat(m.find()).isFalse();

        final Pattern atomicgroup4 = Pattern.compile("(?>a+)ab");
        m = atomicgroup4.matcher("aaaabbbb");
        // -> possessive を単純にatomic group に置き換えただけなら同じ動きとなる。
        assertThat(m.find()).isFalse();

        // ref[4] によると atomic group 内でのbackgrackは発生する。そのため、以下のケースはマッチする。
        final Pattern atomicgroup5 = Pattern.compile("(?>a+ab)");
        m = atomicgroup5.matcher("aaaabbbb");
        assertThat(m.find()).isTrue();
        assertThat(se(m)).isEqualTo(se(0, 5));
        assertThat(m.groupCount()).isEqualTo(0);
        assertThat(se(m, 0)).isEqualTo(se(0, 5, 0, "aaaab"));
    }

}
