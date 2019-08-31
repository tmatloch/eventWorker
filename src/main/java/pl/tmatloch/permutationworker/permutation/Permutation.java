package pl.tmatloch.permutationworker.permutation;

import java.util.List;

public interface Permutation {

    void calculatePermutations(String text);
    List<String> getPermutationResults();

    default String swap(String originalString, int firstPosition, int secondPosition) {
        char[] charArray = originalString.toCharArray();
        char tmp = charArray[firstPosition];
        charArray[firstPosition] = charArray[secondPosition];
        charArray[secondPosition] = tmp;
        return String.valueOf(charArray);
    }
}
