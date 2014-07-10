/**
 * @author Petr (http://www.sallyx.org/)
 */
package common.fuzzy.fuzzyOperators;

import common.fuzzy.FuzzyTerm;
import static common.misc.utils.MaxDouble;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FzAND extends FuzzyTerm {
    /**
     * an instance of this class may AND together up to 4 terms
     */
    private List<FuzzyTerm> m_Terms = new ArrayList<FuzzyTerm>(4);

    //disallow assignment
    //private FzzAnd& operator=(const FzzAnd&);
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        m_Terms.clear();
    }

    /**
     * copy ctor
     */
    public FzAND(FzAND fa) {
        Iterator<FuzzyTerm> curTerm = fa.m_Terms.iterator();
        while(curTerm.hasNext()) {
            m_Terms.add(curTerm.next().Clone());
        }
    }

    //ctors accepting fuzzy terms.
    /**
     * ctor using two terms
     */
    public FzAND(FuzzyTerm op1, FuzzyTerm op2) {
        m_Terms.add(op1.Clone());
        m_Terms.add(op2.Clone());
    }

    /**
     * ctor using three terms
     */
    public FzAND(FuzzyTerm op1, FuzzyTerm op2, FuzzyTerm op3) {
        m_Terms.add(op1.Clone());
        m_Terms.add(op2.Clone());
        m_Terms.add(op3.Clone());
    }

    /**
     * ctor using four terms
     */
    public FzAND(FuzzyTerm op1, FuzzyTerm op2, FuzzyTerm op3, FuzzyTerm op4) {
        m_Terms.add(op1.Clone());
        m_Terms.add(op2.Clone());
        m_Terms.add(op3.Clone());
        m_Terms.add(op4.Clone());
    }

    /**
     * virtual ctor
     */
    @Override
    public FuzzyTerm Clone() {
        return new FzAND(this);
    }

    /**
     *  the AND operator returns the minimum DOM of the sets it is operating on
     */
    @Override
    public double GetDOM() {
        double smallest = MaxDouble;
        Iterator<FuzzyTerm> it = m_Terms.iterator();
        while (it.hasNext()) {
            FuzzyTerm curTerm = it.next();
            if (curTerm.GetDOM() < smallest) {
                smallest = curTerm.GetDOM();
            }
        }
        
        return smallest;
    }

    @Override
    public void ClearDOM() {
        Iterator<FuzzyTerm> curTerm = m_Terms.iterator();
        while (curTerm.hasNext()) {
            curTerm.next().ClearDOM();
        }
    }

    @Override
    public void ORwithDOM(double val) {
        Iterator<FuzzyTerm> curTerm = m_Terms.iterator();
        while (curTerm.hasNext()) {
            curTerm.next().ORwithDOM(val);
        }
    }
}
