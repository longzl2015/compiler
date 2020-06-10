package zl.compiler;

import java.lang.*;
import java.math.*;
import java.util.*;
import java.io.Serializable;

import org.mvel2.MVEL;

public class TestA {

    private static transient Serializable asdfe_mvel = MVEL.compileExpression("Math.abs(a)");

    private String a;
    public Long getA() {
        return new Random().nextLong();
    }


    public Long getAsdfe() {
        Long result = (Long) MVEL.executeExpression(asdfe_mvel, this);
        return result;
    }

}