package pl.tmatloch.permutationworker.rabbitmq;

import java.util.ArrayList;
import java.util.List;

class Permutation {

    private final List<String> permutationResult = new ArrayList<>();

    void permute(String string, int startIndex, int endIndex){
        if(startIndex == endIndex){
            permutationResult.add(string);
        } else {
            for(int i = startIndex; i <= endIndex; i++){
                string = swap(string, startIndex, i);
                permute(string, startIndex + 1, endIndex);
                string = swap(string, startIndex, i);
            }
        }
    }

    private String swap(String originalString, int firstPosition, int secondPosition){
        char[] charArray = originalString.toCharArray();
        char tmp = charArray[firstPosition];
        charArray[firstPosition] = charArray[secondPosition];
        charArray[secondPosition] = tmp;
        return String.valueOf(charArray);
    }

    List<String> getPermutationResult() {
        return permutationResult;
    }
}
