package takred.botpoc;

import java.util.List;

public class FinderBisection {
    public boolean contains(List<Integer> allNumbers, int number){
        int border = allNumbers.size() / 2;
        for (int i = 0; i < allNumbers.size(); i++) {
            if (number < allNumbers.get(border)){
                border = border / 2;
            } else {
                border = border + ((allNumbers.size() - border) / 2);
            }
            System.out.println(allNumbers.get(border));
            if (allNumbers.get(border) == number || allNumbers.get(border + 1) == number){
                return true;
            }
        }
        return false;
    }
}
