package com.elasalle.lamp.data.repository;

import com.elasalle.lamp.model.user.FieldLookupList;

@Repository
public interface FieldLookupListRepository {
    void save(FieldLookupList fieldLookupList);
    void delete(FieldLookupList fieldLookupList);
    FieldLookupList findByFieldId(String fieldId);
}
