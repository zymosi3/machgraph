package org.github.zymosi3.mg.math;

public final class Mat3 {

    public static final Mat3 ZERO = new Mat3();
    public static final Mat3 IDENTITY = new Mat3(1.0f);

    public final float m00, m01, m02;
    public final float m10, m11, m12;
    public final float m20, m21, m22;

    public Mat3() {
        m00 = m01 = m02 = 0f;
        m10 = m11 = m12 = 0f;
        m20 = m21 = m22 = 0f;
    }

    public Mat3(float diagonalValue) {
        m00 = m11 = m22 = diagonalValue;
        m01 = m02 = 0f;
        m10 = m12 = 0f;
        m20 = m21 = 0f;
    }

    public Mat3(float m00, float m11, float m22) {
        this.m00 = m00;
        this.m11 = m11;
        this.m22 = m22;
        m01 = m02 = 0f;
        m10 = m12 = 0f;
        m20 = m21 = 0f;
    }

    public Mat3(
            float m00, float m01, float m02,
            float m10, float m11, float m12,
            float m20, float m21, float m22
    ) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
    }

    public Mat3(float... mat) {
        assert mat.length >= 9 : "Invalid matrix array length";

        m00 = mat[0];
        m01 = mat[1];
        m02 = mat[2];
        m10 = mat[3];
        m11 = mat[4];
        m12 = mat[5];
        m20 = mat[6];
        m21 = mat[7];
        m22 = mat[8];
    }

    public Mat3(Mat3 mat) {
        this.m00 = mat.m00;
        this.m01 = mat.m01;
        this.m02 = mat.m02;
        this.m10 = mat.m10;
        this.m11 = mat.m11;
        this.m12 = mat.m12;
        this.m20 = mat.m20;
        this.m21 = mat.m21;
        this.m22 = mat.m22;
    }

    public Mat3 multiply(float a) {
        return new Mat3(
                m00 * a, m01 * a, m02 * a,
                m10 * a, m11 * a, m12 * a,
                m20 * a, m21 * a, m22 * a
        );
    }

    public Mat3 multiply(Mat3 mat) {
        return new Mat3(
                this.m00 * mat.m00 + this.m01 * mat.m10 + this.m02 * mat.m20,
                this.m00 * mat.m01 + this.m01 * mat.m11 + this.m02 * mat.m21,
                this.m00 * mat.m02 + this.m01 * mat.m12 + this.m02 * mat.m22,

                this.m10 * mat.m00 + this.m11 * mat.m10 + this.m12 * mat.m20,
                this.m10 * mat.m01 + this.m11 * mat.m11 + this.m12 * mat.m21,
                this.m10 * mat.m02 + this.m11 * mat.m12 + this.m12 * mat.m22,

                this.m20 * mat.m00 + this.m21 * mat.m10 + this.m22 * mat.m20,
                this.m20 * mat.m01 + this.m21 * mat.m11 + this.m22 * mat.m21,
                this.m20 * mat.m02 + this.m21 * mat.m12 + this.m22 * mat.m22
        );
    }

    public Vec3 multiply(Vec3 vec) {
        return new Vec3(
                m00 * vec.x + m01 * vec.y + m02 * vec.z,
                m10 * vec.x + m11 * vec.y + m12 * vec.z,
                m20 * vec.x + m21 * vec.y + m22 * vec.z
        );
    }

    public Mat3 transpose() {
        return new Mat3(
                m00, m10, m20,
                m01, m11, m21,
                m02, m12, m22
        );
    }

    public float determinant() {
        return m00 * (m11 * m22 - m12 * m21) - m01 * (m10 * m22 - m12 * m20) + m02 * (m10 * m21 - m11 * m20);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mat3 mat3 = (Mat3) o;

        if (Float.compare(mat3.m00, m00) != 0) return false;
        if (Float.compare(mat3.m01, m01) != 0) return false;
        if (Float.compare(mat3.m02, m02) != 0) return false;
        if (Float.compare(mat3.m10, m10) != 0) return false;
        if (Float.compare(mat3.m11, m11) != 0) return false;
        if (Float.compare(mat3.m12, m12) != 0) return false;
        if (Float.compare(mat3.m20, m20) != 0) return false;
        if (Float.compare(mat3.m21, m21) != 0) return false;
        return Float.compare(mat3.m22, m22) == 0;
    }

    @Override
    public int hashCode() {
        int result = (m00 != +0.0f ? Float.floatToIntBits(m00) : 0);
        result = 31 * result + (m01 != +0.0f ? Float.floatToIntBits(m01) : 0);
        result = 31 * result + (m02 != +0.0f ? Float.floatToIntBits(m02) : 0);
        result = 31 * result + (m10 != +0.0f ? Float.floatToIntBits(m10) : 0);
        result = 31 * result + (m11 != +0.0f ? Float.floatToIntBits(m11) : 0);
        result = 31 * result + (m12 != +0.0f ? Float.floatToIntBits(m12) : 0);
        result = 31 * result + (m20 != +0.0f ? Float.floatToIntBits(m20) : 0);
        result = 31 * result + (m21 != +0.0f ? Float.floatToIntBits(m21) : 0);
        result = 31 * result + (m22 != +0.0f ? Float.floatToIntBits(m22) : 0);
        return result;
    }

    @Override
    public String toString() {
        return
                String.format("|%8.5f %8.5f %8.5f|\n", m00, m01, m02) +
                String.format("|%8.5f %8.5f %8.5f|\n", m10, m11, m12) +
                String.format("|%8.5f %8.5f %8.5f|\n", m20, m21, m22);
    }
}
