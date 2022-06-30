package tech.yurimednikov.vertxbook.cashx.models;

public final class ApplicationConfiguration {
    
    private final String databaseUrl;

    public ApplicationConfiguration(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    
}
