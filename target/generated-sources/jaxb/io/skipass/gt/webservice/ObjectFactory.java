//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.06.19 at 02:54:20 PM CEST 
//


package io.skipass.gt.webservice;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the io.skipass.gt.webservice package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: io.skipass.gt.webservice
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetSkipassByIdRequest }
     * 
     */
    public GetSkipassByIdRequest createGetSkipassByIdRequest() {
        return new GetSkipassByIdRequest();
    }

    /**
     * Create an instance of {@link GetSkipassByIdResponse }
     * 
     */
    public GetSkipassByIdResponse createGetSkipassByIdResponse() {
        return new GetSkipassByIdResponse();
    }

    /**
     * Create an instance of {@link Skipass }
     * 
     */
    public Skipass createSkipass() {
        return new Skipass();
    }

    /**
     * Create an instance of {@link GetStockRequest }
     * 
     */
    public GetStockRequest createGetStockRequest() {
        return new GetStockRequest();
    }

    /**
     * Create an instance of {@link GetStockResponse }
     * 
     */
    public GetStockResponse createGetStockResponse() {
        return new GetStockResponse();
    }

    /**
     * Create an instance of {@link PrepareRequest }
     * 
     */
    public PrepareRequest createPrepareRequest() {
        return new PrepareRequest();
    }

    /**
     * Create an instance of {@link Order }
     * 
     */
    public Order createOrder() {
        return new Order();
    }

    /**
     * Create an instance of {@link PrepareResponse }
     * 
     */
    public PrepareResponse createPrepareResponse() {
        return new PrepareResponse();
    }

    /**
     * Create an instance of {@link Vote }
     * 
     */
    public Vote createVote() {
        return new Vote();
    }

    /**
     * Create an instance of {@link BookSkipassRequest }
     * 
     */
    public BookSkipassRequest createBookSkipassRequest() {
        return new BookSkipassRequest();
    }

    /**
     * Create an instance of {@link BookSkipassResponse }
     * 
     */
    public BookSkipassResponse createBookSkipassResponse() {
        return new BookSkipassResponse();
    }

    /**
     * Create an instance of {@link RollBackRequest }
     * 
     */
    public RollBackRequest createRollBackRequest() {
        return new RollBackRequest();
    }

}