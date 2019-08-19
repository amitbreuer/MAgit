
package generated;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="preceding-commit" maxOccurs="2">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "precedingCommit"
})
@XmlRootElement(name = "preceding-commits")
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
public class PrecedingCommits {

    @XmlElement(name = "preceding-commit", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected List<PrecedingCommits.PrecedingCommit> precedingCommit;

    /**
     * Gets the value of the precedingCommit property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the precedingCommit property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPrecedingCommit().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PrecedingCommits.PrecedingCommit }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public List<PrecedingCommits.PrecedingCommit> getPrecedingCommit() {
        if (precedingCommit == null) {
            precedingCommit = new ArrayList<PrecedingCommits.PrecedingCommit>();
        }
        return this.precedingCommit;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public static class PrecedingCommit {

        @XmlAttribute(name = "id", required = true)
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
        protected String id;

        /**
         * Gets the value of the id property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
        public String getId() {
            return id;
        }

        /**
         * Sets the value of the id property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
        public void setId(String value) {
            this.id = value;
        }

    }

}
