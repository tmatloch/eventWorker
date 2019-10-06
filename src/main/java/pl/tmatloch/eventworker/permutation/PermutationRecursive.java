package pl.tmatloch.eventworker.permutation;

import java.util.ArrayList;
import java.util.List;

public class PermutationRecursive implements Permutation {

    private final List<String> permutationResult = new ArrayList<>();

    private void permute(String string, int startIndex, int endIndex){
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

    @Override
    public void calculatePermutations(String text) {
        int textLength = text.length();
        permute(text, 0, textLength - 1);
    }

    @Override
    public List<String> getPermutationResults() {
        return permutationResult;
    }
}
