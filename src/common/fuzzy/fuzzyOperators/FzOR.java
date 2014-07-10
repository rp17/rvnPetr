/**
 *  a fuzzy OR operator class
 * 
 * @author Petr (http://www.sallyx.org/)
 */
package common.fuzzy.fuzzyOperators;

import common.fuzzy.FuzzyTerm;
import static common.misc.utils.MinFloat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FzOR extends FuzzyTerm {
    //an instance of this class may AND together up to 4 terms

    private List<FuzzyTerm> m_Terms = new ArrayList(4);

    //no assignment op necessary
    //FzOR& operator=(const FzOR&);
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        m_Terms.clear();
    }

    //copy ctor
    public FzOR(FzOR fa) {
        Iterator<FuzzyTerm> curTerm = fa.m_Terms.iterator();
        while (curTerm.hasNext()) {
            m_Terms.add(curTerm.next().Clone());
        }
    }

    //ctors accepting fuzzy terms.
    /**
     * ctor using two terms
     */
    public FzOR(FuzzyTerm op1, FuzzyTerm op2) {
        m_Terms.add(op1.Clone());
        m_Terms.add(op2.Clone());
    }

    /**
     * ctor using three terms
     */
    public FzOR(FuzzyTerm op1, FuzzyTerm op2, FuzzyTerm op3) {
        m_Terms.add(op1.Clone());
        m_Terms.add(op2.Clone());
        m_Terms.add(op3.Clone());
    }

    /**
     * ctor using four terms
     */
    public FzOR(FuzzyTerm op1, FuzzyTerm op2, FuzzyTerm op3, FuzzyTerm op4) {
        m_Terms.add(op1.Clone());
        m_Terms.add(op2.Clone());
        m_Terms.add(op3.Clone());
        m_Terms.add(op4.Clone());
    }

    //virtual ctor
    public FuzzyTerm Clone() {
        return new FzOR(this);
    }

    /**
     * the OR operator returns the maximum DOM of the sets it is operating on
     */
    @Override
    public double GetDOM() {
        double largest = MinFloat;

        Iterator<FuzzyTerm> it = m_Terms.iterator();
        while (it.hasNext()) {
            FuzzyTerm curTerm = it.next();
            if (curTerm.GetDOM() > largest) {
                largest = curTerm.GetDOM();
            }
        }

        return largest;
    }

    //unused
    @Override
    public void ClearDOM() {
        assert false : "<FzOR::ClearDOM>: invalid context";
    }

    @Override
    public void ORwithDOM(double val) {
        assert false : "<FzOR::ORwithDOM>: invalid context";
    }
}
