package com.fuzzyDucks.fms.classification;

import com.mongodb.client.FindIterable;
import org.bson.Document;

public class FileUtils {

    public static boolean isEmpty(FindIterable<Document> docs){
        if (docs.first() == null) {
            throw new IllegalArgumentException("No files");
        }
        return true;
    }
}