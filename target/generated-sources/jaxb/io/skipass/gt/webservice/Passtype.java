//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.06.19 at 02:54:20 PM CEST 
//


package io.skipass.gt.webservice;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for passtype.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="passtype"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="child"/&gt;
 *     &lt;enumeration value="student"/&gt;
 *     &lt;enumeration value="adult"/&gt;
 *     &lt;enumeration value="senior"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "passtype")
@XmlEnum
public enum Passtype {

    @XmlEnumValue("child")
    CHILD("child"),
    @XmlEnumValue("student")
    STUDENT("student"),
    @XmlEnumValue("adult")
    ADULT("adult"),
    @XmlEnumValue("senior")
    SENIOR("senior");
    private final String value;

    Passtype(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Passtype fromValue(String v) {
        for (Passtype c: Passtype.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
