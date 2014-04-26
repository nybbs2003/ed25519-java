package net.i2p.crypto.eddsa.math;

import java.math.BigInteger;

/**
 * A twisted Edwards curve.
 * Points on the curve satisfy -x^2 + y^2 = 1 + d x^2y^2
 * @author str4d
 *
 */
public class Curve {
    private Field f;
    private FieldElement d;
    private FieldElement d2;
    private FieldElement I;

    public Curve(Field f, BigInteger d) {
        this.f = f;
        this.d = fromBigInteger(d);
        this.d2 = this.d.multiply(fromBigInteger(Constants.TWO));
        this.I = fromBigInteger(Constants.TWO).modPow(f.getQ().subtract(Constants.ONE).divide(Constants.FOUR), f.getQ());
    }

    public Field getField() {
        return f;
    }

    public FieldElement getD() {
        return d;
    }

    public FieldElement get2D() {
        return d2;
    }

    public FieldElement getI() {
        return I;
    }

    public FieldElement fromBigInteger(BigInteger x) {
        return new FieldElement(f, x);
    }

    public FieldElement fromByteArray(byte[] x) {
        return new FieldElement(f, x);
    }

    public GroupElement createPoint(BigInteger x, BigInteger y) {
        return GroupElement.p2(this, fromBigInteger(x), fromBigInteger(y), fromBigInteger(Constants.ONE));
    }

    /**
     * Verify that a point is on the curve.
     * @param P The point to check.
     * @return true if the point lies on the curve.
     */
    public boolean isOnCurve(GroupElement P) {
        switch (P.repr) {
        case P2:
        case P3:
            FieldElement recip = P.Z.invert();
            FieldElement x = P.X.multiply(recip);
            FieldElement y = P.Y.multiply(recip);
            FieldElement xx = x.square();
            FieldElement yy = y.square();
            FieldElement dxxyy = d.multiply(xx).multiply(yy);
            return fromBigInteger(Constants.ONE).add(dxxyy).add(xx).subtract(yy).equals(fromBigInteger(Constants.ZERO));

        default:
            return isOnCurve(P.toP2());
        }
    }
}
