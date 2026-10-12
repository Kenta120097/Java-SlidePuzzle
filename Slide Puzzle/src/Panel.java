import java.util.*;

public class Panel {
    public int number;
    public int size;
    public List<Integer> states;
    public Panel(int number, int size) {
        this.number = number;
        this.size = size;
    }
    public void push(List<Integer> state) {
        int pos = state.indexOf(number);
        int zero = state.indexOf(0);
        int row1 = pos / size, col1 = pos % size, row2 = zero / size, col2 = zero % size;

        if ((Math.abs(row1 - row2) + Math.abs(col1 - col2)) == 1) {
            Collections.swap(state, pos, zero);
        }
        this.states = new ArrayList<>(state);
    }
}