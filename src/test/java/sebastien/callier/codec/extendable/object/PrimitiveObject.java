/*
 * Copyright 2017 Sebastien Callier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sebastien.callier.codec.extendable.object;

/**
 * @author Sebastien Callier
 * @since 2017
 */
@SuppressWarnings({"WeakerAccess", "SameParameterValue"})
public class PrimitiveObject {
    private boolean bool;
    private byte byt;
    private char cha;
    private double doubl;
    private float floa;
    private int in;
    private long lon;
    private short shor;

    public boolean isBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }

    public byte getByt() {
        return byt;
    }

    public void setByt(byte byt) {
        this.byt = byt;
    }

    public char getCha() {
        return cha;
    }

    public void setCha(char cha) {
        this.cha = cha;
    }

    public double getDoubl() {
        return doubl;
    }

    public void setDoubl(double doubl) {
        this.doubl = doubl;
    }

    public float getFloa() {
        return floa;
    }

    public void setFloa(float floa) {
        this.floa = floa;
    }

    public int getIn() {
        return in;
    }

    public void setIn(int in) {
        this.in = in;
    }

    public long getLon() {
        return lon;
    }

    public void setLon(long lon) {
        this.lon = lon;
    }

    public short getShor() {
        return shor;
    }

    public void setShor(short shor) {
        this.shor = shor;
    }

    @Override
    public boolean equals(Object o) {
        if (!PrimitiveObject.class.isAssignableFrom(o.getClass())) {
            return false;
        }
        PrimitiveObject other = (PrimitiveObject) o;
        return other.bool == bool &&
               other.byt == byt &&
               other.cha == cha &&
               other.doubl == doubl &&//avoid NaN
               other.floa == floa &&
               other.in == in &&
               other.lon == lon &&
               other.shor == shor;
    }
}
