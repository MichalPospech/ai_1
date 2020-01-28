package ai_1.csp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Variable {
    private Collection<Constraint> constraints;
    private Boolean value;
    int num;

    public Variable(int i) {
        value = null;
        constraints = new ArrayList<>() {};
        num = i;
    }

    public void addConstraint(Constraint constraint){
        constraints.add(constraint);
    }

    public boolean hasValue(){
        return !(value==null);
    }
    public boolean value(){
        return value;
    }

    public int constraintNum(){
        return constraints.size();
    }

    Collection<Constraint> getConstraints(){
        return Collections.unmodifiableCollection(constraints);
    }

    public void set(boolean v){
        if(hasValue() && !value.equals(v)){
            throw new RuntimeException("Invalid assignment");
        }
        value = v;
        for (Constraint variableConstraint : getConstraints()) {
            variableConstraint.addCheckRequest();
        }
    }
    public void unset(){
        value = null;
        for (Constraint variableConstraint : getConstraints()) {
            variableConstraint.removeCheckRequest();
        }
    }
}
