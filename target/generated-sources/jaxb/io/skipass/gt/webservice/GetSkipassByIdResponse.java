//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.06.19 at 02:54:20 PM CEST 
//


package io.skipass.gt.webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="skipass" type="{http://skipass.io/gt/webservice}skipass"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "skipass"
})
@XmlRootElement(name = "getSkipassByIdResponse")
public class GetSkipassByIdResponse {

    @XmlElement(required = true)
    protected Skipass skipass;

    /**
     * Gets the value of the skipass property.
     * 
     * @return
     *     possible object is
     *     {@link Skipass }
     *     
     */
    public Skipass getSkipass() {
        return skipass;
    }

    /**
     * Sets the value of the skipass property.
     * 
     * @param value
     *     allowed object is
     *     {@link Skipass }
     *     
     */
    public void setSkipass(Skipass value) {
        this.skipass = value;
    }

}
