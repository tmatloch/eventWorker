package pl.tmatloch.eventworker.permutation;

import java.util.ArrayList;
import java.util.List;

public class PermutationIterate implements Permutation{

    private final List<String> permutationResult = new ArrayList<>();

    @Override
    public void calculatePermutations(String text) {
        permutationResult.clear();
        permutationResult.add(String.valueOf(text.charAt(0)));

        for (int i = 1; i < text.length(); i++)
        {
            for (int j = permutationResult.size() - 1; j >= 0 ; j--)
            {
                String str = permutationResult.remove(j);
                for (int k = 0; k <= str.length(); k++)
                {
                    permutationResult.add(str.substring(0, k) + text.charAt(i) +
                            str.substring(k));
                }
            }
        }
    }

    @Override
    public List<String> getPermutationResults() {
        return permutationResult;
    }
}
