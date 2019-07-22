package com.agileengine;

import com.agileengine.parser.Parser;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


public class Demo {

    private static final Parser PARSER = new Parser();
    private static final Set<String> attributesValues = new LinkedHashSet<>();
    private static final Set<Elements> possibleElements = new LinkedHashSet<>();
    private static Logger LOGGER = LoggerFactory.getLogger(Demo.class);

    public static void main(String[] args) {
        String originResourcePath = args[0];
        String secondResourcePath = args[1];

        final Element originElement = findElementById(PARSER.parseFile(new File(originResourcePath)));
        LOGGER.info("Target element attrs: [{}]", originElement);
        final Attributes attributes = originElement.attributes();
        attributes.forEach(attribute -> attributesValues.add(attribute.getValue()));
        attributes.forEach(attribute -> possibleElements.add(PARSER.parseFile(
                new File(secondResourcePath)).getElementsByAttributeValue(attribute.getKey(), attribute.getValue())));
        LOGGER.info("The most similar element is: [{}]", getTheMostSimilarElement());
        LOGGER.info("With path: [{}]", getPath());
    }

    private static Element getTheMostSimilarElement() {
        int amountOfSimilarElements = 0;
        int theMaxNumberOfSimilarities = 0;
        Element similarElement = null;

        for (Elements elements : possibleElements) {
            for (Element element : elements) {
                for (Attribute attribute : element.attributes()) {
                    if (attributesValues.contains(attribute.getValue())) {
                        amountOfSimilarElements++;
                    }
                }
                if (amountOfSimilarElements > theMaxNumberOfSimilarities) {
                    theMaxNumberOfSimilarities = amountOfSimilarElements;
                    similarElement = element;
                }
                amountOfSimilarElements = 0;
            }
        }
        return similarElement;
    }

    private static Element findElementById(Document document) {
        return Optional.of(document.getElementById(com.agileengine.utilities.Constants.TARGET_ELEMENT_ID))
                .orElseThrow(() -> new RuntimeException("Element wasn't found"));
    }

    private static String getPath() {
        Element element = getTheMostSimilarElement();
        Elements elementsParents = element.parents();
        elementsParents.add(element);
        String output = elementsParents.stream()
                .map(el -> el.nodeName() + "{" + el.elementSiblingIndex() + "}")
                .collect(Collectors.joining(" > "));
        writeOutputToFile(output);
        return output;
    }

    private static void writeOutputToFile(String output) {
        try {
            FileWriter fw = new FileWriter("./samples/output.txt");
            fw.write(output);
            fw.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        LOGGER.info("Output was successfully written to file");
    }
}