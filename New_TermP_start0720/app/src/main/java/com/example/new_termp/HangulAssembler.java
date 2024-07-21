package com.example.new_termp;

public class HangulAssembler {

    private static final int HANGUL_BASE = 0xAC00;
    private static final int CHOSUNG_COUNT = 19;
    private static final int JUNGSUNG_COUNT = 21;
    private static final int JONGSUNG_COUNT = 28;

    private static final char[] CHOSUNG_LIST = {
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ',
            'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    };

    private static final char[] JUNGSUNG_LIST = {
            'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ',
            'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ',
            'ㅣ'
    };

    private static final char[] JONGSUNG_LIST = {
            ' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ',
            'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ',
            'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    };

    public static String assemble(String consonant, String vowel) throws IllegalArgumentException {
        return assemble(consonant, vowel, ' ');
    }

    public static String assemble(String consonant, String vowel, char jongseong) throws IllegalArgumentException {
        int chosungIndex = findIndex(CHOSUNG_LIST, consonant.charAt(0));
        if (chosungIndex == -1) throw new IllegalArgumentException("유효하지 않은 초성입니다.");

        int jungsungIndex = findIndex(JUNGSUNG_LIST, vowel.charAt(0));
        if (jungsungIndex == -1) throw new IllegalArgumentException("유효하지 않은 중성입니다.");

        int jongsungIndex = findIndex(JONGSUNG_LIST, jongseong);
        if (jongsungIndex == -1) throw new IllegalArgumentException("유효하지 않은 종성입니다.");

        int unicode = HANGUL_BASE + (chosungIndex * JUNGSUNG_COUNT * JONGSUNG_COUNT) + (jungsungIndex * JONGSUNG_COUNT) + jongsungIndex;
        return String.valueOf((char) unicode);
    }

    private static int findIndex(char[] array, char ch) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == ch) return i;
        }
        return -1;
    }
}
