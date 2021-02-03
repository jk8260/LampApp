package com.elasalle.lamp.data.mapper.realm;

import com.elasalle.lamp.data.entity.realm.SearchTypeEntity;
import com.elasalle.lamp.data.mapper.Cartographer;
import com.elasalle.lamp.model.user.SearchType;

public class SearchTypeEntityMapper extends Cartographer<SearchTypeEntity, SearchType> {
    @Override
    public SearchTypeEntity modelToEntity(SearchType model) {
        SearchTypeEntity entity = new SearchTypeEntity();
        entity.contentTypeCode = model.getContentTypeCode();
        entity.contentTypeLabel = model.getContentTypeLabel();
        return entity;
    }

    @Override
    public SearchType entityToModel(SearchTypeEntity entity) {
        SearchType searchType = new SearchType();
        searchType.setContentTypeCode(entity.contentTypeCode);
        searchType.setContentTypeLabel(entity.contentTypeLabel);
        return searchType;
    }
}
