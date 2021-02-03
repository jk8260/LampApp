package com.elasalle.lamp.data.repository;

import com.elasalle.lamp.model.scan.ScanSet;

import java.util.List;

@Repository
public interface ScanSetRepository {
    void save(ScanSet set);
    List<ScanSet> findAll();
    List<ScanSet> findAllByCustomerId(String customerId);
    ScanSet findById(String id);
    void deleteOne(ScanSet set);
    void deleteAll();
}
