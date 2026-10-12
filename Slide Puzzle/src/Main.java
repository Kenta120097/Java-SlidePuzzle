import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class Main {
    static int level = 1;
    public static void main(String[] args) {
        List<Integer> state = new ArrayList<>(Arrays.asList(1,2,3,0));
        boolean noRead = true;

        try(FileReader fr = new FileReader("save.txt")){
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            if(line != null) {
                level = Integer.parseInt(line);
                state.clear();
                noRead = false;
                for (int i = 0; i < Math.pow(Math.ceil(Math.cbrt(level) + 1), 2); i++) {
                    line = reader.readLine();
                    if(line != null)state.add(Integer.valueOf(line));
                    else{
                        level = 1;
                        state = new ArrayList<>(Arrays.asList(1,2,3,0));
                        noRead = true;
                        break;
                    }
                }
            }
        }catch(FileNotFoundException e){
            try {
                new File("save.txt").createNewFile();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }catch(IOException e){
            System.out.println(e);
        }

        JFrame frame = new JFrame("Slide Puzzle");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);

        game(state, frame, noRead);
    }

    public static void game(List<Integer> state, JFrame frame, boolean mix) {
        int size = (int) Math.ceil(Math.cbrt(level) + 1);
        if(mix) {
            List<Integer> shuffle = new ArrayList<>();
            for (int i = 0; i < (Math.pow(size, 2) - 1) * (Math.pow(size, 2) - 2) / 2; i++) {
                shuffle.add(i);
            }
            Collections.shuffle(shuffle);
            for (int i = 0; i < (level - Math.pow(size - 2, 3)) * 2; i++) {
                int count = 0;
                for (int j = 0; j < Math.pow(size, 2) - 1; j++) {
                    if (shuffle.get(i) < count + Math.pow(size, 2) - j - 2) {
                        int temp = state.get(j);
                        state.set(j, state.get(j + shuffle.get(i) - count + 1));
                        state.set(j + shuffle.get(i) - count + 1, temp);
                        break;
                    }
                    count += (int) (Math.pow(size, 2) - j - 2);
                }
            }
        }

        frame.setSize(size * 100, size * 100);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(frame, "状態を保存して終了しますか？", "終了確認", JOptionPane.YES_NO_CANCEL_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    try (PrintWriter writer = new PrintWriter(new FileWriter("save.txt"))) {
                        writer.println(level);
                        for (Integer num : state) {
                            writer.println(num);
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, "保存に失敗しました: " + ex.getMessage());
                    }
                    System.exit(0);

                } else if (result == JOptionPane.NO_OPTION) {
                    System.exit(0);
                }
            }
        });

        operation(size, state, frame);
    }

    public static void operation(int size, List<Integer> state, JFrame frame) {
        JButton[] btn = new JButton[(int) (Math.pow(size, 2))];
        for (int i = 0; i < btn.length; i++) {
            final int j = i;
            new Panel(state.get(i), size);
            btn[i] = new JButton(String.valueOf(state.get(i)));
            btn[i].setSize(100, 100);
            btn[i].setFont(new Font("Arial", Font.PLAIN ,20));
            btn[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Panel panel = new Panel(state.get(j), size);
                    panel.push(state);

                    for (int k = 0; k < btn.length; k++) {
                        btn[k].setText(String.valueOf(state.get(k)));
                        btn[k].setVisible(state.get(k) != 0);
                    }

                    if(state.getLast() == 0){
                        if(clear(size, state)){
                            if(Math.ceil(Math.cbrt(level)) == Math.cbrt(level)){
                                for(int k = 0; k < size * 2 + 1; k++)state.add(1);
                            }
                            for(int k = 1; k <= Math.pow(Math.ceil(Math.cbrt(level + 1) + 1), 2); k++) {
                                state.set(k-1, k);
                            }
                            state.set(state.size() - 1, 0);
                            level++;
                            game(state, frame, true);
                        }
                    }
                }
            });
            btn[i].setVisible(true);
            if (state.get(i) == 0) btn[i].setVisible(false);
        }

        Container c = frame.getContentPane();
        c.removeAll();
        c.setLayout(new GridLayout(size, size));
        for (int i = 0; i < btn.length; i++) {
            c.add(btn[i]);
        }
        frame.revalidate();
        frame.repaint();
    }

    public static boolean clear(int size, List<Integer> state) {
        for (int i = 0; i < Math.pow(size, 2) - 2; i++) {
            if (state.get(i) > state.get(i + 1)) return false;
        }
        return true;
    }
}