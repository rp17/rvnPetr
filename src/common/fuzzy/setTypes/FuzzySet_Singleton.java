/**
 * Desc:   This defines a fuzzy set that is a singleton (a range
 *         over which the DOM is always 1.0)
 * 
 * @author Petr (http://www.sallyx.org/)
 */
package common.fuzzy.setTypes;

public class FuzzySet_Singleton extends FuzzySet {
    //the values that define the shape of this FLV

    private double m_dMidPoint;
    private double m_dLeftOffset;
    private double m_dRightOffset;

    public FuzzySet_Singleton(double mid,
            double lft,
            double rgt) {
        super(mid);
        m_dMidPoint = mid;
        m_dLeftOffset = lft;
        m_dRightOffset = rgt;
    }

    /**
     * this method calculates the degree of membership for a particular value
     */
    @Override
    public double CalculateDOM(double val) {
        if ((val >= m_dMidPoint - m_dLeftOffset)
                && (val <= m_dMidPoint + m_dRightOffset)) {
            return 1.0;
        } //out of range of this FLV, return zero
        else {
            return 0.0;
        }
    }
}