package org.mifosplatform.portfolio.search.data;

import org.mifosplatform.portfolio.search.SearchConstants.SEARCH_SUPPORTED_RESOURCES;


public class SearchConditions {
    
    private final String searchQuery;
    private final String searchResource;
    private final Boolean clientSearch;
    private final Boolean groupSearch;
    private final Boolean loanSeach;
    private final Boolean clientIdentifierSearch;
    public SearchConditions(String searchQueryParam, String searchResource) {
        this.searchQuery = searchQueryParam;
        this.searchResource = searchResource;
        this.clientSearch = (null == searchResource || searchResource.toLowerCase().contains(SEARCH_SUPPORTED_RESOURCES.CLIENTS.name().toLowerCase()))? true : false;
        this.groupSearch = (null == searchResource || searchResource.toLowerCase().contains(SEARCH_SUPPORTED_RESOURCES.GROUPS.name().toLowerCase()))? true : false;
        this.loanSeach = (null == searchResource || searchResource.toLowerCase().contains(SEARCH_SUPPORTED_RESOURCES.LOANS.name().toLowerCase()))? true : false;
        this.clientIdentifierSearch = (null == searchResource || searchResource.toLowerCase().contains(SEARCH_SUPPORTED_RESOURCES.CLIENTIDENTIFIERS.name().toLowerCase()))? true : false;
    }
    public SearchConditions(String searchQueryParam, String searchResource, Boolean clientSearch, Boolean groupSearch, Boolean loanSeach, Boolean clientIdentifierSearch) {
        this.searchQuery = searchQueryParam;
        this.searchResource = searchResource;
        this.clientSearch = clientSearch;
        this.groupSearch = groupSearch;
        this.loanSeach = loanSeach;
        this.clientIdentifierSearch = clientIdentifierSearch;
    }
    
    public String getSearchQuery() {
        return this.searchQuery;
    }
    
    public String getSearchResource() {
        return this.searchResource;
    }
    
    public Boolean isClientSearch() {
        return this.clientSearch;
    }
    
    public Boolean isGroupSearch() {
        return this.groupSearch;
    }
    
    public Boolean isLoanSeach() {
        return this.loanSeach;
    }
    
    public Boolean isClientIdentifierSearch() {
        return this.clientIdentifierSearch;
    }

    
}
