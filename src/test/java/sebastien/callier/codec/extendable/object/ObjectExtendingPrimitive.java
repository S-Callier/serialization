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

import java.util.Objects;

/**
 * @author Sebastien Callier
 * @since 2017
 */
public class ObjectExtendingPrimitive extends PrimitiveObject {
    private Boolean bool2;

    private Byte byt2;
    private Character cha2;
    private Double doubl2;
    private Float floa2;
    private Integer in2;
    private Long lon2;
    private Short shor2;

    public Boolean getBool2() {
        return bool2;
    }

    public void setBool2(Boolean bool2) {
        this.bool2 = bool2;
    }

    public Byte getByt2() {
        return byt2;
    }

    public void setByt2(Byte byt2) {
        this.byt2 = byt2;
    }

    public Character getCha2() {
        return cha2;
    }

    public void setCha2(Character cha2) {
        this.cha2 = cha2;
    }

    public Double getDoubl2() {
        return doubl2;
    }

    public void setDoubl2(Double doubl2) {
        this.doubl2 = doubl2;
    }

    public Float getFloa2() {
        return floa2;
    }

    public void setFloa2(Float floa2) {
        this.floa2 = floa2;
    }

    public Integer getIn2() {
        return in2;
    }

    public void setIn2(Integer in2) {
        this.in2 = in2;
    }

    public Long getLon2() {
        return lon2;
    }

    public void setLon2(Long lon2) {
        this.lon2 = lon2;
    }

    public Short getShor2() {
        return shor2;
    }

    public void setShor2(Short shor2) {
        this.shor2 = shor2;
    }

    @Override
    public boolean equals(Object o) {
        if (!o.getClass().equals(ObjectExtendingPrimitive.class)) {
            return false;
        }
        ObjectExtendingPrimitive other = (ObjectExtendingPrimitive) o;
        return super.equals(o) &&
               Objects.equals(other.getBool2(), getBool2()) &&
               Objects.equals(other.getByt2(), getByt2()) &&
               Objects.equals(other.getCha2(), getCha2()) &&
               Objects.equals(other.getDoubl2(), getDoubl2()) &&
               Objects.equals(other.getFloa2(), getFloa2()) &&
               Objects.equals(other.getIn2(), getIn2()) &&
               Objects.equals(other.getLon2(), getLon2()) &&
               Objects.equals(other.getShor2(), getShor2());
    }
}
