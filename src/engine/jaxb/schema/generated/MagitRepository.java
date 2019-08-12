
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
 *       &lt;sequence>
 *         &lt;element ref="{}location"/>
 *         &lt;element ref="{}MagitBlobs"/>
 *         &lt;element ref="{}MagitFolders"/>
 *         &lt;element ref="{}MagitCommits"/>
 *         &lt;element ref="{}MagitBranches"/>
 *         &lt;element name="MagitRemoteReference" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;all>
 *                   &lt;element ref="{}location"/>
 *                   &lt;element ref="{}name"/>
 *                 &lt;/all>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "location",
    "magitBlobs",
    "magitFolders",
    "magitCommits",
    "magitBranches",
    "magitRemoteReference"
})
@XmlRootElement(name = "MagitRepository")
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
public class MagitRepository {

    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String location;
    @XmlElement(name = "MagitBlobs", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected MagitBlobs magitBlobs;
    @XmlElement(name = "MagitFolders", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected MagitFolders magitFolders;
    @XmlElement(name = "MagitCommits", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected MagitCommits magitCommits;
    @XmlElement(name = "MagitBranches", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected MagitBranches magitBranches;
    @XmlElement(name = "MagitRemoteReference")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected MagitRepository.MagitRemoteReference magitRemoteReference;
    @XmlAttribute(name = "name", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    protected String name;

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public String getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setLocation(String value) {
        this.location = value;
    }

    /**
     * Gets the value of the magitBlobs property.
     * 
     * @return
     *     possible object is
     *     {@link MagitBlobs }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public MagitBlobs getMagitBlobs() {
        return magitBlobs;
    }

    /**
     * Sets the value of the magitBlobs property.
     * 
     * @param value
     *     allowed object is
     *     {@link MagitBlobs }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setMagitBlobs(MagitBlobs value) {
        this.magitBlobs = value;
    }

    /**
     * Gets the value of the magitFolders property.
     * 
     * @return
     *     possible object is
     *     {@link MagitFolders }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public MagitFolders getMagitFolders() {
        return magitFolders;
    }

    /**
     * Sets the value of the magitFolders property.
     * 
     * @param value
     *     allowed object is
     *     {@link MagitFolders }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setMagitFolders(MagitFolders value) {
        this.magitFolders = value;
    }

    /**
     * Gets the value of the magitCommits property.
     * 
     * @return
     *     possible object is
     *     {@link MagitCommits }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public MagitCommits getMagitCommits() {
        return magitCommits;
    }

    /**
     * Sets the value of the magitCommits property.
     * 
     * @param value
     *     allowed object is
     *     {@link MagitCommits }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setMagitCommits(MagitCommits value) {
        this.magitCommits = value;
    }

    /**
     * Gets the value of the magitBranches property.
     * 
     * @return
     *     possible object is
     *     {@link MagitBranches }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public MagitBranches getMagitBranches() {
        return magitBranches;
    }

    /**
     * Sets the value of the magitBranches property.
     * 
     * @param value
     *     allowed object is
     *     {@link MagitBranches }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setMagitBranches(MagitBranches value) {
        this.magitBranches = value;
    }

    /**
     * Gets the value of the magitRemoteReference property.
     * 
     * @return
     *     possible object is
     *     {@link MagitRepository.MagitRemoteReference }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public MagitRepository.MagitRemoteReference getMagitRemoteReference() {
        return magitRemoteReference;
    }

    /**
     * Sets the value of the magitRemoteReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link MagitRepository.MagitRemoteReference }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public void setMagitRemoteReference(MagitRepository.MagitRemoteReference value) {
        this.magitRemoteReference = value;
    }

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
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;all>
     *         &lt;element ref="{}location"/>
     *         &lt;element ref="{}name"/>
     *       &lt;/all>
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
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
    public static class MagitRemoteReference {

        @XmlElement(required = true)
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
        protected String location;
        @XmlElement(required = true)
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
        protected String name;

        /**
         * Gets the value of the location property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
        public String getLocation() {
            return location;
        }

        /**
         * Sets the value of the location property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2019-08-11T12:12:34+03:00", comments = "JAXB RI v2.2.8-b130911.1802")
        public void setLocation(String value) {
            this.location = value;
        }

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

    }

}
