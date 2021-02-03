package com.elasalle.lamp.module;

import com.elasalle.lamp.data.repository.SessionRepository;
import com.elasalle.lamp.session.Session;
import com.elasalle.lamp.session.SessionManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
@Singleton
public class ApplicationModule {

    @Provides
    Session session() {
        return new Session();
    }

    @Provides
    SessionManager sessionManager(Session session, SessionRepository sessionRepository) {
        return new SessionManager(session, sessionRepository);
    }
}
