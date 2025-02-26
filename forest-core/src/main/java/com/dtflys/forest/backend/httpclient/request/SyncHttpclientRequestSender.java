package com.dtflys.forest.backend.httpclient.request;

import com.dtflys.forest.backend.AbstractHttpExecutor;
import com.dtflys.forest.backend.httpclient.conn.ForestSSLConnectionFactory;
import com.dtflys.forest.backend.httpclient.conn.HttpclientConnectionManager;
import com.dtflys.forest.backend.httpclient.response.HttpclientForestResponseFactory;
import com.dtflys.forest.backend.httpclient.response.HttpclientResponseHandler;
import com.dtflys.forest.exceptions.ForestNetworkException;
import com.dtflys.forest.exceptions.ForestRetryException;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import com.dtflys.forest.handler.LifeCycleHandler;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.http.ForestResponseFactory;
import com.dtflys.forest.logging.LogConfiguration;
import com.dtflys.forest.logging.ForestLogHandler;
import com.dtflys.forest.logging.ResponseLogMessage;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.*;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.Date;

/**
 * @author gongjun[jun.gong@thebeastshop.com]
 * @since 2017-05-19 20:16
 */
public class SyncHttpclientRequestSender extends AbstractHttpclientRequestSender {

    private HttpClient client;

    public SyncHttpclientRequestSender(HttpclientConnectionManager connectionManager, ForestRequest request) {
        super(connectionManager, request);
    }

    protected HttpClient getHttpClient(CookieStore cookieStore) {
        HttpClient client = connectionManager.getHttpClient(request, cookieStore);
        setupHttpClient(client);
        return client;
    }

    protected void setupHttpClient(HttpClient client) {
    }



    public void logResponse(ForestResponse response) {
        LogConfiguration logConfiguration = request.getLogConfiguration();
        if (!logConfiguration.isLogEnabled() || response.isLogged()) {
            return;
        }
        response.setLogged(true);
        ResponseLogMessage logMessage = new ResponseLogMessage(response, response.getStatusCode());
        ForestLogHandler logHandler = logConfiguration.getLogHandler();
        if (logHandler != null) {
            if (logConfiguration.isLogResponseStatus()) {
                logHandler.logResponseStatus(logMessage);
            }
            if (logConfiguration.isLogResponseContent()) {
                logHandler.logResponseContent(logMessage);
            }
        }
    }


    @Override
    public void sendRequest(
            ForestRequest request, AbstractHttpExecutor executor,
            HttpclientResponseHandler responseHandler,
            HttpUriRequest httpRequest, LifeCycleHandler lifeCycleHandler,
            CookieStore cookieStore, Date startDate)  {
        HttpResponse httpResponse = null;
        ForestResponse response = null;
        HttpContext httpContext = new BasicHttpContext();
        httpContext.setAttribute("REQUEST", request);
        HttpClientContext httpClientContext = HttpClientContext.adapt(httpContext);
        client = getHttpClient(cookieStore);
        ForestResponseFactory forestResponseFactory = new HttpclientForestResponseFactory();
        try {
            logRequest(request.getCurrentRetryCount(), (HttpRequestBase) httpRequest);
            httpResponse = client.execute(httpRequest, httpClientContext);
        } catch (Throwable e) {
            httpRequest.abort();
            response = forestResponseFactory.createResponse(request, httpResponse, lifeCycleHandler, e, startDate);
            ForestRetryException retryException = new ForestRetryException(
                    e,  request, request.getRetryCount(), request.getCurrentRetryCount());
            try {
                request.canRetry(response, retryException);
            } catch (Throwable throwable) {
                response = forestResponseFactory.createResponse(request, httpResponse, lifeCycleHandler, throwable, startDate);
                lifeCycleHandler.handleSyncWithException(request, response, e);
                return;
            }
            response = forestResponseFactory.createResponse(request, httpResponse, lifeCycleHandler, null, startDate);
            logResponse(response);
            executor.execute(lifeCycleHandler);
            return;
        } finally {
            if (response == null) {
                response = forestResponseFactory.createResponse(request, httpResponse, lifeCycleHandler, null, startDate);
            }
            logResponse(response);
        }
        response = forestResponseFactory.createResponse(request, httpResponse, lifeCycleHandler, null, startDate);
        // 检查是否重试
        ForestRetryException retryEx = request.canRetry(response);
        if (retryEx != null && retryEx.isNeedRetry() && !retryEx.isMaxRetryCountReached()) {
            executor.execute(lifeCycleHandler);
            return;
        }

        // 检查响应是否失败
        if (retryEx == null && response.isError()) {
            ForestNetworkException networkException =
                    new ForestNetworkException("", response.getStatusCode(), response);
            ForestRetryException retryException = new ForestRetryException(
                    networkException,  request, request.getRetryCount(), request.getCurrentRetryCount());
            try {
                request.canRetry(response, retryException);
            } catch (Throwable throwable) {
                responseHandler.handleSync(httpResponse, response);
                return;
            }
            executor.execute(lifeCycleHandler);
            return;
        }


        try {
            lifeCycleHandler.handleSaveCookie(request, getCookiesFromHttpCookieStore(cookieStore));
            responseHandler.handleSync(httpResponse, response);
        } catch (Exception ex) {
            if (ex instanceof ForestRuntimeException) {
                throw ex;
            }
            else {
                throw new ForestRuntimeException(ex);
            }
        }
    }

}
