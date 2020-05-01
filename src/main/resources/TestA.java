package zl.compiler;

import java.lang.*;
import java.math.*;
import java.util.*;
import java.io.Serializable;

import org.mvel2.MVEL;

public class TestA {

    private static transient Serializable asdfe_mvel = MVEL.compileExpression("\"asdf\"+a");

    private String a;
    public String getA() {
        return new Random().nextDouble()+"";
    }


    public String getAsdfe() {
        String result = (String) MVEL.executeExpression(asdfe_mvel, this);
        return result;
    }

}