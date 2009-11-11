package ca.couchware.wezzle2d.util;

/**
 *
 * @author cdmckay
 */
public class Rational
{
    private int numerator;
    private int denominator;

    public Rational(int numerator, int denominator)
    {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public int getDenominator()
    {
        return denominator;
    }

    public int getNumerator()
    {
        return numerator;
    }
    
}
