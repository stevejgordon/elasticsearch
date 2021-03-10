/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */
package org.elasticsearch.xpack.ml.rest.calendar;

import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.action.RestStatusToXContentListener;
import org.elasticsearch.xpack.core.action.util.PageParams;
import org.elasticsearch.xpack.core.ml.action.GetCalendarsAction;
import org.elasticsearch.xpack.core.ml.calendars.Calendar;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.elasticsearch.rest.RestRequest.Method.GET;
import static org.elasticsearch.rest.RestRequest.Method.POST;
import static org.elasticsearch.xpack.ml.MachineLearning.BASE_PATH;
import static org.elasticsearch.xpack.ml.MachineLearning.PRE_V7_BASE_PATH;

public class RestGetCalendarsAction extends BaseRestHandler {

    @Override
    public List<Route> routes() {
        return Collections.emptyList();
    }

    @Override
    public List<ReplacedRoute> replacedRoutes() {
        // TODO: remove deprecated endpoint in 8.0.0
        return org.elasticsearch.common.collect.List.of(
            new ReplacedRoute(GET, BASE_PATH + "calendars/{" + Calendar.ID + "}",
                GET, PRE_V7_BASE_PATH + "calendars/{" + Calendar.ID + "}"),
            new ReplacedRoute(GET, BASE_PATH + "calendars/",
                GET, PRE_V7_BASE_PATH + "calendars/"),
            new ReplacedRoute(POST, BASE_PATH + "calendars/{" + Calendar.ID + "}",
                POST, PRE_V7_BASE_PATH + "calendars/{" + Calendar.ID + "}"),
            new ReplacedRoute(POST, BASE_PATH + "calendars/",
                POST, PRE_V7_BASE_PATH + "calendars/")
        );
    }

    @Override
    public String getName() {
        return "ml_get_calendars_action";
    }

    @Override
    protected RestChannelConsumer prepareRequest(RestRequest restRequest, NodeClient client) throws IOException {

        String calendarId = restRequest.param(Calendar.ID.getPreferredName());

        GetCalendarsAction.Request request;
        if (restRequest.hasContentOrSourceParam()) {
            try (XContentParser parser = restRequest.contentOrSourceParamParser()) {
                request = GetCalendarsAction.Request.parseRequest(calendarId, parser);
            }
        } else  {
            request = new GetCalendarsAction.Request();
            if (Strings.isNullOrEmpty(calendarId) == false) {
                request.setCalendarId(calendarId);
            }
            if (restRequest.hasParam(PageParams.FROM.getPreferredName()) || restRequest.hasParam(PageParams.SIZE.getPreferredName())) {
                request.setPageParams(new PageParams(restRequest.paramAsInt(PageParams.FROM.getPreferredName(), PageParams.DEFAULT_FROM),
                        restRequest.paramAsInt(PageParams.SIZE.getPreferredName(), PageParams.DEFAULT_SIZE)));
            }
        }

        return channel -> client.execute(GetCalendarsAction.INSTANCE, request, new RestStatusToXContentListener<>(channel));
    }
}
