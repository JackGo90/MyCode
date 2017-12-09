
package com.redsun.bos.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.redsun.bos.ws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _WaybilldetailListResponse_QNAME = new QName("http://ws.bos.redsun.com/", "waybilldetailListResponse");
    private final static QName _AddResponse_QNAME = new QName("http://ws.bos.redsun.com/", "addResponse");
    private final static QName _Add_QNAME = new QName("http://ws.bos.redsun.com/", "add");
    private final static QName _WaybilldetailList_QNAME = new QName("http://ws.bos.redsun.com/", "waybilldetailList");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.redsun.bos.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AddResponse }
     * 
     */
    public AddResponse createAddResponse() {
        return new AddResponse();
    }

    /**
     * Create an instance of {@link WaybilldetailListResponse }
     * 
     */
    public WaybilldetailListResponse createWaybilldetailListResponse() {
        return new WaybilldetailListResponse();
    }

    /**
     * Create an instance of {@link WaybilldetailList }
     * 
     */
    public WaybilldetailList createWaybilldetailList() {
        return new WaybilldetailList();
    }

    /**
     * Create an instance of {@link Add }
     * 
     */
    public Add createAdd() {
        return new Add();
    }

    /**
     * Create an instance of {@link Waybilldetail }
     * 
     */
    public Waybilldetail createWaybilldetail() {
        return new Waybilldetail();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WaybilldetailListResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.bos.redsun.com/", name = "waybilldetailListResponse")
    public JAXBElement<WaybilldetailListResponse> createWaybilldetailListResponse(WaybilldetailListResponse value) {
        return new JAXBElement<WaybilldetailListResponse>(_WaybilldetailListResponse_QNAME, WaybilldetailListResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.bos.redsun.com/", name = "addResponse")
    public JAXBElement<AddResponse> createAddResponse(AddResponse value) {
        return new JAXBElement<AddResponse>(_AddResponse_QNAME, AddResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Add }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.bos.redsun.com/", name = "add")
    public JAXBElement<Add> createAdd(Add value) {
        return new JAXBElement<Add>(_Add_QNAME, Add.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WaybilldetailList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.bos.redsun.com/", name = "waybilldetailList")
    public JAXBElement<WaybilldetailList> createWaybilldetailList(WaybilldetailList value) {
        return new JAXBElement<WaybilldetailList>(_WaybilldetailList_QNAME, WaybilldetailList.class, null, value);
    }

}
