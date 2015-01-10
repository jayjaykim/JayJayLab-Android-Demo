package com.jayjaylab.androiddemo.app.greyhound.util;

import android.location.Location;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import roboguice.util.Ln;

/**
 * Created by jongjoo on 1/10/15.
 */
public class GPXParser {
    private static final String PROVIDER = "jayjaylab";

    private static Location currentLocation;
    private static List<Location> list;

    /**
     * Parses the given gpx file in <var>absolutePath</var>
     * @param absolutePath an absolute path of gpx file
     * @return List a list storing Location objects
     */
    public static List<Location> parse(String absolutePath) {
        if(absolutePath == null || !new File(absolutePath).exists()) {
            return null;
        }

        if(list == null) {
            list = new ArrayList<Location>(40);
        } else {
            list.clear();
        }

        SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        try {
            InputStream xmlInput = new FileInputStream(absolutePath);
            SAXParser saxParser = saxFactory.newSAXParser();
            saxParser.parse(xmlInput, handler);
        } catch(Exception e) {
            Ln.e(e);
        }

        return list;
    }

    private static final DefaultHandler handler = new DefaultHandler() {
        @Override
        public void startDocument() throws SAXException {
            Ln.d("startDocument()");
        }

        @Override
        public void endDocument() throws SAXException {
            Ln.d("endDocument()");
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            Ln.d("endElement() : qName : %s", qName);

            if(qName.equals("trkpt")) {
                list.add(currentLocation);
                currentLocation = null;
            }
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            Ln.d("startElement() : qName : %s", qName);
            if(qName.equals("trkpt")) {
                currentLocation = new Location(PROVIDER);
                currentLocation.setLatitude(Double.valueOf(attributes.getValue("lat")));
                currentLocation.setLongitude(Double.valueOf(attributes.getValue("lon")));
            }
        }

        @Override
        public void error(SAXParseException e) throws SAXException {
            Ln.d("error()");
        }

        @Override
        public void fatalError(SAXParseException e) throws SAXException {
            Ln.d("fatalError()");
        }

        @Override
        public void warning(SAXParseException e) throws SAXException {
            Ln.d("warning()");
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            Ln.d("characters()");
        }
    };
}
