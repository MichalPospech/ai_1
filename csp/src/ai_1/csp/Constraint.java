package ai_1.csp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Constraint {
    private int count;
    private List<Integer> varsInt;
    private List<Variable> vars;
    private int checkRequests;

    public Constraint(int count, List<Integer> vars) {
        this.count = count;
        this.varsInt = vars;
        this.vars = new ArrayList<Variable>();
        checkRequests = 0;
    }

    public List<Integer> getVarsInt(){
        return Collections.unmodifiableList(varsInt);
    }

    public List<Variable> getVars(){
        return Collections.unmodifiableList(vars);
    }

    public void addVar(Variable var){
        vars.add(var);
    }

    public int sum(){
        return ((int) vars.stream().filter(Variable::hasValue).filter(Variable::value).count());
    }

    public int set(){
        return ((int) vars.stream().filter(Variable::hasValue).count());
    }

    public int count(){
        return vars.size();
    }
    public int remaining(){
        return count - sum();
    }

    public boolean isCheckable(){
        return checkRequests > 0;
    }
    public void addCheckRequest(){
        checkRequests++;
    }
    public void removeCheckRequest(){
        if (checkRequests>0){
            --checkRequests;
        }else {
            throw new RuntimeException();
        }
    }

}
