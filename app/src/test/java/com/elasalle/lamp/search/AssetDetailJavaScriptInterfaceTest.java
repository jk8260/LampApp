package com.elasalle.lamp.search;

import com.elasalle.lamp.BuildConfig;
import com.elasalle.lamp.LampApp;
import com.elasalle.lamp.model.asset.AssetDetails;
import com.elasalle.lamp.model.search.Datum;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, application = LampApp.class)
public class AssetDetailJavaScriptInterfaceTest {

    @Mock
    AssetDetailActivity activityMock;

    AssetDetailJavaScriptInterface assetDetailJavaScriptInterface;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        assetDetailJavaScriptInterface = new AssetDetailJavaScriptInterface(activityMock);
    }

    @Test
    public void testOnCallback_DetailsJson() {
        // Setup
        final String detailsJson = getDetailsJson();
        // Action
        assetDetailJavaScriptInterface.onCallback(detailsJson);
        // Assertions
        verify(activityMock, times(1)).showDetail((AssetDetails)any());
    }

    @Test
    public void testOnCallback_ListJson() {
        // Setup
        final String listJson = getListJson();
        // Action
        assetDetailJavaScriptInterface.onCallback(listJson);
        // Assertions
        verify(activityMock, times(1)).showListScreen((List<Datum>) any());
    }

    private String getDetailsJson() {
        return "{\"objects\":{\"title\":\"LAMP Ticket \",\"url\":\"https://qa-lamp.elasalle.com/MobileV2/Tickets/Details/5582e152-8024-44ad-acda-a24700c001dc\",\"actions\":[{\"title\":\"your title goes here\",\"type\":\"share\",\"data\":\"your data goes here\"}]},\"type\":\"details\"}";
    }

    private String getListJson() {
        return "{\"objects\":[{\"id\":\"5582e152-8024-44ad-acda-a24700c001dc\",\"target\":\"https://qa-lamp.elasalle.com/MobileV2/Assets/TicketDetailsInfo/5582e152-8024-44ad-acda-a24700c001dc\",\"type\":\"ticket details\",\"attributes\":[{\"label\":\"TicketID\",\"value\":\"5582e152-8024-44ad-acda-a24700c001dc\",\"decoration\":\"false\",\"display\":true},{\"label\":\"TicketCode\",\"value\":\"TIC0087168\",\"decoration\":\"false\",\"display\":true},{\"label\":\"Ticket Name\",\"value\":\"Asset - Add to Contract\",\"decoration\":\"false\",\"display\":true},{\"label\":\"Status\",\"value\":\"C\",\"decoration\":\"false\",\"display\":true},{\"label\":\"Assigned To\",\"value\":\"Jennifer Babcock\",\"decoration\":\"true\"},{\"label\":\"LastUpdated On\",\"value\":\"3/3/2008 3:01:20 PM\",\"decoration\":\"true\",\"display\":true}]}],\"type\":\"list\"}";
    }

}