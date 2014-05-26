/*
 * Copyright 2014 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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
package javasnack.snacks;

import java.io.UnsupportedEncodingException;

import javasnack.tool.UnsignedByte;

public class UnicodeC0C1 implements Runnable {

    String getAttrs(int codepoint) {
        StringBuilder sb = new StringBuilder();
        sb.append(Character.isAlphabetic(codepoint) ? "AL" : "__");
        sb.append("-");
        sb.append(Character.isLetter(codepoint) ? "LT" : "__");
        sb.append("-");
        sb.append(Character.isDigit(codepoint) ? "DG" : "__");
        sb.append("-");
        sb.append(Character.isUpperCase(codepoint) ? "UP" : "__");
        sb.append("-");
        sb.append(Character.isLowerCase(codepoint) ? "LO" : "__");
        sb.append("-");
        sb.append(Character.isTitleCase(codepoint) ? "TC" : "__");
        sb.append("-");
        sb.append(Character.isSpaceChar(codepoint) ? "SP" : "__");
        sb.append("-");
        sb.append(Character.isWhitespace(codepoint) ? "WS" : "__");
        sb.append("-");
        sb.append(Character.isISOControl(codepoint) ? "ISOCTL" : "______");
        return sb.toString();
    }

    @Override
    public void run() {
        try {
            String data = UnsignedByte.create0x00to0xFFString();
            int len = data.length();
            for (int i = 0; i < len; i++) {
                int cp = Character.codePointAt(data, i);
                System.out.print("binary:[" + i + "],");
                System.out.print("attr=[" + getAttrs(cp) + "],");
                System.out.println("name=[" + Character.getName(cp) + "]");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /*
     * RESULT:
     * 
     * binary:[0],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[NULL]
     * binary:[1],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[START OF HEADING]
     * binary:[2],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[START OF TEXT]
     * binary:[3],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[END OF TEXT]
     * binary:[4],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[END OF TRANSMISSION]
     * binary:[5],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[ENQUIRY]
     * binary:[6],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[ACKNOWLEDGE]
     * binary:[7],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[BELL]
     * binary:[8],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[BACKSPACE]
     * binary:[9],attr=[__-__-__-__-__-__-__-WS-ISOCTL],name=[CHARACTER TABULATION]
     * binary:[10],attr=[__-__-__-__-__-__-__-WS-ISOCTL],name=[LINE FEED (LF)]
     * binary:[11],attr=[__-__-__-__-__-__-__-WS-ISOCTL],name=[LINE TABULATION]
     * binary:[12],attr=[__-__-__-__-__-__-__-WS-ISOCTL],name=[FORM FEED (FF)]
     * binary:[13],attr=[__-__-__-__-__-__-__-WS-ISOCTL],name=[CARRIAGE RETURN (CR)]
     * binary:[14],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[SHIFT OUT]
     * binary:[15],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[SHIFT IN]
     * binary:[16],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[DATA LINK ESCAPE]
     * binary:[17],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[DEVICE CONTROL ONE]
     * binary:[18],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[DEVICE CONTROL TWO]
     * binary:[19],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[DEVICE CONTROL THREE]
     * binary:[20],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[DEVICE CONTROL FOUR]
     * binary:[21],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[NEGATIVE ACKNOWLEDGE]
     * binary:[22],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[SYNCHRONOUS IDLE]
     * binary:[23],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[END OF TRANSMISSION BLOCK]
     * binary:[24],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[CANCEL]
     * binary:[25],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[END OF MEDIUM]
     * binary:[26],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[SUBSTITUTE]
     * binary:[27],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[ESCAPE]
     * binary:[28],attr=[__-__-__-__-__-__-__-WS-ISOCTL],name=[INFORMATION SEPARATOR FOUR]
     * binary:[29],attr=[__-__-__-__-__-__-__-WS-ISOCTL],name=[INFORMATION SEPARATOR THREE]
     * binary:[30],attr=[__-__-__-__-__-__-__-WS-ISOCTL],name=[INFORMATION SEPARATOR TWO]
     * binary:[31],attr=[__-__-__-__-__-__-__-WS-ISOCTL],name=[INFORMATION SEPARATOR ONE]
     * binary:[32],attr=[__-__-__-__-__-__-SP-WS-______],name=[SPACE]
     * binary:[33],attr=[__-__-__-__-__-__-__-__-______],name=[EXCLAMATION MARK]
     * binary:[34],attr=[__-__-__-__-__-__-__-__-______],name=[QUOTATION MARK]
     * binary:[35],attr=[__-__-__-__-__-__-__-__-______],name=[NUMBER SIGN]
     * binary:[36],attr=[__-__-__-__-__-__-__-__-______],name=[DOLLAR SIGN]
     * binary:[37],attr=[__-__-__-__-__-__-__-__-______],name=[PERCENT SIGN]
     * binary:[38],attr=[__-__-__-__-__-__-__-__-______],name=[AMPERSAND]
     * binary:[39],attr=[__-__-__-__-__-__-__-__-______],name=[APOSTROPHE]
     * binary:[40],attr=[__-__-__-__-__-__-__-__-______],name=[LEFT PARENTHESIS]
     * binary:[41],attr=[__-__-__-__-__-__-__-__-______],name=[RIGHT PARENTHESIS]
     * binary:[42],attr=[__-__-__-__-__-__-__-__-______],name=[ASTERISK]
     * binary:[43],attr=[__-__-__-__-__-__-__-__-______],name=[PLUS SIGN]
     * binary:[44],attr=[__-__-__-__-__-__-__-__-______],name=[COMMA]
     * binary:[45],attr=[__-__-__-__-__-__-__-__-______],name=[HYPHEN-MINUS]
     * binary:[46],attr=[__-__-__-__-__-__-__-__-______],name=[FULL STOP]
     * binary:[47],attr=[__-__-__-__-__-__-__-__-______],name=[SOLIDUS]
     * binary:[48],attr=[__-__-DG-__-__-__-__-__-______],name=[DIGIT ZERO]
     * binary:[49],attr=[__-__-DG-__-__-__-__-__-______],name=[DIGIT ONE]
     * binary:[50],attr=[__-__-DG-__-__-__-__-__-______],name=[DIGIT TWO]
     * binary:[51],attr=[__-__-DG-__-__-__-__-__-______],name=[DIGIT THREE]
     * binary:[52],attr=[__-__-DG-__-__-__-__-__-______],name=[DIGIT FOUR]
     * binary:[53],attr=[__-__-DG-__-__-__-__-__-______],name=[DIGIT FIVE]
     * binary:[54],attr=[__-__-DG-__-__-__-__-__-______],name=[DIGIT SIX]
     * binary:[55],attr=[__-__-DG-__-__-__-__-__-______],name=[DIGIT SEVEN]
     * binary:[56],attr=[__-__-DG-__-__-__-__-__-______],name=[DIGIT EIGHT]
     * binary:[57],attr=[__-__-DG-__-__-__-__-__-______],name=[DIGIT NINE]
     * binary:[58],attr=[__-__-__-__-__-__-__-__-______],name=[COLON]
     * binary:[59],attr=[__-__-__-__-__-__-__-__-______],name=[SEMICOLON]
     * binary:[60],attr=[__-__-__-__-__-__-__-__-______],name=[LESS-THAN SIGN]
     * binary:[61],attr=[__-__-__-__-__-__-__-__-______],name=[EQUALS SIGN]
     * binary:[62],attr=[__-__-__-__-__-__-__-__-______],name=[GREATER-THAN SIGN]
     * binary:[63],attr=[__-__-__-__-__-__-__-__-______],name=[QUESTION MARK]
     * binary:[64],attr=[__-__-__-__-__-__-__-__-______],name=[COMMERCIAL AT]
     * binary:[65],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER A]
     * binary:[66],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER B]
     * binary:[67],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER C]
     * binary:[68],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER D]
     * binary:[69],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER E]
     * binary:[70],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER F]
     * binary:[71],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER G]
     * binary:[72],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER H]
     * binary:[73],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER I]
     * binary:[74],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER J]
     * binary:[75],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER K]
     * binary:[76],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER L]
     * binary:[77],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER M]
     * binary:[78],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER N]
     * binary:[79],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER O]
     * binary:[80],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER P]
     * binary:[81],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER Q]
     * binary:[82],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER R]
     * binary:[83],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER S]
     * binary:[84],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER T]
     * binary:[85],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER U]
     * binary:[86],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER V]
     * binary:[87],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER W]
     * binary:[88],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER X]
     * binary:[89],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER Y]
     * binary:[90],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER Z]
     * binary:[91],attr=[__-__-__-__-__-__-__-__-______],name=[LEFT SQUARE BRACKET]
     * binary:[92],attr=[__-__-__-__-__-__-__-__-______],name=[REVERSE SOLIDUS]
     * binary:[93],attr=[__-__-__-__-__-__-__-__-______],name=[RIGHT SQUARE BRACKET]
     * binary:[94],attr=[__-__-__-__-__-__-__-__-______],name=[CIRCUMFLEX ACCENT]
     * binary:[95],attr=[__-__-__-__-__-__-__-__-______],name=[LOW LINE]
     * binary:[96],attr=[__-__-__-__-__-__-__-__-______],name=[GRAVE ACCENT]
     * binary:[97],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER A]
     * binary:[98],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER B]
     * binary:[99],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER C]
     * binary:[100],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER D]
     * binary:[101],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER E]
     * binary:[102],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER F]
     * binary:[103],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER G]
     * binary:[104],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER H]
     * binary:[105],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER I]
     * binary:[106],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER J]
     * binary:[107],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER K]
     * binary:[108],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER L]
     * binary:[109],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER M]
     * binary:[110],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER N]
     * binary:[111],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER O]
     * binary:[112],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER P]
     * binary:[113],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER Q]
     * binary:[114],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER R]
     * binary:[115],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER S]
     * binary:[116],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER T]
     * binary:[117],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER U]
     * binary:[118],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER V]
     * binary:[119],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER W]
     * binary:[120],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER X]
     * binary:[121],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER Y]
     * binary:[122],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER Z]
     * binary:[123],attr=[__-__-__-__-__-__-__-__-______],name=[LEFT CURLY BRACKET]
     * binary:[124],attr=[__-__-__-__-__-__-__-__-______],name=[VERTICAL LINE]
     * binary:[125],attr=[__-__-__-__-__-__-__-__-______],name=[RIGHT CURLY BRACKET]
     * binary:[126],attr=[__-__-__-__-__-__-__-__-______],name=[TILDE]
     * binary:[127],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[DELETE]
     * binary:[128],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[LATIN 1 SUPPLEMENT 80]
     * binary:[129],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[LATIN 1 SUPPLEMENT 81]
     * binary:[130],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[BREAK PERMITTED HERE]
     * binary:[131],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[NO BREAK HERE]
     * binary:[132],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[LATIN 1 SUPPLEMENT 84]
     * binary:[133],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[NEXT LINE (NEL)]
     * binary:[134],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[START OF SELECTED AREA]
     * binary:[135],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[END OF SELECTED AREA]
     * binary:[136],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[CHARACTER TABULATION SET]
     * binary:[137],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[CHARACTER TABULATION WITH JUSTIFICATION]
     * binary:[138],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[LINE TABULATION SET]
     * binary:[139],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[PARTIAL LINE FORWARD]
     * binary:[140],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[PARTIAL LINE BACKWARD]
     * binary:[141],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[REVERSE LINE FEED]
     * binary:[142],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[SINGLE SHIFT TWO]
     * binary:[143],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[SINGLE SHIFT THREE]
     * binary:[144],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[DEVICE CONTROL STRING]
     * binary:[145],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[PRIVATE USE ONE]
     * binary:[146],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[PRIVATE USE TWO]
     * binary:[147],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[SET TRANSMIT STATE]
     * binary:[148],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[CANCEL CHARACTER]
     * binary:[149],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[MESSAGE WAITING]
     * binary:[150],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[START OF GUARDED AREA]
     * binary:[151],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[END OF GUARDED AREA]
     * binary:[152],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[START OF STRING]
     * binary:[153],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[LATIN 1 SUPPLEMENT 99]
     * binary:[154],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[SINGLE CHARACTER INTRODUCER]
     * binary:[155],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[CONTROL SEQUENCE INTRODUCER]
     * binary:[156],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[STRING TERMINATOR]
     * binary:[157],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[OPERATING SYSTEM COMMAND]
     * binary:[158],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[PRIVACY MESSAGE]
     * binary:[159],attr=[__-__-__-__-__-__-__-__-ISOCTL],name=[APPLICATION PROGRAM COMMAND]
     * binary:[160],attr=[__-__-__-__-__-__-SP-__-______],name=[NO-BREAK SPACE]
     * binary:[161],attr=[__-__-__-__-__-__-__-__-______],name=[INVERTED EXCLAMATION MARK]
     * binary:[162],attr=[__-__-__-__-__-__-__-__-______],name=[CENT SIGN]
     * binary:[163],attr=[__-__-__-__-__-__-__-__-______],name=[POUND SIGN]
     * binary:[164],attr=[__-__-__-__-__-__-__-__-______],name=[CURRENCY SIGN]
     * binary:[165],attr=[__-__-__-__-__-__-__-__-______],name=[YEN SIGN]
     * binary:[166],attr=[__-__-__-__-__-__-__-__-______],name=[BROKEN BAR]
     * binary:[167],attr=[__-__-__-__-__-__-__-__-______],name=[SECTION SIGN]
     * binary:[168],attr=[__-__-__-__-__-__-__-__-______],name=[DIAERESIS]
     * binary:[169],attr=[__-__-__-__-__-__-__-__-______],name=[COPYRIGHT SIGN]
     * binary:[170],attr=[AL-LT-__-__-LO-__-__-__-______],name=[FEMININE ORDINAL INDICATOR]
     * binary:[171],attr=[__-__-__-__-__-__-__-__-______],name=[LEFT-POINTING DOUBLE ANGLE QUOTATION MARK]
     * binary:[172],attr=[__-__-__-__-__-__-__-__-______],name=[NOT SIGN]
     * binary:[173],attr=[__-__-__-__-__-__-__-__-______],name=[SOFT HYPHEN]
     * binary:[174],attr=[__-__-__-__-__-__-__-__-______],name=[REGISTERED SIGN]
     * binary:[175],attr=[__-__-__-__-__-__-__-__-______],name=[MACRON]
     * binary:[176],attr=[__-__-__-__-__-__-__-__-______],name=[DEGREE SIGN]
     * binary:[177],attr=[__-__-__-__-__-__-__-__-______],name=[PLUS-MINUS SIGN]
     * binary:[178],attr=[__-__-__-__-__-__-__-__-______],name=[SUPERSCRIPT TWO]
     * binary:[179],attr=[__-__-__-__-__-__-__-__-______],name=[SUPERSCRIPT THREE]
     * binary:[180],attr=[__-__-__-__-__-__-__-__-______],name=[ACUTE ACCENT]
     * binary:[181],attr=[AL-LT-__-__-LO-__-__-__-______],name=[MICRO SIGN]
     * binary:[182],attr=[__-__-__-__-__-__-__-__-______],name=[PILCROW SIGN]
     * binary:[183],attr=[__-__-__-__-__-__-__-__-______],name=[MIDDLE DOT]
     * binary:[184],attr=[__-__-__-__-__-__-__-__-______],name=[CEDILLA]
     * binary:[185],attr=[__-__-__-__-__-__-__-__-______],name=[SUPERSCRIPT ONE]
     * binary:[186],attr=[AL-LT-__-__-LO-__-__-__-______],name=[MASCULINE ORDINAL INDICATOR]
     * binary:[187],attr=[__-__-__-__-__-__-__-__-______],name=[RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK]
     * binary:[188],attr=[__-__-__-__-__-__-__-__-______],name=[VULGAR FRACTION ONE QUARTER]
     * binary:[189],attr=[__-__-__-__-__-__-__-__-______],name=[VULGAR FRACTION ONE HALF]
     * binary:[190],attr=[__-__-__-__-__-__-__-__-______],name=[VULGAR FRACTION THREE QUARTERS]
     * binary:[191],attr=[__-__-__-__-__-__-__-__-______],name=[INVERTED QUESTION MARK]
     * binary:[192],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER A WITH GRAVE]
     * binary:[193],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER A WITH ACUTE]
     * binary:[194],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER A WITH CIRCUMFLEX]
     * binary:[195],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER A WITH TILDE]
     * binary:[196],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER A WITH DIAERESIS]
     * binary:[197],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER A WITH RING ABOVE]
     * binary:[198],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER AE]
     * binary:[199],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER C WITH CEDILLA]
     * binary:[200],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER E WITH GRAVE]
     * binary:[201],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER E WITH ACUTE]
     * binary:[202],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER E WITH CIRCUMFLEX]
     * binary:[203],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER E WITH DIAERESIS]
     * binary:[204],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER I WITH GRAVE]
     * binary:[205],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER I WITH ACUTE]
     * binary:[206],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER I WITH CIRCUMFLEX]
     * binary:[207],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER I WITH DIAERESIS]
     * binary:[208],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER ETH]
     * binary:[209],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER N WITH TILDE]
     * binary:[210],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER O WITH GRAVE]
     * binary:[211],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER O WITH ACUTE]
     * binary:[212],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER O WITH CIRCUMFLEX]
     * binary:[213],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER O WITH TILDE]
     * binary:[214],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER O WITH DIAERESIS]
     * binary:[215],attr=[__-__-__-__-__-__-__-__-______],name=[MULTIPLICATION SIGN]
     * binary:[216],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER O WITH STROKE]
     * binary:[217],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER U WITH GRAVE]
     * binary:[218],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER U WITH ACUTE]
     * binary:[219],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER U WITH CIRCUMFLEX]
     * binary:[220],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER U WITH DIAERESIS]
     * binary:[221],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER Y WITH ACUTE]
     * binary:[222],attr=[AL-LT-__-UP-__-__-__-__-______],name=[LATIN CAPITAL LETTER THORN]
     * binary:[223],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER SHARP S]
     * binary:[224],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER A WITH GRAVE]
     * binary:[225],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER A WITH ACUTE]
     * binary:[226],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER A WITH CIRCUMFLEX]
     * binary:[227],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER A WITH TILDE]
     * binary:[228],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER A WITH DIAERESIS]
     * binary:[229],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER A WITH RING ABOVE]
     * binary:[230],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER AE]
     * binary:[231],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER C WITH CEDILLA]
     * binary:[232],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER E WITH GRAVE]
     * binary:[233],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER E WITH ACUTE]
     * binary:[234],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER E WITH CIRCUMFLEX]
     * binary:[235],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER E WITH DIAERESIS]
     * binary:[236],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER I WITH GRAVE]
     * binary:[237],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER I WITH ACUTE]
     * binary:[238],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER I WITH CIRCUMFLEX]
     * binary:[239],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER I WITH DIAERESIS]
     * binary:[240],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER ETH]
     * binary:[241],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER N WITH TILDE]
     * binary:[242],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER O WITH GRAVE]
     * binary:[243],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER O WITH ACUTE]
     * binary:[244],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER O WITH CIRCUMFLEX]
     * binary:[245],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER O WITH TILDE]
     * binary:[246],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER O WITH DIAERESIS]
     * binary:[247],attr=[__-__-__-__-__-__-__-__-______],name=[DIVISION SIGN]
     * binary:[248],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER O WITH STROKE]
     * binary:[249],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER U WITH GRAVE]
     * binary:[250],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER U WITH ACUTE]
     * binary:[251],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER U WITH CIRCUMFLEX]
     * binary:[252],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER U WITH DIAERESIS]
     * binary:[253],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER Y WITH ACUTE]
     * binary:[254],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER THORN]
     * binary:[255],attr=[AL-LT-__-__-LO-__-__-__-______],name=[LATIN SMALL LETTER Y WITH DIAERESIS]
     */
}
