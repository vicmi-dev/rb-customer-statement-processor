package com.manuel.rb.models.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "records")
public class XmlTransactions {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "record")
    private List<Transaction> xmlTransactions;

    public List<Transaction> getXmlTransactions() {
        return xmlTransactions;
    }

    public void setXmlTransactions(List<Transaction> xmlTransactions) {
        this.xmlTransactions = xmlTransactions;
    }
}