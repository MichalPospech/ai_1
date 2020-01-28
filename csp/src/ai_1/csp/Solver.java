package ai_1.csp;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Solver {

    private List<Variable> variables;
    private List<Constraint> constraints;
    private List<Variable> sortedVars;
    private List<Assignment> infers;
    // Create a Solver for a problem with _n_ variables.
    public Solver(int n) {
        variables = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            variables.add(new Variable(i));
        }
        constraints = new ArrayList<>();
        sortedVars = new ArrayList<>(variables);
        infers = new ArrayList<>();
    }

    // Add a constraint to the problem.
    public void add(Constraint c) {
        for (Integer var : c.getVarsInt()) {
            variables.get(var).addConstraint(c);
            c.addVar(variables.get(var));
        }
        constraints.add(c);
        sortedVars.sort((v1, v2) -> {
            return -Integer.compare(v1.constraintNum(), v2.constraintNum());
        });
    }

    // Assign a fixed value to a variable.
    public void setVar(int i, boolean v) {
        variables.get(i).set(v);
        infers.removeIf(a -> a.var == i);
    }
    public void unsetVar(int i){
        variables.get(i).unset();
    }

    // Deduce that some variable must have a certain value, and assign that value
    // to the variable.  Return the assignment that was made, or null if
    // no deduction was possible.
    public Assignment solve() {
        if (infers.size()>0){
            var assign = infers.get(0);
            infers.remove(0);
            return assign;
        }
        if(constraints.stream().anyMatch(Constraint::isCheckable)){
            var vars = infer();
            if(vars!=null && vars.size()>0){
                infers= vars.stream().map(v-> new Assignment(v.num,v.value())).collect(Collectors.toList());
                var result = infers.get(0);
                infers.remove(0);
                return result;
            }
        }
        return deduce();

    }

    private List<Variable> infer() {
        var vars = new ArrayList<Variable>();
        var contradiction = false;
        for (var constraint : constraints.stream().filter(Constraint::isCheckable).collect(Collectors.toList())) {// for all constraints that have to be checked
            if (constraint.remaining() == constraint.count() - constraint.set()) {
                for (Variable variable : constraint.getVars().stream().filter(v -> !v.hasValue()).collect(Collectors.toList())) {
                    variable.set(true);
                    vars.add(variable);
                }

            } else if (constraint.remaining() == 0 && constraint.set() < constraint.count()) {
                for (Variable variable : constraint.getVars().stream().filter(v -> !v.hasValue()).collect(Collectors.toList())) {
                    variable.set(false);
                    vars.add(variable);
                }
            } else if (constraint.remaining() < 0) {
                //too many true
                contradiction = true;
                break;
            } else if (constraint.remaining() > constraint.count() - constraint.set()) {
                //not enough true
                contradiction = true;
                break;
            }
        }

        if(contradiction){
            for (Variable var : vars) {
                var.unset();
            }
            return null;
        }
        if (vars.size()>0){
            var newVars = infer();
            if(newVars == null){
                for (Variable var : vars) {
                    var.unset();
                }
                return null;
            }
            vars.addAll(newVars);
        }
        return vars;
    }

    private boolean assign(int i, boolean v){
        setVar(i, v);
        var inferred = infer();
        if (inferred==null){
            unsetVar(i);
            return false;
        }
        var exists = backtrack();
        unsetVar(i);
        for (Variable variable : inferred) {
            variable.unset();
        }
        return exists;
    }
    private boolean backtrack(){
        var var = sortedVars.stream().filter(v -> !v.hasValue()).findFirst();
        if (var.isEmpty()){
            return true;
        }
        var actualVar = var.get();
        var correct = assign(actualVar.num, true);

        if(correct){
            return true;
        }else{
            correct = assign(actualVar.num,false);
            return correct;
        }
    }

    private Assignment deduce(){
        var unsetVars = sortedVars.stream().filter(v -> !v.hasValue()).collect(Collectors.toList());
        for (Variable unsetVar : unsetVars) {
           var truePossible = assign(unsetVar.num,true);
           var falsePossible = assign(unsetVar.num, false);
           if (truePossible) {
               if (!falsePossible) {
                   unsetVar.set(true);
                   return new Assignment(unsetVar.num, true);
               }
           }
           if(falsePossible){
               if (!truePossible){
                   unsetVar.set(false);
                   return new Assignment(unsetVar.num, false);

               }
           }
        }
        return null;
    }

}
