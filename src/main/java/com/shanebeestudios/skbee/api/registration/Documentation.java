package com.shanebeestudios.skbee.api.registration;

public class Documentation {

    private boolean noDoc;
    private String name;
    private String[] description;
    private String[] examples;
    private String[] since;
    private String[] keywords;

    public void setNoDoc(boolean noDoc) {
        this.noDoc = noDoc;
    }

    public boolean isNoDoc() {
        return noDoc;
    }

    void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    void setDescription(String[] description) {
        this.description = description;
    }

    String[] getDescription() {
        return this.description;
    }

    void setExamples(String[] examples) {
        this.examples = examples;
    }

    String[] getExamples() {
        return this.examples;
    }

    void setSince(String[] since) {
        this.since = since;
    }

    String[] getSince() {
        return this.since;
    }

    void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }

    String[] getKeywords() {
        return this.keywords;
    }

}
