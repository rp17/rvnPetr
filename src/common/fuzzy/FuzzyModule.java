/**
 * Desc:   this class describes a fuzzy module: a collection of fuzzy variables
 *         and the rules that operate on them.
 * 
 * @author Petr (http://www.sallyx.org/)
 */
package common.fuzzy;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class FuzzyModule {

    private class VarMap extends HashMap<String, FuzzyVariable> {
        public VarMap() {
            super();
        }
    };

    /**
     * you must pass one of these values to the defuzzify method. This module
     * only supports the MaxAv and centroid methods.
     */
    public static enum DefuzzifyMethod {

        max_av, centroid
    };
    /**
     * when calculating the centroid of the fuzzy manifold this value is used
     * to determine how many cross-sections should be sampled
     */
    public static final int NumSamples = 15;
    //a map of all the fuzzy variables this module uses
    private VarMap m_Variables =  new VarMap();
    /**
     * a vector containing all the fuzzy rules
     */
    private List<FuzzyRule> m_Rules = new ArrayList<FuzzyRule>();

    /**
     * zeros the DOMs of the consequents of each rule. Used by Defuzzify()
     */
    private void SetConfidencesOfConsequentsToZero() {
        Iterator<FuzzyRule> curRule = m_Rules.iterator();
        while (curRule.hasNext()) {
            curRule.next().SetConfidenceOfConsequentToZero();
        }
    }

    //------------------------------ dtor -----------------------------------------
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        m_Variables.clear();
        m_Rules.clear();
    }

    /**
     *  creates a new 'empty' fuzzy variable and returns a reference to it.
     */
    public FuzzyVariable CreateFLV(String VarName) {
        m_Variables.put(VarName, new FuzzyVariable());
        return m_Variables.get(VarName);
    }

    /**
     * adds a rule to the module
     */
    public void AddRule(FuzzyTerm antecedent, FuzzyTerm consequence) {
        m_Rules.add(new FuzzyRule(antecedent, consequence));
    }

    /**
     * this method calls the Fuzzify method of the variable with the same name
     *  as the key
     */
    public void Fuzzify(String NameOfFLV, double val) {
        //first make sure the key exists
        assert (m_Variables.get(NameOfFLV) != null) :
                "<FuzzyModule::Fuzzify>:key not found";

        m_Variables.get(NameOfFLV).Fuzzify(val);
    }

    /**
     * given a fuzzy variable and a deffuzification method this returns a 
     * crisp value
     */
    public double DeFuzzify(String NameOfFLV, DefuzzifyMethod method) {
        //first make sure the key exists
        assert (m_Variables.get(NameOfFLV) != null) :
                "<FuzzyModule::DeFuzzifyMaxAv>:key not found";

        //clear the DOMs of all the consequents of all the rules
        SetConfidencesOfConsequentsToZero();

        //process the rules
        Iterator<FuzzyRule> curRule = m_Rules.iterator();
        while (curRule.hasNext()) {
            curRule.next().Calculate();
        }

        //now defuzzify the resultant conclusion using the specified method
        switch (method) {
            case centroid:
                return m_Variables.get(NameOfFLV).DeFuzzifyCentroid(NumSamples);
            case max_av:
                return m_Variables.get(NameOfFLV).DeFuzzifyMaxAv();
        }

        return 0.0;
    }

    /**
     * writes the DOMs of all the variables in the module to an output stream
     */
    public PrintStream WriteAllDOMs(PrintStream os) {
        os.print("\n\n");

        Set<Entry<String, FuzzyVariable>> set = m_Variables.entrySet();
        Iterator<Entry<String, FuzzyVariable>> it = set.iterator();
        while (it.hasNext()) {
            Entry<String, FuzzyVariable> curVar = it.next();
            os.print("\n--------------------------- ");
            os.print(curVar.getKey() + " " + curVar.getValue().WriteDOMs(os));
            os.println();
        }

        return os;
    }
}