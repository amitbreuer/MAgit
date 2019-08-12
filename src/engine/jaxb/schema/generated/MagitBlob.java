
package engine.jaxb.schema.generated;

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
 *       &lt;all>
 *         &lt;element ref="{}name"/>
 *         &lt;element ref="{}last-updater"/>
 *         &lt;element ref="{}last-update-date"/>
 *         &lt;element ref="{}content"/>
 *       &lt;/all>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "MagitBlob")
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
public class MagitBlob {

    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String name;
    @XmlElement(name = "last-updater", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String lastUpdater;
    @XmlElement(name = "last-update-date", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String lastUpdateDate;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String content;
    @XmlAttribute(name = "id", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String id;

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
     * Gets the value of the lastUpdater property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getLastUpdater() {
        return lastUpdater;
    }

    /**
     * Sets the value of the lastUpdater property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setLastUpdater(String value) {
        this.lastUpdater = value;
    }

    /**
     * Gets the value of the lastUpdateDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    /**
     * Sets the value of the lastUpdateDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setLastUpdateDate(String value) {
        this.lastUpdateDate = value;
    }

    /**
     * Gets the value of the content property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getContent() {
        return content;
    }

    /**
     * Sets the value of the content property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setContent(String value) {
        this.content = value;
    }

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
