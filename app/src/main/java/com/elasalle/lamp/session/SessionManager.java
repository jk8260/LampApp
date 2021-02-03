package com.elasalle.lamp.session;

import com.elasalle.lamp.data.repository.SessionRepository;
import com.elasalle.lamp.login.LoginManager;
import com.elasalle.lamp.model.user.Customer;
import com.elasalle.lamp.model.SystemMessage;
import com.elasalle.lamp.model.user.User;

import javax.inject.Inject;

public class SessionManager {

    private final Session session;
    private final SessionRepository sessionRepository;

    @Inject
    public SessionManager(Session session, SessionRepository sessionRepository) {
        this.session = session;
        this.sessionRepository = sessionRepository;
    }

    public void logout() {
        endSession();
        LoginManager.showLoginScreen();
    }

    public Session session() {
        return session;
    }

    public void startSession(User user) {
        session.clearUser();
        session.setAttribute(Session.SESSION_USER, user);
        sessionRepository.saveUser(user);
    }

    public void endSession() {
        session.clear();
        sessionRepository.clear();
    }

    public User user() {
        User user = session.getUser();
        if (user == null) {
            user = sessionRepository.getUser();
        }
        return user;
    }

    public SystemMessage systemMessage() {
        return (SystemMessage) session.getAttribute(Session.SESSION_SYSTEM_MESSAGE);
    }

    public void clearSystemMessage() {
        session.clearAttribute(Session.SESSION_SYSTEM_MESSAGE);
    }

    public void setAboutTime(String time) {
        session.setAttribute(Session.SESSION_ABOUT_TIMESTAMP, time);
    }

    public String getAboutTime() {
        return (String) session.getAttribute(Session.SESSION_ABOUT_TIMESTAMP);
    }

    public void setSearchTime(String time) {
        session.setAttribute(Session.SESSION_SEARCH_TIMESTAMP, time);
    }

    public String getSearchTime() {
        return (String) session.getAttribute(Session.SESSION_SEARCH_TIMESTAMP);
    }

    public void setLookupTime(String time) {
        session.setAttribute(Session.SESSION_LOOKUP_TIMESTAMP, time);
    }

    public String getLookupTime() {
        return (String) session.getAttribute(Session.SESSION_LOOKUP_TIMESTAMP);
    }

    public void setScanTime(String time) {
        session.setAttribute(Session.SESSION_SCAN_TIMESTAMP, time);
    }

    public String getScanTime() {
        return (String) session.getAttribute(Session.SESSION_SCAN_TIMESTAMP);
    }

    public void setNotificationsTime(String time) {
        session.setAttribute(Session.SESSION_NOTIFICATIONS_TIMESTAMP, time);
    }

    public String getNotificationsTime() {
        return (String) session.getAttribute(Session.SESSION_NOTIFICATIONS_TIMESTAMP);
    }

    public void setSystemMessage(SystemMessage systemMessage) {
        session.setAttribute(Session.SESSION_SYSTEM_MESSAGE, systemMessage);
    }

    public void setCustomerDetails(Customer customer) {
        customer.removeToken();
        session.setAttribute(Session.SESSION_CUSTOMER_DETAILS, customer);
        sessionRepository.saveCustomer(customer);
    }

    public Customer getCustomerDetails() {
        Customer customer = session.getCustomerDetails();
        if (customer == null) {
            customer = sessionRepository.getCustomer();
        }
        return customer;
    }

    public void changeCurrentCustomerOnUser(final String customerId) {
        final User changedCustomerUser = user().changeCustomer(customerId);
        session.setAttribute(Session.SESSION_USER, changedCustomerUser);
        sessionRepository.saveUser(changedCustomerUser);
    }

    public String token() {
        return (String) session.getAttribute(Session.SESSION_TOKEN);
    }

    public void token(String token) {
        session.setAttribute(Session.SESSION_TOKEN, token);
    }
}
