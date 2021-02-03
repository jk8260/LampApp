package com.elasalle.lamp.data.mapper.realm;

import com.elasalle.lamp.data.entity.realm.CustomerEntity;
import com.elasalle.lamp.data.entity.realm.FieldEntity;
import com.elasalle.lamp.data.entity.realm.SearchTypeEntity;
import com.elasalle.lamp.data.entity.realm.RealmString;
import com.elasalle.lamp.data.mapper.Cartographer;
import com.elasalle.lamp.model.user.Customer;
import com.elasalle.lamp.model.user.Field;
import com.elasalle.lamp.model.user.SearchType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.realm.RealmList;

public class CustomerEntityMapper extends Cartographer<CustomerEntity, Customer> {

    private final FieldEntityMapper fieldEntityMapper;
    private final SearchTypeEntityMapper searchTypeEntityMapper;

    @Inject
    public CustomerEntityMapper(FieldEntityMapper fieldEntityMapper, SearchTypeEntityMapper searchTypeEntityMapper) {
        this.fieldEntityMapper = fieldEntityMapper;
        this.searchTypeEntityMapper = searchTypeEntityMapper;
    }

    @Override
    public CustomerEntity modelToEntity(Customer model) {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.customerId = model.getCustomerId();
        customerEntity.name = model.getName();
        customerEntity.logoUrl = model.getLogoUrl();
        customerEntity.fields = mapFields(model.getFields());
        customerEntity.privileges = mapPrivileges(model.getPrivileges());
        customerEntity.searchTypes = mapSearchTypes(model.getSearchTypes());
        return customerEntity;
    }

    private RealmList<RealmString> mapPrivileges(List<String> privileges) {
        RealmList<RealmString> realmStrings = new RealmList<>();
        for (String privilege : privileges) {
            RealmString realmString = new RealmString();
            realmString.value = privilege;
            realmStrings.add(realmString);
        }
        return realmStrings;
    }

    private RealmList<SearchTypeEntity> mapSearchTypes(List<SearchType> searchTypes) {
        RealmList<SearchTypeEntity> searchTypeEntities = new RealmList<>();
        for (SearchType searchType : searchTypes) {
            SearchTypeEntity searchTypeEntity = searchTypeEntityMapper.modelToEntity(searchType);
            searchTypeEntities.add(searchTypeEntity);
        }
        return searchTypeEntities;
    }

    private RealmList<FieldEntity> mapFields(List<Field> fields) {
        RealmList<FieldEntity> fieldEntities = new RealmList<>();
        for (Field field : fields) {
            FieldEntity fieldEntity = fieldEntityMapper.modelToEntity(field);
            fieldEntities.add(fieldEntity);
        }
        return fieldEntities;
    }

    @Override
    public Customer entityToModel(CustomerEntity entity) {
        Customer customer = new Customer();
        customer.setCustomerId(entity.customerId);
        customer.setName(entity.name);
        customer.setLogoUrl(entity.logoUrl);
        customer.setFields(mapFieldEntities(entity.fields));
        customer.setPrivileges(mapPrivilegeEntities(entity.privileges));
        customer.setSearchTypes(mapSearchTypeEntities(entity.searchTypes));
        return customer;
    }

    private List<String> mapPrivilegeEntities(RealmList<RealmString> privileges) {
        List<String> stringList = new ArrayList<>();
        for (RealmString realmString : privileges) {
            String value = realmString.value;
            stringList.add(value);
        }
        return stringList;
    }

    private List<SearchType> mapSearchTypeEntities(List<SearchTypeEntity> searchTypeEntities) {
        List<SearchType> searchTypes = new ArrayList<>();
        for (SearchTypeEntity entity : searchTypeEntities) {
            SearchType searchType = searchTypeEntityMapper.entityToModel(entity);
            searchTypes.add(searchType);
        }
        return searchTypes;
    }

    private List<Field> mapFieldEntities(List<FieldEntity> fieldEntities) {
        List<Field> fieldList = new ArrayList<>();
        for (FieldEntity entity : fieldEntities) {
            Field field = fieldEntityMapper.entityToModel(entity);
            fieldList.add(field);
        }
        return fieldList;
    }
}
