/**
 *  Desc:  Class to define a fuzzy linguistic variable (FLV).
 *         An FLV comprises of a number of fuzzy sets  
 * @author Petr (http://www.sallyx.org/)
 */
package common.fuzzy;

import common.fuzzy.setTypes.FuzzySet;
import common.fuzzy.setTypes.FuzzySet_LeftShoulder;
import common.fuzzy.setTypes.FuzzySet_RightShoulder;
import common.fuzzy.setTypes.FuzzySet_Singleton;
import common.fuzzy.setTypes.FuzzySet_Triangle;
import static common.misc.utils.MinOf;
import static common.misc.utils.isEqual;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class FuzzyVariable {

    private class MemberSets extends HashMap<String, FuzzySet> {
    };

    //disallow copies
    private FuzzyVariable(FuzzyVariable fv) {
        throw new UnsupportedOperationException("Unsupported operation");
    }
    //private FuzzyVariable& operator=(const FuzzyVariable&);
    /**
     * a map of the fuzzy sets that comprise this variable
     */
    private MemberSets m_MemberSets = new MemberSets();
    //the minimum and maximum value of the range of this variable
    private double m_dMinRange;
    private double m_dMaxRange;

    /**
     * this method is called with the upper and lower bound of a set each time a
     * new set is added to adjust the upper and lower range values accordingly
     */
    private void AdjustRangeToFit(double minBound, double maxBound) {
        if (minBound < m_dMinRange) {
            m_dMinRange = minBound;
        }
        if (maxBound > m_dMaxRange) {
            m_dMaxRange = maxBound;
        }
    }

    /**
     * a client retrieves a reference to a fuzzy variable when an instance is
     * created via FuzzyModule::CreateFLV(). To prevent the client from deleting
     * the instance the FuzzyVariable destructor is made private and the 
     * FuzzyModule class made a friend.
     */
    //friend FuzzyModule;
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        m_MemberSets.clear();
    }

    public FuzzyVariable() {
        m_dMinRange = 0.0;
        m_dMaxRange = 0.0;
    }

    //the following methods create instances of the sets named in the method
    //name and add them to the member set map. Each time a set of any type is
    //added the m_dMinRange and m_dMaxRange are adjusted accordingly. All of the
    //methods return a proxy class representing the newly created instance. This
    //proxy set can be used as an operand when creating the rule base.
    /**
     * adds a left shoulder type set
     */
    public FzSet AddLeftShoulderSet(String name,
            double minBound,
            double peak,
            double maxBound) {
        m_MemberSets.put(name, new FuzzySet_LeftShoulder(peak, peak - minBound, maxBound - peak));

        //adjust range if necessary
        AdjustRangeToFit(minBound, maxBound);

        return new FzSet(m_MemberSets.get(name));
    }

    /**
     *  adds a left shoulder type set
     */
    public FzSet AddRightShoulderSet(String name,
            double minBound,
            double peak,
            double maxBound) {
        m_MemberSets.put(name, new FuzzySet_RightShoulder(peak, peak - minBound, maxBound - peak));

        //adjust range if necessary
        AdjustRangeToFit(minBound, maxBound);

        return new FzSet(m_MemberSets.get(name));
    }

    /**
     * adds a triangular shaped fuzzy set to the variable
     */
    public FzSet AddTriangularSet(String name,
            double minBound,
            double peak,
            double maxBound) {
        m_MemberSets.put(name, new FuzzySet_Triangle(peak,
                peak - minBound,
                maxBound - peak));
        //adjust range if necessary
        AdjustRangeToFit(minBound, maxBound);

        return new FzSet(m_MemberSets.get(name));
    }

    /**
     * adds a singleton to the variable
     */
    public FzSet AddSingletonSet(String name,
            double minBound,
            double peak,
            double maxBound) {
        m_MemberSets.put(name, new FuzzySet_Singleton(peak,
                peak - minBound,
                maxBound - peak));

        AdjustRangeToFit(minBound, maxBound);

        return new FzSet(m_MemberSets.get(name));
    }

    /**
     * fuzzify a value by calculating its DOM in each of this variable's subsets
     * takes a crisp value and calculates its degree of membership for each set
     * in the variable.
     */
    void Fuzzify(double val) {
        //make sure the value is within the bounds of this variable
        assert (val >= m_dMinRange) && (val <= m_dMaxRange) :
                "<FuzzyVariable::Fuzzify>: value out of range";

        //for each set in the flv calculate the DOM for the given value
        Set<Entry<String, FuzzySet>> set = m_MemberSets.entrySet();
        Iterator<Entry<String, FuzzySet>> it = set.iterator();
        while (it.hasNext()) {
            Entry<String, FuzzySet> curSet = it.next();
            curSet.getValue().SetDOM(curSet.getValue().CalculateDOM(val));
        }
    }

    /**
     * defuzzifies the value by averaging the maxima of the sets that have fired
     *
     * OUTPUT = sum (maxima * DOM) / sum (DOMs) 
     */
    public double DeFuzzifyMaxAv() {
        double bottom = 0.0;
        double top = 0.0;

        Set<Entry<String, FuzzySet>> set = m_MemberSets.entrySet();
        Iterator<Entry<String, FuzzySet>> it = set.iterator();
        while (it.hasNext()) {
            Entry<String, FuzzySet> curSet = it.next();
            bottom += curSet.getValue().GetDOM();
            top += curSet.getValue().GetRepresentativeVal() * curSet.getValue().GetDOM();
        }

        //make sure bottom is not equal to zero
        if (isEqual(0, bottom)) {
            return 0.0;
        }

        return top / bottom;
    }

    /**
     * defuzzify the variable using the centroid method
     */
    public double DeFuzzifyCentroid(int NumSamples) {
        //calculate the step size
        double StepSize = (m_dMaxRange - m_dMinRange) / (double) NumSamples;

        double TotalArea = 0.0;
        double SumOfMoments = 0.0;

        //step through the range of this variable in increments equal to StepSize
        //adding up the contribution (lower of CalculateDOM or the actual DOM of this
        //variable's fuzzified value) for each subset. This gives an approximation of
        //the total area of the fuzzy manifold.(This is similar to how the area under
        //a curve is calculated using calculus... the heights of lots of 'slices' are
        //summed to give the total area.)
        //
        //in addition the moment of each slice is calculated and summed. Dividing
        //the total area by the sum of the moments gives the centroid. (Just like
        //calculating the center of mass of an object)
        for (int samp = 1; samp <= NumSamples; ++samp) {
            //for each set get the contribution to the area. This is the lower of the 
            //value returned from CalculateDOM or the actual DOM of the fuzzified 
            //value itself   
            Set<Entry<String, FuzzySet>> set = m_MemberSets.entrySet();
            Iterator<Entry<String, FuzzySet>> it = set.iterator();
            while (it.hasNext()) {
                Entry<String, FuzzySet> curSet = it.next();
                double contribution =
                        MinOf(curSet.getValue().CalculateDOM(m_dMinRange + samp * StepSize),
                        curSet.getValue().GetDOM());

                TotalArea += contribution;

                SumOfMoments += (m_dMinRange + samp * StepSize) * contribution;
            }
        }

        //make sure total area is not equal to zero
        if (isEqual(0, TotalArea)) {
            return 0.0;
        }

        return (SumOfMoments / TotalArea);
    }

    public PrintStream WriteDOMs(PrintStream os) {

        Set<Entry<String, FuzzySet>> set = m_MemberSets.entrySet();
        Iterator<Entry<String, FuzzySet>> it = set.iterator();
        while (it.hasNext()) {
            Entry<String, FuzzySet> curSet = it.next();
            os.printf("\n%s is %s", curSet.getKey(), curSet.getValue().GetDOM());
        }

        os.printf("\nMin Range: %f \nMax Range: %f", m_dMinRange, m_dMaxRange);

        return os;
    }
}