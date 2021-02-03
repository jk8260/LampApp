package com.elasalle.lamp.data.repository;

import com.elasalle.lamp.scan.model.NewScanSet;

import java.util.List;

@Repository
public interface NewScanSetRepository {
    void save(NewScanSet newScanSet);
    List<NewScanSet> findAll();
    List<NewScanSet> findAllByCustomerId(String customerId);
    NewScanSet findById(String id);
    void deleteOne(NewScanSet newScanSet);
    void deleteAll();
}
