package com.elasalle.lamp.module;

import com.elasalle.lamp.data.DataStoreManager;
import com.elasalle.lamp.data.mapper.realm.AssetEntityMapper;
import com.elasalle.lamp.data.mapper.realm.CustomerEntityMapper;
import com.elasalle.lamp.data.mapper.realm.FieldEntityMapper;
import com.elasalle.lamp.data.mapper.realm.FieldLookupListEntityMapper;
import com.elasalle.lamp.data.mapper.realm.GuestLoginEntityMapper;
import com.elasalle.lamp.data.mapper.realm.LookupLIstItemEntityMapper;
import com.elasalle.lamp.data.mapper.realm.NewScanSetEntityMapper;
import com.elasalle.lamp.data.mapper.realm.NotificationEntityMapper;
import com.elasalle.lamp.data.mapper.realm.ScanSetEntityMapper;
import com.elasalle.lamp.data.mapper.realm.ScanSetFieldEntityMapper;
import com.elasalle.lamp.data.mapper.realm.SearchTypeEntityMapper;
import com.elasalle.lamp.data.mapper.realm.UserEntityMapper;
import com.elasalle.lamp.data.repository.FieldLookupListRepository;
import com.elasalle.lamp.data.repository.GuestLoginRepository;
import com.elasalle.lamp.data.repository.NewScanSetRepository;
import com.elasalle.lamp.data.repository.NotificationRepository;
import com.elasalle.lamp.data.repository.ScanSetRepository;
import com.elasalle.lamp.data.repository.SessionRepository;
import com.elasalle.lamp.data.repository.UserRepository;
import com.elasalle.lamp.data.repository.impl.realm.FieldLookupListRepositoryImpl;
import com.elasalle.lamp.data.repository.impl.realm.GuestLoginRepositoryImpl;
import com.elasalle.lamp.data.repository.impl.realm.NewScanSetRepositoryImpl;
import com.elasalle.lamp.data.repository.impl.realm.NotificationRepositoryImpl;
import com.elasalle.lamp.data.repository.impl.realm.ScanSetRepositoryImpl;
import com.elasalle.lamp.data.repository.impl.realm.SessionRepositoryImpl;
import com.elasalle.lamp.data.repository.impl.realm.UserRepositoryImpl;
import com.elasalle.lamp.model.user.User;

import javax.inject.Singleton;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;

@Module
@Singleton
public class DataModule {

    @Provides
    DataStoreManager dataManager(Lazy<User> user) {
        return new DataStoreManager(user);
    }

    @Provides
    FieldEntityMapper fieldEntityMapper() {
        return new FieldEntityMapper();
    }

    @Provides
    SearchTypeEntityMapper searchTypeEntityMapper() {
        return new SearchTypeEntityMapper();
    }

    @Provides
    CustomerEntityMapper customerEntityMapper(FieldEntityMapper fieldEntityMapper, SearchTypeEntityMapper searchTypeEntityMapper) {
        return new CustomerEntityMapper(fieldEntityMapper, searchTypeEntityMapper);
    }

    @Provides
    UserEntityMapper userEntityMapper(CustomerEntityMapper customerEntityMapper) {
        return new UserEntityMapper(customerEntityMapper);
    }

    @Provides
    GuestLoginEntityMapper guestLoginEntityMapper() {
        return new GuestLoginEntityMapper();
    }

    @Provides
    UserRepository userRepository(DataStoreManager dataStoreManager, UserEntityMapper userEntityMapper) {
        return new UserRepositoryImpl(dataStoreManager, userEntityMapper);
    }

    @Provides
    GuestLoginRepository guestLoginRepository(DataStoreManager dataStoreManager, GuestLoginEntityMapper guestLoginEntityMapper) {
        return new GuestLoginRepositoryImpl(dataStoreManager, guestLoginEntityMapper);
    }

    @Provides
    SessionRepository sessionRepository(DataStoreManager dataStoreManager, UserEntityMapper userEntityMapper, CustomerEntityMapper customerEntityMapper) {
        return new SessionRepositoryImpl(dataStoreManager, userEntityMapper, customerEntityMapper);
    }

    @Provides
    ScanSetRepository scanSetRepository(DataStoreManager dataStoreManager, ScanSetEntityMapper scanSetEntityMapper) {
        return new ScanSetRepositoryImpl(dataStoreManager, scanSetEntityMapper);
    }

    @Provides
    ScanSetEntityMapper scanSetEntityMapper(AssetEntityMapper assetEntityMapper) {
        return new ScanSetEntityMapper(assetEntityMapper);
    }

    @Provides
    ScanSetFieldEntityMapper scanSetFieldEntityMapper(FieldEntityMapper fieldEntityMapper) {
        return new ScanSetFieldEntityMapper(fieldEntityMapper);
    }

    @Provides
    NewScanSetEntityMapper newScanSetEntityMapper(ScanSetFieldEntityMapper scanSetFieldEntityMapper, AssetEntityMapper assetEntityMapper) {
        return new NewScanSetEntityMapper(scanSetFieldEntityMapper, assetEntityMapper);
    }

    @Provides
    NewScanSetRepository newScanSetRepository(DataStoreManager dataStoreManager, NewScanSetEntityMapper newScanSetEntityMapper) {
        return new NewScanSetRepositoryImpl(dataStoreManager, newScanSetEntityMapper);
    }

    @Provides
    LookupLIstItemEntityMapper lookupLIstItemEntityMapper() {
        return new LookupLIstItemEntityMapper();
    }

    @Provides
    FieldLookupListEntityMapper fieldLookupListEntityMapper(FieldEntityMapper fieldEntityMapper, LookupLIstItemEntityMapper lookupLIstItemEntityMapper) {
        return new FieldLookupListEntityMapper(fieldEntityMapper, lookupLIstItemEntityMapper);
    }

    @Provides
    FieldLookupListRepository fieldLookupListRepository(DataStoreManager dataStoreManager, FieldLookupListEntityMapper mapper) {
        return new FieldLookupListRepositoryImpl(dataStoreManager, mapper);
    }

    @Provides
    AssetEntityMapper assetEntityMapper(FieldEntityMapper fieldEntityMapper) {
        return new AssetEntityMapper(fieldEntityMapper);
    }

    @Provides
    NotificationEntityMapper notificationEntityMapper() {
        return new NotificationEntityMapper();
    }

    @Provides
    NotificationRepository notificationRepository(DataStoreManager dataStoreManager, NotificationEntityMapper mapper) {
        return new NotificationRepositoryImpl(dataStoreManager, mapper);
    }

}
