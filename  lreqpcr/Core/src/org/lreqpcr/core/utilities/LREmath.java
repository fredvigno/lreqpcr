/*
 * Copyright (C) 2013   Bob Rutledge
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 * and open the template in the editor.
 */

package org.lreqpcr.core.utilities;

/**
 *
 * A collection of static methods for LRE-related calculations
 *
 * @author Bob Rutledge
 */
public class LREmath {
    
    
    /**
     * Calculates Target Quantity in fluorescence units (Fo)
     *
     * <p>Note that any quantitative unit can be used just as long as the
     * Fc is converted to the desired unit (e.g. Nc for molecules or 
     * Mc for mass). Also note that the return value will be in the designated 
     * quantitative unit.
     * 
     * @param  c   the cycle number
     * @param cF   the cycle fluorescence reading (Fc)
     * @param dE   the rate of loss in cycle efficiency (deltaE)
     * @param mE   the maximal amplification efficiency (Emax)
     * @return     the predicted target quantity in fluorescence units (Fo) 
     */
    public static double calcFo(int c, double cF, double dE, double mE) {
        double maxF = (mE)/-dE;
        double trgFo = maxF/(1+(((maxF/cF)-1)*Math.pow(mE+1, c)));
        return trgFo;
    }
    
    /**
     * Calculates Target Quantity in fluorescence units (Fo) using an overridden Emax
     * value
     * 
     * @param  c   the cycle number
     * @param cF   the cycle fluorescence reading (Fc)
     * @param dE   the rate of loss in cycle efficiency (deltaE)
     * @param mE   the maximal amplification efficiency (Emax)
     * @param overriddenEmaxValue the Emax value to use for Fo calculation
     * @return     the predicted target quantity in fluorescence units (Fo) using the specified Emax
     */
    public static double calcFo(int c, double cF, double dE, double mE, double overridenEmaxValue) {
        double maxF = (mE)/-dE;
        //Note that lack of deltaE is the primary basis for the presumed validation of overriding Emax
        double trgFo = maxF/(1+(((maxF/cF)-1)*Math.pow(overridenEmaxValue + 1, c)));
        return trgFo;
    }
    
    /**
     * Calculates predicted cycle fluorescence for one cycle
     *
     *<p>Note that any quantitative unit can be used just as long as the
     *Fo is converted to the desired unit (e.g. No for molecules or 
     *Mo for mass). Also note that the return value will be in the designated 
     * quantitative unit.
     * 
     * @param c the cycle number 
     * @param dE the rate of loss in cycle efficiency (deltaE)
     * @param mE the maximal amplification efficiency (Emax)
     * @param oF the target quantity in fluorescence units (Fo)     
     * @return the predicted cycle fluorescence (pFc)
     */
    public static double calcPrdFc(int c, double dE, double mE, double oF) {
        double mF = (mE)/-dE; //Fmax
        double pFc = mF/(1+(((mF/oF)-1)*Math.pow(mE+1, -c)));
        return pFc;
    }

    /**
     * Calculate predicted Fo using an overridden Emax
     * @param c
     * @param dE
     * @param mE
     * @param oF
     * @param overriddenEmax
     * @return
     */
    public static double calcPrdFc(int c, double dE, double mE, double oF, double overriddenEmax) {
        double mF = (mE)/-dE; //Fmax
        double pFc = mF/(1+(((mF/oF)-1)*Math.pow(overriddenEmax+1, -c)));
        return pFc;
    }

    /**
     * Calculates C1/2 for a profile based on the supplied parameters.
     *
     * @param deltaE the delta E (slope of the LRE plot)
     * @param emax Emax (Y-intercept of the LRE plot)
     * @param avFo the average Fo of this profile
     * @return C1/2 which the fractional cycle at which Fc reaches half of Fmax
     */
    public static double getMidC(double deltaE, double emax, double avFo){
        double maxF = emax/deltaE*-1;
        double numerator = Math.log10((maxF/avFo)-1);
        double denominator = Math.log10(emax + 1);
        double midC = numerator/denominator;
        return midC;
    }

    /**
     * Determines non-linear correlation coefficent (R2) for
     * the predicted Fc within the LRE window
     *
     * @param fcpFc the array holding the values for Fc and predicted Fc: [0, Fc][1, pFc]
     * @return the nonlinear correlation coefficient (R2)
     */
    public static double calcNonLinearR2(double[][] fcpFc) {
        int listLength = fcpFc[0].length;
        double nr2; //R2
        double sumFc = 0; //Sum of the Fc readings across the LRE window
        double avFc; //Average Fc across the LRE window
        double numSum = 0; //Numerator sum
        double demSum = 0; //Denominator sum
        for(int i=0; i<listLength; i++) {
            sumFc += fcpFc[0][i];
        }
        avFc = sumFc/listLength;
        for(int i=0; i<listLength; i++) {
            numSum += Math.pow((fcpFc[0][i])-fcpFc[1][i], 2);
            demSum += Math.pow(fcpFc[0][i] - avFc, 2);
        }
        nr2 = 1-(numSum/demSum);
        return nr2;
    }
    
    /**
     * Determines non-linear correlation coefficent (R2) for
     * the predicted Fc vs. the actual Fc
     *
     * @param fc the observed fluorescence readings (Fc)
     * @param pFc the predicted fluorescence readings (pFc)
     * @return the nonlinear correlation coefficient (R2)
     */
    public static double calcNonLinearR2(double[] fc, double[]pFc) {
        if (fc.length != pFc.length){
            //Should 
            throw new IllegalArgumentException("Number of readings must be equal");
        }
        int listLength = fc.length;
        double nr2; //R2
        double sumFc = 0; //Sum of the Fc readings across the LRE window
        double avFc; //Average Fc across the LRE window
        double numSum = 0; //Numerator sum
        double demSum = 0; //Denominator sum
        for(int i=0; i<listLength; i++) {
            sumFc += fc[i];
        }
        avFc = sumFc/listLength;
        for(int i=0; i<listLength; i++) {
            numSum += Math.pow((fc[i])-pFc[i], 2);
            demSum += Math.pow(pFc[i] - avFc, 2);
        }
        nr2 = 1-(numSum/demSum);
        return nr2;
    }
}
