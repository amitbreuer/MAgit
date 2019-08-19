
package generated;

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
 *         &lt;element ref="{}name"/>
 *         &lt;element name="pointed-commit">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="tracking-after" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="is-remote" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="tracking" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "name",
    "pointedCommit",
    "trackingAfter"
})
@XmlRootElement(name = "MagitSingleBranch")
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
public class MagitSingleBranch {

    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String name;
    @XmlElement(name = "pointed-commit", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected MagitSingleBranch.PointedCommit pointedCommit;
    @XmlElement(name = "tracking-after")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String trackingAfter;
    @XmlAttribute(name = "is-remote")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected Boolean isRemote;
    @XmlAttribute(name = "tracking")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected Boolean tracking;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the pointedCommit property.
     * 
     * @return
     *     possible object is
     *     {@link MagitSingleBranch.PointedCommit }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public MagitSingleBranch.PointedCommit getPointedCommit() {
        return pointedCommit;
    }

    /**
     * Sets the value of the pointedCommit property.
     * 
     * @param value
     *     allowed object is
     *     {@link MagitSingleBranch.PointedCommit }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setPointedCommit(MagitSingleBranch.PointedCommit value) {
        this.pointedCommit = value;
    }

    /**
     * Gets the value of the trackingAfter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getTrackingAfter() {
        return trackingAfter;
    }

    /**
     * Sets the value of the trackingAfter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setTrackingAfter(String value) {
        this.trackingAfter = value;
    }

    /**
     * Gets the value of the isRemote property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public boolean isIsRemote() {
        if (isRemote == null) {
            return false;
        } else {
            return isRemote;
        }
    }

    /**
     * Sets the value of the isRemote property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setIsRemote(Boolean value) {
        this.isRemote = value;
    }

    /**
     * Gets the value of the tracking property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public boolean isTracking() {
        if (tracking == null) {
            return false;
        } else {
            return tracking;
        }
    }

    /**
     * Sets the value of the tracking property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setTracking(Boolean value) {
        this.tracking = value;
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
    public static class PointedCommit {

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
