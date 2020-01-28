package ai_1.csp;

import java.util.*;

public class SolverTest {
    static void check(boolean b) {
        if (!b)
            throw new Error("assertion failed");
    }

    static Assignment v(int var, boolean val) {
        return new Assignment(var, val);
    }

    static boolean equal(List<Assignment> l, List<Assignment> m) {
        if (l.size() != m.size())
            return false;

        for (int i = 0 ; i < l.size() ; ++i)
            if (l.get(i).var != m.get(i).var || l.get(i).val != m.get(i).val)
                return false;

        return true;
    }

    static void printAssignments(List<Assignment> l) {
        for (int i = 0 ; i < l.size() ; ++i) {
            if (i > 0)
                System.out.print(", ");
            Assignment a = l.get(i);
            System.out.format("%d -> %b", a.var, a.val);
        }
        System.out.println();
    }

    static boolean checkDeduction(Solver s, List<Assignment> m, boolean setVars) {
        System.out.println("should deduce:");
        printAssignments(m);

        List<Assignment> l = new ArrayList<Assignment>();
        while (true) {
            Assignment a = s.solve();
            if (a == null)
                break;
            l.add(a);
            if (setVars) {
                // check that we can repeat the assignment of a known value
                s.setVar(a.var, a.val);
            }
        }
        Collections.sort(l, (a, b) -> Integer.compare(a.var, b.var));
        if (!equal(l, m)) {
            System.out.println("wrong assignments were deduced:");
            printAssignments(l);
            return false;
        }
        System.out.println("deduced correctly");
        return true;
    }

    static boolean checkDeduction(Solver s, List<Assignment> m) {
        return checkDeduction(s, m, false);
    }	

    static Solver solver4() {
        Solver s = new Solver(4);
        s.add(new Constraint(1, List.of(0, 1)));
        s.add(new Constraint(1, List.of(1, 2)));
        s.add(new Constraint(1, List.of(2, 3)));
        return s;
    }

    static boolean test_a() {
        Solver s = new Solver(3);
        s.add(new Constraint(1, List.of(0)));
        s.add(new Constraint(1, List.of(1)));
        s.add(new Constraint(0, List.of(2)));
        return checkDeduction(s, List.of(v(0, true), v(1, true), v(2, false)));
    }

    static boolean test_b(boolean setVars) {
        Solver s = solver4();
        s.add(new Constraint(2, List.of(0, 2, 3)));

        return checkDeduction(s, List.of(v(0, true), v(1, false), v(2, true), v(3, false)), setVars);
    }

    public static boolean test1() {
        Solver s = solver4();
        if (s.solve() != null) {
            System.out.println("error: no deduction should be possible");
            return false;
        }

        s.setVar(0, false);
        return checkDeduction(s, List.of(v(1, true), v(2, false), v(3, true)), true);
    }
    
    public static boolean test2() {
        // Test a situation on a 3 x 3 Minesweeper board.
        Solver s = new Solver(9);
        
        for (int i : List.of(2, 5, 6))
            s.setVar(i, false);
        
        s.add(new Constraint(1, List.of(1, 4, 5)));
        s.add(new Constraint(1, List.of(1, 2, 4, 7, 8)));
        s.add(new Constraint(1, List.of(3, 4, 7)));
        
        return checkDeduction(s, List.of(v(7, false), v(8, false)));
    }
    
    public static boolean test3() {
        // Test a situation on a 4 x 4 Minesweeper board.
        Solver s = new Solver(16);
        
        for (int i = 0 ; i < 8 ; ++i)
            s.setVar(i, false);
        
        s.add(new Constraint(1, List.of(0, 1, 5, 8, 9)));
        s.add(new Constraint(1, List.of(0, 1, 2, 4, 6, 8, 9, 10)));
        s.add(new Constraint(1, List.of(1, 2, 3, 5, 7, 9, 10, 11)));
        s.add(new Constraint(1, List.of(2, 3, 6, 10, 11)));
        
        return checkDeduction(s, List.of(v(8, true), v(9, false), v(10, false), v(11, true)));
    }
    
    public static boolean test4() {
        // Test a situation on a 4 x 4 Minesweeper board.
        Solver s = new Solver(16);

        for (int i = 4; i < 16; ++i)
            if (i % 4 == 0 || i % 4 == 1)
                s.setVar(i, false);

        s.add(new Constraint(1, List.of(0, 1, 5, 8, 9)));
        s.add(new Constraint(2, List.of(0, 1, 2, 4, 6, 8, 9, 10)));
        s.add(new Constraint(2, List.of(4, 5, 6, 8, 10, 12, 13, 14)));
        s.add(new Constraint(2, List.of(8, 9, 10, 12, 14)));

        return checkDeduction(s, List.of(v(2, false), v(6, false), v(10, true), v(14, true)));
    }
    
    public static boolean test5() {
        // Test a situation on a 5 x 5 Minesweeper board.
        Solver s = new Solver(25);
        
        for (int i : List.of(10, 11, 12, 13, 14, 15, 16, 18, 19, 20, 21, 23, 24))
            s.setVar(i, false);
        
        s.add(new Constraint(1, List.of(5, 6, 11, 15, 16)));
        s.add(new Constraint(2, List.of(5, 6, 7, 10, 12, 15, 16, 17)));
        s.add(new Constraint(1, List.of(10, 11, 12, 15, 17, 20, 21, 22)));
        s.add(new Constraint(1, List.of(15, 16, 17, 20, 22)));
        s.add(new Constraint(3, List.of(6, 7, 8, 11, 13, 16, 17, 18)));
        s.add(new Constraint(2, List.of(7, 8, 9, 12, 14, 17, 18, 19)));
        s.add(new Constraint(1, List.of(8, 9, 13, 18, 19)));
        s.add(new Constraint(1, List.of(12, 13, 14, 17, 19, 22, 23, 24)));
        s.add(new Constraint(1, List.of(17, 18, 19, 22, 24)));
        
        return checkDeduction(s, List.of(v(5, false), v(6, true), v(8, true), v(9, false)));
    }

    public static void test(String[] args) {
        if (test_a() && test_b(false) && test_b(true) &&
            test1() && test2() && test3() && test4() && test5())
            System.out.println("all tests succeeded");
    }
    public static void main(String[] args) {
        test(new String[]{});
    }
}
