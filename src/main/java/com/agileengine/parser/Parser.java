package com.agileengine.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

import static com.agileengine.utilities.Constants.CHARSET_NAME;

public class Parser {

    public Document parseFile(File file) {
        try {
            return Jsoup.parse(
                    file,
                    CHARSET_NAME,
                    file.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Error while parsing file with path: ".concat(file.getAbsolutePath()));
        }
    }
}
