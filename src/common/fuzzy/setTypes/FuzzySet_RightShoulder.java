/**
 *  Desc:   definition of a fuzzy set that has a right shoulder shape. (the
 *          maximum value this variable can accept is *any* value greater than 
 *          the midpoint.
 * 
 * @author Petr (http://www.sallyx.org/)
 */
package common.fuzzy.setTypes;

import static common.misc.utils.isEqual;

public class FuzzySet_RightShoulder extends FuzzySet {
//the values that define the shape of this FLV
    private double m_dPeakPoint;
    private double m_dLeftOffset;
    private double m_dRightOffset;

    public FuzzySet_RightShoulder(double peak,
            double LeftOffset,
            double RightOffset) {

        super(((peak + RightOffset) + peak) / 2);
        m_dPeakPoint = peak;
        m_dLeftOffset = LeftOffset;
        m_dRightOffset = RightOffset;

    }

    /**
     * this method calculates the degree of membership for a particular value
     */
    @Override
    public double CalculateDOM(double val) {
        //test for the case where the left or right offsets are zero
        //(to prevent divide by zero errors below)
        if ((isEqual(m_dRightOffset, 0.0) && (isEqual(m_dPeakPoint, val)))
                || (isEqual(m_dLeftOffset, 0.0) && (isEqual(m_dPeakPoint, val)))) {
            return 1.0;
        } //find DOM if left of center
        else if ((val <= m_dPeakPoint) && (val > (m_dPeakPoint - m_dLeftOffset))) {
            double grad = 1.0 / m_dLeftOffset;

            return grad * (val - (m_dPeakPoint - m_dLeftOffset));
        } //find DOM if right of center and less than center + right offset
        else if ((val > m_dPeakPoint) && (val <= m_dPeakPoint + m_dRightOffset)) {
            return 1.0;
        } else {
            return 0;
        }
    }
}
